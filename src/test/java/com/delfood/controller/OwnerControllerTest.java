package com.delfood.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.delfood.dto.OwnerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class OwnerControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	private OwnerDTO owner;
	
	/**
	 * 테스트 실행 전 설정 메서드
	 */
	@Before
	public void setBefore() throws Exception{
		owner = new OwnerDTO();
		owner.setId("id 1");
		owner.setPassword("password 1");
	}
	
	@Test
	public void signInTest() throws Exception {
		ResultActions resultActions =
			mockMvc.perform(post("/owners/ownerInfo")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(objectMapper.writeValueAsString(owner))); // objectMapper 이용하여 owner객체 json 변환 
		
		resultActions.andDo(print())
					.andExpect(status().isOk());
	}

}
