package com.delfood.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
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

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Log4j2
public class MemberControllerTest {
	@Autowired
	private MockMvc mockMvc;
	private MockHttpSession mockSession;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	

	@Test
	public void signUpTest() throws Exception {
		mockMvc.perform(post("/members/signUp")	
				.param("id", "testID")
				.param("password", "testPassword")
				.param("mail", "testMail@test.com")
				.param("name", "testName")
				.param("tel", "010-1234-4567"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	
	@Test
	public void signInTest() throws Exception {
		mockSession = new MockHttpSession();
		mockMvc.perform(post("/members/signIn")
				.param("id", "testID")
				.param("password", "testPassword")
				.session(mockSession))
			.andDo(print())
			.andExpect(status().isOk());
	}

}
