package com.delfood.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import com.delfood.dto.MemberDTO;
import com.delfood.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

/*
 * @SpringBootTest - 통합테스트 환경을 제공하는 spring-boot-test 어노테이션.
 * 		실제 구동되는 어플리케이션과 동일한 어플리케이션 컨텍스트를 제공함.
 * 		대신 어플리케이션의 설정을 모두 로드하기 때문에 성능, 속도면에서 느리다.
 * 		제공되는 의존성 : JUnit, Spring Test, Spring Boot Test, AssertJ, Hamcrest(matcher object), Mockito,
 * 					  JSONassert. JsonPath
 * 
 * @RunWith - JUnit에 내장된 러너 대신 어노테이션에 제공된 러너를 사용한다.
 * 		SpringBootTest를 사용시 이 어노테이션을 같이 사용해야 한다.
 * 
 * @AutoConfigureMockMvc - Mock 테스트시 필요한 의존성을 제공.
 * 		Service에서 호출하는 Bean을 주입해준다.
 * 		간단히 컨트롤러 클래스만 테스트 하고 싶다면 Mockup Test를 사용할 수 있는데
 * 		service 객체에 @MockBean 어노테이션을 적용하는 것으로 이 어노테이션을 대체할 수 있다.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Log4j2
public class MemberControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MockHttpSession mockSession;
	@Autowired
	private ObjectMapper mapper;
	
	

	@Test
	@Transactional // 테스트 종료 후 롤백한다.
	public void signUpTest() throws Exception {
		MemberDTO memberInfo = new MemberDTO();
		memberInfo.setId("testID001");
		memberInfo.setPassword("testPassword001");
		memberInfo.setMail("testMail@mail.com");
		memberInfo.setName("testName");
		memberInfo.setTel("010-1234-1234");
		
		mockMvc.perform(post("/members/")	
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(memberInfo)))
			.andDo(print()) 
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.result", is("success")));
	}

	
	@Test
	public void signInTest() throws Exception {
		MemberDTO memberInfo = new MemberDTO();
		memberInfo.setId("testID");
		memberInfo.setPassword("testPassword");
		
		mockMvc.perform(post("/members/signIn")
				.session(mockSession)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(memberInfo)))
			.andDo(print())
			.andExpect(status().isOk());
		
		memberInfo.setPassword("failPassword");
		mockMvc.perform(post("/members/signIn")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(memberInfo)))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	
	@Test
	public void idDuplTest() throws Exception {
		mockMvc.perform(get("/members/idCheck/testID"))
			.andDo(print())
			.andExpect(jsonPath("$.result", is("duplicated")))
			.andExpect(status().isLocked());
	}
	
	
	@Test
	@Transactional
	public void signUpDuplTest() throws Exception {
		MemberDTO memberInfo = new MemberDTO();
		memberInfo.setId("testID"); // 이미 있는 아이디
		memberInfo.setPassword("testPassword");
		memberInfo.setMail("testMail@mail.com");
		memberInfo.setName("testName");
		memberInfo.setTel("010-1234-1234");
		
		mockMvc.perform(post("/members/")	
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(memberInfo)))
			.andDo(print()) 
			.andExpect(status().isLocked())
			.andExpect(jsonPath("$.result", is("id_duplicated")));
	}
	
	@Test
	@Transactional
	public void passwordUpdateTest() throws Exception{
		Map<String, Object> sessionAttr = new HashMap<>();
		sessionAttr.put("LOGIN_MEMBER_ID", "testID");
		mockMvc.perform(put("/members/password")
				.param("password", "updatePassword")
				.sessionAttrs(sessionAttr))
		.andDo(print())
		.andExpect(jsonPath("$.result", is("success")))
		.andExpect(status().isOk());
	}
	
	
	
	
	
}
