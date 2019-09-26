package com.delfood.service;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delfood.dto.MemberDTO;
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
	public boolean insertMember(MemberDTO memberInfo) throws NoSuchAlgorithmException {
		memberInfo.setPassword(SHA256Util.encryptSHA256(memberInfo.getPassword()));
		return memberMapper.insertMember(memberInfo) == 1;
	}
	
	/**
	 * 
	 * @param id
	 * @param password
	 * @return 
	 * success : 로그인 성공
	 * fail : id 또는 password가 일치하지 않음
	 * deleted : 삭제된 아이디(로그인 불가)
	 * error : 그 밖에 오류가 난 경우
	 * @throws NoSuchAlgorithmException 
	 */
	public MemberDTO signIn(String id, String password) throws NoSuchAlgorithmException {
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
	public boolean checkIdDuplicated(String id) {
		return memberMapper.findById(id) != null;
	}
	
	/**
	 * 회원 비밀번호를 변경한다.
	 * @param id
	 * @param password
	 * @return
	 * 변경 성공시 true
	 * 변경 실패시 false
	 */
	public boolean updateMemberPassword(String id, String password) {
		return memberMapper.updateMemberPassword(id, password) == 1;
	}
	
	public boolean deleteMember(String id) {
		return memberMapper.deleteMember(id) == 1;
	}
	
	
}
