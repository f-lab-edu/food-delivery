package com.delfood.service;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dto.MemberDTO;
import com.delfood.mapper.DMLOperationError;
import com.delfood.mapper.MemberMapper;
import com.delfood.utils.SHA256Util;

@Service
public class MemberService {
	@Autowired
	private MemberMapper memberMapper;
	
	public MemberDTO getMemberInfo(String memberId) {
		return memberMapper.findById(memberId);
	}
	
	/*
	 * - 고객 회원가입 메서드
	 * 비밀번호를 암호화하여 세팅한다.
	 * MyBatis에서 insert return값은 성공시 1이 리턴된다.
	 * return값은 검사하여 null값이면 true, null이 아닐시 insert에 실패한 것이니 false를 반환한다
	 */
	public void insertMember(MemberDTO memberInfo){
		memberInfo.setPassword(SHA256Util.encryptSHA256(memberInfo.getPassword()));
		int insertCount = memberMapper.insertMember(memberInfo);
		
		if(insertCount != 1)
			throw new RuntimeException("insertMember ERROR! 회원가입 메서드를 확인해주세요\n"
					+ "Params : " + memberInfo);
	}
	
	/**
	 * 
	 * @param id
	 * @param password
	 * @return 
	 */
	public MemberDTO login(String id, String password){
		String cryptoPassword = SHA256Util.encryptSHA256(password);
		MemberDTO memberInfo = memberMapper.findByIdAndPassword(id, cryptoPassword);
		return memberInfo;
	}	
	
	/**
	 * 회원가입시 아이디 중복 체크를 진행한다.
	 * @param id
	 * @return
	 * true : 중복된 아이디
	 * false : 중복되지 않은 아이디(생성 가능한 아이디)
	 */
	public boolean isDuplicatedId(String id) {
		return memberMapper.idCheck(id) == 1;
	}
	
	/**
	 * 회원 비밀번호를 변경한다.
	 * @param id
	 * @param password
	 * @return
	 */
	public DMLOperationError updateMemberPassword(String id, String password){
		String cryptoPassword = SHA256Util.encryptSHA256(password);
		int result = memberMapper.updateMemberPassword(id, cryptoPassword);
		if(result == 1) 
			return DMLOperationError.SUCCESS;	// 원하는 1개의 데이터만 수정
		else if(result == 0) 
			return DMLOperationError.NONE_CHANGED; // 데이터가 수정되지 않음. WHERE 조건 확인 필요
		else 
			return DMLOperationError.TOO_MANY_CHANGED; // 데이터가 너무 많이 바뀜. WHERE 조건 확인 필요.
		
	}
	/**
	 * 회원 status를 'DELETED'로 변경한다
	 * @param id
	 * @return
	 */
	public DMLOperationError deleteMember(String id){
		int result = memberMapper.deleteMember(id);
		if(result == 1) 
			return DMLOperationError.SUCCESS;	// 원하는 1개의 데이터만 수정
		else if(result == 0) 
			return DMLOperationError.NONE_CHANGED; // 데이터가 수정되지 않음. WHERE 조건 확인 필요
		else 
			return DMLOperationError.TOO_MANY_CHANGED; // 데이터가 너무 많이 바뀜. WHERE 조건 확인 필요.
	}
	
	/**
	 * 회원 address를 update한다.
	 * @param id
	 * @param address
	 * @param addressDetail
	 * @return
	 */
	public DMLOperationError updateMemberAddress(String id, String address, String addressDetail){
		int result = memberMapper.updateMemberAddress(id, address, addressDetail);
		if(result == 1) 
			return DMLOperationError.SUCCESS;	// 원하는 1개의 데이터만 수정
		else if(result == 0) 
			return DMLOperationError.NONE_CHANGED; // 데이터가 수정되지 않음. WHERE 조건 확인 필요
		else 
			return DMLOperationError.TOO_MANY_CHANGED; // 데이터가 너무 많이 바뀜. WHERE 조건 확인 필요.
	}
	
	
}
