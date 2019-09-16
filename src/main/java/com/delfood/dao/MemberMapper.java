package com.delfood.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.delfood.vo.MemberVO;

@Repository
public interface MemberMapper {
	public List<MemberVO> getAll();
}
