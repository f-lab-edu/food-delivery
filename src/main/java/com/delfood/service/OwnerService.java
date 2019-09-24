package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.OwnerMapper;

@RestController
@Service
@RequestMapping("/owner/")
public class OwnerService {

	@Autowired
	private OwnerMapper ownerMapper;
	
	@PostMapping("signIn")
	public OwnerDTO getOwnerInfo(String id, String password) {
		return ownerMapper.findByIdAndPassword(id, password);
	}
}
