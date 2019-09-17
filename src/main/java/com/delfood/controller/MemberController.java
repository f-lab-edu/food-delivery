package com.delfood.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.MemberDTO;
import com.delfood.service.MemberService;

@RestController
@RequestMapping("/members/")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@GetMapping("{id}")
	public Map<String, Object> memberInfo(@PathVariable("id") String id){
		Map<String, String> params = new HashMap<>();
		params.put("id", id);
		MemberDTO memberInfo = memberService.findById(params);
		
		Map<String, Object> result = new HashMap<>();
		if(memberInfo == null) {
			result.put("flag", "false");
		}else {
			result.put("flag", "true");
			result.put("memberInfo", memberInfo);
		}
		
		return result;
	}
}
