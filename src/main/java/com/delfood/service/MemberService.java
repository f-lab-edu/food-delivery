package com.delfood.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dto.MemberDTO;
import com.delfood.mapper.MemberMapper;

@Service
public class MemberService {
	@Autowired
	private MemberMapper memberMapper;
	
	public MemberDTO getMemberInfo(String memberId) {
		return memberMapper.findById(memberId);
	}
	
	
}
