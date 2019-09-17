package com.delfood.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dao.MemberDao;
import com.delfood.dto.MemberDTO;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;
	
	public MemberDTO findById(Map<String, String> params) {
		return memberDao.findById(params);
	}
	
	
}
