package com.delfood.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.service.MemberService;
import com.delfood.vo.MemberVO;

@RestController
@RequestMapping("/member/")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@GetMapping("list")
	public List<MemberVO> list() {
		return memberService.getAll();
	}
}
