package lm.swith.studyroom.model;

import java.util.Date;

import lombok.*;

@Getter
@Setter
public class ChatMessage {
	
	private Long message_no;
	private Long room_no;
	private Long user_no;
	private String content;
	private Date datetime;
}