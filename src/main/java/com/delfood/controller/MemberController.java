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
	
	/**
	 * 로그인한 사용자가 마이페이지를 눌렀을 때 보여줄 사용자 정보를 반환한다.
	 * @param session
	 * @return MemberDTO
	 */
	@GetMapping("myInfo")
	public MemberDTO memberInfo(HttpSession session){
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		MemberDTO memberInfo = memberService.getMemberInfo(id);
		return memberInfo;
	}
	
}
