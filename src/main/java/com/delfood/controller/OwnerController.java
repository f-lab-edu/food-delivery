package com.delfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.OwnerMapper;
import com.delfood.service.OwnerService;

@RestController
@Service
@RequestMapping("/owner/")
public class OwnerController {
	
	@Autowired
	private OwnerService ownerService;
	
	@PostMapping("signIn")
	public OwnerDTO signIn(String id, String password) {
		return ownerService.getOwnerInfo(id, password);
	}
	
	
}
