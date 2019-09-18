package com.delfood.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.delfood.dto.MemberDTO;

@Repository
public interface MemberMapper {

	MemberDTO getMemberInfo(String memberId);

}
