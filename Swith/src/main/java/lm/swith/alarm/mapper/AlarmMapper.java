package lm.swith.alarm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import lm.swith.alarm.model.Alarm;

@Mapper
public interface AlarmMapper {

	void insertAlarm(Alarm alarm); 
	
	Alarm getAlarmByUserNo(Long user_no);
	
	boolean  AlarmByData(Long user_no, Long post_no, String AlarmByData);
	
	void deleteAlarm(Long alarm_no);
}