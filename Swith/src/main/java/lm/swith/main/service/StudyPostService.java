package lm.swith.main.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lm.swith.alarm.mapper.AlarmMapper;
import lm.swith.alarm.model.Alarm;
import lm.swith.main.mapper.StudyPostMapper;
import lm.swith.main.model.Cafes;
import lm.swith.main.model.Comments;
import lm.swith.main.model.Likes;
import lm.swith.main.model.PostTechStacks;
import lm.swith.main.model.StudyApplication;
import lm.swith.main.model.StudyPost;
import lm.swith.user.mapper.UsersMapper;
import lm.swith.user.model.SwithUser;
@Service
public class StudyPostService {
	  @Autowired
	  private final StudyPostMapper studyPostMapper;
	  @Autowired
	  private final AlarmMapper alarmMapper;

	  @Autowired
	  private final UsersMapper usersMapper;

	  @Autowired
	  public StudyPostService(StudyPostMapper studyPostMapper,  AlarmMapper alarmMapper, UsersMapper usersMapper) {
	      this.studyPostMapper = studyPostMapper;
	  this.alarmMapper = alarmMapper;
	  this.usersMapper = usersMapper;
	  }
	// Main Part
    
    // 스터디 등록하기
    @Transactional
    public void insertStudyPost(StudyPost studyPost) {
        try {
            // StudyPost 삽입
        	studyPost.setStudy_period(studyPost.getStudy_period() + "개월");
            studyPostMapper.insertStudyPost(studyPost);
            

            // PostTechStacks 삽입
            System.out.println("Original skill_no list: " + studyPost.getSkills());
            List<Long> postTechStacksList = studyPost.getSkills();
            
            System.out.println("postTechStacksList size: " + postTechStacksList.size());
            
            for (Long skill_no : postTechStacksList) {
                System.out.println("Current skill_no: " + skill_no);
                PostTechStacks postTechStacks = new PostTechStacks();
                postTechStacks.setPost_no(studyPost.getPost_no());
                postTechStacks.setSkill_no(skill_no);
                postTechStacks.setUser_no(studyPost.getUser_no());
                System.out.println("PostTechStacks skill_no: " + postTechStacks.getSkill_no());
         
                // PostTechStacks를 삽입
                studyPostMapper.insertPostTechStacks(postTechStacks);
            }

            // StudyApplication 삽입
            StudyApplication studyApplication = new StudyApplication();
            studyApplication.setPost_no(studyPost.getPost_no());
            studyApplication.setUser_no(studyPost.getUser_no());
            studyApplication.setMax_study_applicants(studyPost.getMax_study_applicants());
            studyPostMapper.insertStudyApplication(studyApplication);
            //System.out.println("getMax_study_applicants" + studyApplication.getMax_study_applicants());
        } catch (Exception e) {
            // 롤백 여부 확인을 위해 예외 발생
            throw new RuntimeException("Transaction rolled back", e);
        }
    }
    
   
    
    // 스터디 게시글 작성 내 첫모임 장소 카페 리스트
    public List<Cafes> getAllCafes(String bplcnm, String sitewhladdr, String x, String y) {
        return studyPostMapper.getAllCafes(bplcnm, sitewhladdr, x, y);
    }
	
	// 스터디 목록 불러오기	
    public List<StudyPost> getAllStudyPostWithSkills() {
        return studyPostMapper.getAllStudyPostWithSkills();
    }
    

    // 스터디 조건 검색
    public List<StudyPost> getStudiesBySelect(List<Long> skill_no,String recruit_type,String study_method,String study_location) {
	    Map<String, Object> params = new HashMap<>();
	    // skill_no가 비어있지 않은 경우에만 파라미터로 추가
	   
	        params.put("skill_no", skill_no);
	    
	   
	    params.put("recruit_type", recruit_type);
	    params.put("study_method", study_method);
	    params.put("study_location", study_location);
    	return studyPostMapper.getStudiesBySelect(params);
    }
    
    // 스터디 키워드 검색
    public List<StudyPost> getStudiesByKeyword(String keyword) {
        return studyPostMapper.getStudiesByKeyword(keyword);}
    
    // 마감기한 지난 스터디 상태 변경
    @Transactional
    public void updateStudyStatus() {
        List<StudyPost> expiredPosts = studyPostMapper.findExpiredStudyStatus();
      Alarm alarm = new Alarm(); // 알람 데이터 넣어줄 객체
       List<StudyApplication> userNumber; // 보류 (user_no는 보내줄 사람) (신청한 사람들)
        SwithUser userNickName;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        LocalDateTime deadLine;
        int compare = 0;
        for (StudyPost post : expiredPosts) {
        	deadLine = LocalDateTime.parse(post.getRecruit_deadline(), formatter);
        	compare = deadLine.compareTo(now);
           // 마감기한 지난 스터디의 신청자 찾기
         userNumber = studyPostMapper.getAllApplicantsByPostNoStudyRoom(post.getPost_no()); 
         for(StudyApplication stap : userNumber) { 
          alarm.setPost_no(post.getPost_no()); // postNo주입
          alarm.setAlarm_message(post.getStudy_title() + "의 " + post.getRecruit_deadline() + "이 지나 스터디방이 활성화 됩니다.");
          alarm.setUser_no(stap.getUser_no()); // 신청자의
         alarmMapper.insertAlarm(alarm);
          }
          
          
           // 상태를 업데이트하는 작업 수행
         if (compare == 0) {
           studyPostMapper.updateStudyStatus();
         }
        }
   }
    
    
    
    // 자정마다 마감기한 지난 스터디 상태 변경 실행 -> 백에서 자동 실행이기 때문에 controller 필요x
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void runUpdateStudyStatus() {
    	updateStudyStatus();
    }
    	
    
    
    // MyPage Part
    // 내가 쓴 스터디 목록
    public List<StudyPost> getOwnStudiesWithUserNo(Long user_no) {
    	SwithUser users = studyPostMapper.getUserByUserNo(user_no);
    	return studyPostMapper.getOwnStudiesWithUserNo(user_no);
    }
    
    // 찜한 스터디 목록
    public List<StudyPost> getAllStudiesWithLikes(Long user_no) {
    	SwithUser users = studyPostMapper.getUserByUserNo(user_no);
    	return studyPostMapper.getAllStudiesWithLikes(user_no);
    }
    
    // 내가 참여한 스터디 목록
    public List<StudyPost> getAllStudiesWithUserNo(Long user_no) {
    	SwithUser users = studyPostMapper.getUserByUserNo(user_no);
    	return studyPostMapper.getAllStudiesWithUserNo(user_no);
    }
    
    
    
    
    // Detail Part
    // 스터디 상세 페이지 불러오기
    public StudyPost getStudyPostByPostNo(Long post_no) {
        StudyPost studyPost = studyPostMapper.getStudyPostByPostNo(post_no);

        if (studyPost != null) {
            List<Comments> comments = studyPostMapper.getCommentsByPostNo(post_no);
            studyPost.setComments(comments);
        }

        return studyPost;
    }
    
    // 스터디 수정
    @Transactional
    public void updateStudyPost(StudyPost studyPost) {
    	try {
    		   
        	studyPostMapper.updateStudyPost(studyPost);
        	System.out.println(studyPost);
//    		studyPostMapper.studyApplicationUpdate(studyPost.getUser_no(), studyPost.getPost_no(), studyPost.getMax_study_applicants());
    	  // PostTechStacks 삽입
//        System.out.println("Original skill_no list: " + studyPost.getSkills());
//        List<Long> postTechStacksList = studyPost.getSkills();
//        
//        System.out.println("postTechStacksList size: " + postTechStacksList.size());
//        
//        for (Long skill_no : postTechStacksList) {
//            System.out.println("Current skill_no: " + skill_no);
//            PostTechStacks postTechStacks = new PostTechStacks();
//            postTechStacks.setPost_no(studyPost.getPost_no());
//            postTechStacks.setSkill_no(skill_no);
//            System.out.println("PostTechStacks skill_no: " + postTechStacks.getSkill_no());
//            // PostTechStacks를 삭제 후 다시 저장
//            studyPostMapper.deletePostTechStacksUpdate(postTechStacks);
//            
//            studyPostMapper.insertPostTechStacksUpdate(postTechStacks);
//        }

        // StudyApplication 삽입
//        StudyApplication studyApplication = new StudyApplication();
//        studyApplication.setPost_no(studyPost.getPost_no());
//        studyApplication.setUser_no(studyPost.getUser_no());
//     
//        studyPostMapper.upadateStudyApplication(studyApplication);
//        studyPostMapper.updateStudyPostUser(studyPost.getUser_no());
    } catch (Exception e) {
        // 롤백 여부 확인을 위해 예외 발생
        throw new RuntimeException("Transaction rolled back", e);
    }
  	
  }
 
    
    // 스터디 삭제
    @Transactional
    public void deleteStudyPost(Long post_no) {
    	studyPostMapper.deleteComments(post_no);
    	studyPostMapper.deleteStudyApplication(post_no);
    	studyPostMapper.deletePostTechStacks(post_no);
    	studyPostMapper.deleteStudyPost(post_no);
    }
    
 
    
    // 스터디 신청
    public void addUsersByPostNo(Long post_no, Long user_no) {
    	// Alarm 찾기
    	Alarm alarm = new Alarm();
    	// post_no : 게시글 작성자 의 user_no 찾아서 보내주기
    	StudyPost userNumber = studyPostMapper.selectUserNoByPostNo(post_no);
    	alarm.setUser_no(userNumber.getUser_no()); // 게시글 작성자에게 보내줌
    	alarm.setPost_no(post_no); // 어떤 게시물에 대한건지 
    	SwithUser userNickName = usersMapper.findByUserNo(user_no); // user_no를 가진신청자 이름 불F러오기
    	alarm.setAlarm_message(userNickName.getNickname() + "님이 참가 신청 하였습니다."); // 누가 신청했는지 메세지 설정
    	alarmMapper.insertAlarm(alarm);
    	studyPostMapper.addUsersByPostNo(post_no, user_no);
    }
    
    // 스터디 신청자 목록
    public List<StudyApplication> getAllApplicants(Long post_no) {
    	return studyPostMapper.getAllApplicantsByPostNo(post_no);
    }
    
    
    // 승인된 신청자 수를 가져오기
    public List<StudyApplication> getAllApplicants2(Long post_no) {
        List<StudyApplication> studyApplicants = studyPostMapper.getAllApplicantsByPostNo(post_no);
        for (StudyApplication studyApplication : studyApplicants) {
            studyApplication.setAccepted_applicants(studyPostMapper.getAcceptedApplicants(post_no));
        }
        return studyApplicants;
    }
    
    
    
    // 스터디 승인 인원 카운트
    public int getAcceptedApplicants(Long post_no) {
        return studyPostMapper.getAcceptedApplicants(post_no);
    }
    
    // 스터디 최대 인원 조회
    public int getMaxApplicants(Long post_no) {
    	return studyPostMapper.getMaxApplicants(post_no);
    }
    
    
    // 스터디 신청 상태 업데이트 (승인/거절)
  public void updateApplicantsStatus(Long user_no, Long post_no, boolean accept) {
  	Alarm alarm = new Alarm();
  	
  	alarm.setPost_no(post_no);
  	alarm.setUser_no(user_no);
  	StudyPost studyPostInfo = studyPostMapper.selectUserNoByPostNo(post_no);
  	
  	System.out.println("서비스 accept"+ accept);
      try {   
              if (accept) { // accept가 true라면 승인 
                  studyPostMapper.acceptApplicant(post_no, user_no);
                  alarm.setAlarm_message(studyPostInfo.getStudy_title() + "의 참가 신청 되었습니다.");
                  alarmMapper.insertAlarm(alarm);
              } else {
              	studyPostMapper.deleteApplicant(post_no, user_no);
              	alarm.setAlarm_message(studyPostInfo.getStudy_title() + "의 참가 거절 되었습니다.");
              	alarmMapper.insertAlarm(alarm);
              }
          
      } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
      }
  }
    
    
    
    // 스터디 찜 목록
    public List<Likes> isLiked(Long post_no, Long user_no) {
        return studyPostMapper.isLiked(post_no, user_no);
    }
    
    
    
    
    
    // 스터디 찜 업데이트
    public void likesUpdate(Long user_no, Long post_no) {
        Likes likes = new Likes();
        likes.setUser_no(user_no);
        likes.setPost_no(post_no);

        List<Likes> likesList = studyPostMapper.isLiked(post_no, user_no);

        if (!likesList.isEmpty()) {
            studyPostMapper.deleteLikes(post_no, user_no);
        } else {
            studyPostMapper.addLikes(likes);
        }
    }
    
    
    //찜 카운트
    public List<Likes> likesCount(Long post_no) {
        List<Likes> likes = studyPostMapper.getLikesList(); // 알맞은 방법으로 Likes 목록을 가져와야 합니다.

        for (Likes like : likes) {
            like.setLikesCount(studyPostMapper.likesCount(post_no));
        }

        return likes;
    }
  
   

    // 스터디 게시글 작성 내 첫모임 장소 검색
    public List<Cafes> searchCafes(String keyword) {
        return studyPostMapper.searchCafes(keyword);
    }
   
  
    // Comments Part
    // 댓글 등록
    public void insertComment(Comments comments) {
    	studyPostMapper.insertComment(comments);
    }
    
    // 댓글 불러오기
    public List<Comments> getCommentsByPostNo(Long post_no) {
    	return studyPostMapper.getCommentsByPostNo(post_no);
    }
    
    // 댓글 수정
    public void updateComment(Long post_no, Long user_no , Long comment_no, String comment_content) {
    	studyPostMapper.updateComment(post_no, user_no, comment_no, comment_content);
    }
    
    // 댓글 삭제
    public void deleteComment(Long post_no, Long user_no, Long comment_no) {
    	studyPostMapper.deleteComment(post_no, user_no, comment_no);
    }
    
    // 메인페이지 댓글 갯수
    public List<Comments> getCommentList() {
    	return studyPostMapper.getCommentList();
    	}
    
    
    // Profile Part
    // 유저 프로필 확인 OK
    public SwithUser getUserByUserNo(Long user_no) {
    	SwithUser users = studyPostMapper.getUserByUserNo(user_no);
    	return users;
    }
    
    // Admin Part
    // 닉네임으로 게시글 검색
    public List<StudyPost> getStudiesByNickname(String nickname) {
    	return studyPostMapper.getStudiesByNickname(nickname);
    }
    
    // 닉네임으로 댓글 검색
    public List<Comments> getCommentsByNickname(String nickname) {
    	return studyPostMapper.getCommentsByNickname(nickname);
    }
    
    // 유저 리스트
    public List<SwithUser> getAllUserList(String nickname){
    	return studyPostMapper.getAllUserList(nickname);
    }
    
    // 유저 삭제(탈퇴)
    @Transactional
    public void deleteUser(Long user_no) {
    	studyPostMapper.deleteUserComment(user_no);
    	studyPostMapper.deleteUserStudyApplication(user_no);
    	studyPostMapper.deleteUserPostTechStacks(user_no);
    	studyPostMapper.deleteUserLikes(user_no);
    	studyPostMapper.deleteUserMoment(user_no);
    	studyPostMapper.deleteUserRoomNotice(user_no);
    	studyPostMapper.deleteUserStudyPost(user_no);
    	studyPostMapper.deleteUser(user_no);
    }
    
    private static Calendar convertToLocalDateTimeToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, localDateTime.getYear());
        calendar.set(Calendar.MONTH, localDateTime.getMonthValue() - 1); // Calendar에서는 월이 0부터 시작하므로 1을 빼줍니다.
        calendar.set(Calendar.DAY_OF_MONTH, localDateTime.getDayOfMonth());

        return calendar;
    }
    
}