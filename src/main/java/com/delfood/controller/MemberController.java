package com.delfood.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	/**
	 * 고객이 입력한 정보로 회원가입을 진행한다.
	 * @param memberInfo
	 * @return boolean(회원가입 성공 여부)
	 *  
	 */
	@PostMapping("signUp")
	public boolean signUp(MemberDTO memberInfo) {
		return memberService.insertMember(memberInfo);
	}
	/*
	 * success : 로그인 성공
	 * fail : id 또는 password가 일치하지 않음
	 * deleted : 삭제된 아이디(로그인 불가)
	 * error : 그 밖에 오류가 난 경우
	 */
	@PostMapping("signIn")
	public String signIn(String id, String password, HttpSession session) {
		MemberDTO memberInfo = memberService.signIn(id, password);
		String result;
		if(memberInfo == null) {
			// id 또는 password 확인 필요(DB에서 가져온 정보가 없을 때)
			result = "fail";
		}else if(memberInfo.getStatus().equals("default")) {
			// 로그인 성공시 세션에 회원 id 저장
			result = "success";
			session.setAttribute("LOGIN_MEMBER_ID", memberInfo.getId());
		}else if(memberInfo.getStatus().equals("deleted")) {
			// 삭제된 아이디일 경우
			result = "deleted";
		}else {
			// 예상하지 못한 오류일 경우
			result = "error";
		}
		return result;
	}
	
}
