package com.delfood.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/")
public class MemberController {
	
	@PostMapping("signin")
	public Object signIn() {
		
		return null;
	}
}
