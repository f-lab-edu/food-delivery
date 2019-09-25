package com.delfood.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.OwnerDTO;
import com.delfood.service.OwnerService;

@RestController
@Service
@RequestMapping("/owners/")
public class OwnerController {
	
	@Autowired
	private OwnerService ownerService;

	/**
	 * 사장 정보 조회
	 * 
	 * 아이디와 패스워드를 받아 사장 정보를 리턴합니다.
	 * 
	 * @param ownerIdPwd
	 * @return OwnerDTO
	 */
	@PostMapping("ownerInfo")
	public OwnerDTO ownerInfo(@RequestBody OwnerDTO ownerIdPwd) {
		String id = ownerIdPwd.getId();
		String password = ownerIdPwd.getPassword();
		return ownerService.getOwnerInfo(id, password);
	}
	
	
}
