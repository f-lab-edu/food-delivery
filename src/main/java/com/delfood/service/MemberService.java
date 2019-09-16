package com.delfood.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dao.MemberMapper;
import com.delfood.vo.MemberVO;

@Service
public class MemberService {
	@Autowired
	private MemberMapper memberMapper;
	
	/**
	 * DB에 저장된 모든 멤버를
	 * 리스트 형식으로 가져온다.
	 * @return
	 * - 모든 멤버 리스트
	 */
	public List<MemberVO> getAll(){
		return memberMapper.getAll();
	}
}
