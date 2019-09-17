package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dao.MemberDao;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberMapper;
	
	
}
