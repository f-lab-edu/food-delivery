package com.delfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.service.MemberService;

@RestController
@RequestMapping("/members/")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	

}
