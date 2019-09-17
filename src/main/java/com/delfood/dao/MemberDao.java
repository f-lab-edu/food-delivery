package com.delfood.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.delfood.dto.MemberDTO;

@Repository
public interface MemberDao {

	MemberDTO findById(Map<String, String> params);

}
