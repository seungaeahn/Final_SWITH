package lm.swith.studyroom.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Calendar {
	private Long post_no;
	private Long user_no;
	private Long event_no; //SEQ만들기
	private String title;
	private Date start;//선택한 날 값으로 받아서 사용해야함 
}
