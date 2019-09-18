package com.delfood.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.MemberDTO;
import com.delfood.service.MemberService;

@RestController
@RequestMapping("/members/")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@GetMapping("myInfo")
	public MemberDTO memberInfo(HttpSession session){
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		MemberDTO memberInfo = memberService.getMemberInfo(id);
		return memberInfo;
	}
	
}
