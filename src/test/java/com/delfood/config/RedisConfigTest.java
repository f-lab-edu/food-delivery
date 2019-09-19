package com.delfood.config;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.delfood.dto.MemberDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisConfigTest {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	private MemberDTO newMember() {
		MemberDTO member = new MemberDTO();
		
		member.setId("eric");
		member.setPassword("asdfasdf");
		member.setTel("123-1234-1234");
		member.setLastUpdateDate(new Date());
		member.setRegDate(new Date());
		member.setStatus("default");
		member.setLoginType("default");
		member.setMail("yyy9942@naver.com");
		member.setName("eric Jeong");
		member.setStatus("default");
		
		return member;
	}

	@Test
	public void redisTest() {
		redisTemplate.opsForHash().put("member", "1", newMember());

	    Object result = redisTemplate.opsForHash().get("member", "1");
	    
	    // MemberDTO member = (Member) RedisTemplate.opsForHash().get("member", "1");
	    // 위와 같이 형 변환을 통해 바로 이용할 수 없음
	    // java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class 에러
	    // objectMapper.convertValue 를 이용하여 변환
	    
	    MemberDTO member = objectMapper.convertValue(result, MemberDTO.class);
	    log.info("{}", member);

	    assertThat(member).isNotNull();
	    assertThat(member.getId()).isEqualTo("eric");
	    assertThat(member.getName()).isEqualTo("eric Jeong");
	}
	


}
