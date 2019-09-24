package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.OwnerMapper;

@Service
public class OwnerService {

	@Autowired
	private OwnerMapper ownerMapper;
	
	public OwnerDTO getOwnerInfo(String id, String password) {
		return ownerMapper.findByIdAndPassword(id, password);
	}
}
