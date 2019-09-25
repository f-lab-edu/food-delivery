package com.delfood.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delfood.dto.MemberDTO;
import com.delfood.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RestController
@RequestMapping("/members/")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	/**
	 * 로그인한 사용자가 마이페이지를 눌렀을 때 보여줄 사용자 정보를 반환한다.
	 * @param session
	 * @return MemberDTO
	 */
	@GetMapping("myInfo")
	public MemberDTO memberInfo(HttpSession session, HttpServletResponse httpResponse){
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
		MemberDTO memberInfo = memberService.getMemberInfo(id);
		return memberInfo;
	}
	
	/**
	 * 회원가입 시 아이디의 중복체크를 진행한다.
	 * 아이디 중복체크는 회원가입 아이디 입력 후, 회원가입 요청시 두번 진행한다.
	 * 아이디 중복체크를 한 후 회원가입 버튼을 누를 때 까지 동일한 아이디로 누군가 가입한다면 PK Error가 발생되고
	 * 실제로 회원가입이 진행되지 않을 수 있기 때문에 회원가입을 눌렀을 때 한번 더 실행하는 것이 좋다.
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public MemberIdDuplResponse idCheck(@PathVariable String id, HttpServletResponse httpResponse) {
		MemberIdDuplResponse duplResponse;
		if(memberService.checkIdDuplicated(id)) {
			// 아이디가 중복되어있을 때
			duplResponse = new MemberIdDuplResponse(MemberIdDuplResponse.DuplStatus.duplicated);
			httpResponse.setStatus(HttpStatus.LOCKED.value());
		}else {
			// 아이디가 중복되어있지 않을 때
			duplResponse = new MemberIdDuplResponse(MemberIdDuplResponse.DuplStatus.success);
		}
		return duplResponse;
	}
	
	
	/**
	 * 고객이 입력한 정보로 회원가입을 진행한다.
	 * 회원가입 요청을 보내기 전 먼저 ID 중복체크를 진행한다
	 * @param memberInfo
	 * @return 
	 *  
	 */
	@PostMapping
	public SignUpResponse signUp(@RequestBody MemberDTO memberInfo, HttpServletResponse httpResponse) {
		SignUpResponse result;
		
		String id = memberInfo.getId();
		boolean checkIdDuplicated = memberService.checkIdDuplicated(id);
		if(checkIdDuplicated) {
			// 중복 아이디일 때
			result = new SignUpResponse(SignUpResponse.SignUpStatus.id_duplicated);
			httpResponse.setStatus(HttpStatus.LOCKED.value());
		}else {
			// 중복 아이디가 아닐 때
			boolean insertResponse = memberService.insertMember(memberInfo);
			if(insertResponse) {
				result = new SignUpResponse(SignUpResponse.SignUpStatus.success);
			}else {
				result = new SignUpResponse(SignUpResponse.SignUpStatus.error);
				httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}
		return result;
	}
	
	/*
	 * success : 로그인 성공
	 * fail : id 또는 password가 일치하지 않음
	 * deleted : 삭제된 아이디(로그인 불가)
	 * error : 그 밖에 오류가 난 경우
	 */
	@PostMapping("signIn")
	public SignInResponse signIn(@RequestBody MemberDTO memberDTO, HttpSession session, HttpServletResponse httpResponse) {
		String id = memberDTO.getId();
		String password = memberDTO.getPassword();
		SignInResponse loginResponse;
		MemberDTO memberInfo = memberService.signIn(id, password);
		if(memberInfo == null) {
			// id 또는 password 확인 필요(DB에서 가져온 정보가 없을 때)
			loginResponse = new SignInResponse(SignInResponse.LogInStatus.fail);
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		}else if("default".equals(memberInfo.getStatus())) {
			// 성공시 세션에 ID를 저장
			loginResponse = new SignInResponse(SignInResponse.LogInStatus.success, memberInfo);
			session.setAttribute("LOGIN_MEMBER_ID", memberInfo.getId());
		}else if("deleted".equals(memberInfo.getStatus())) {
			// 삭제된 경우
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			loginResponse = new SignInResponse(SignInResponse.LogInStatus.deleted);
		}else {
			// 예상하지 못한 오류일 경우
			httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			loginResponse = new SignInResponse(SignInResponse.LogInStatus.error);
		}
		return loginResponse;
	}
	
	/**
	 * 회원 비밀번호 변경
	 * @param session
	 * @return
	 */
	@PutMapping("myInfo")
	public UpdateMemberPasswordResponse updateMemberInfo(HttpSession session, String password, HttpServletResponse response) {
		UpdateMemberPasswordResponse updateResponse;
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			updateResponse = new UpdateMemberPasswordResponse(UpdateMemberPasswordResponse.UpdateStatus.no_login);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}else if(password == null){
			updateResponse = new UpdateMemberPasswordResponse(UpdateMemberPasswordResponse.UpdateStatus.empty_password);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}else {
			boolean result = memberService.updateMemberPassword(id, password);
			if(result) {
				updateResponse = new UpdateMemberPasswordResponse(UpdateMemberPasswordResponse.UpdateStatus.success);
			}else {
				updateResponse = new UpdateMemberPasswordResponse(UpdateMemberPasswordResponse.UpdateStatus.error);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}
		
		return updateResponse;
	}
	
	/**
	 * 회원을 삭제 상태로 변환시킨다.
	 * delete 쿼리가 아닌 update 쿼리가 진행된다.
	 * MEMBER 테이블의 status 컬럼을 delete로 바꾸어 비활성 상태로 만든다.
	 * @param session
	 * @param httpResponse
	 * @return
	 */
	@DeleteMapping("myInfo")
	public DeleteMemberResponse deleteMemberInfo(HttpSession session, HttpServletResponse httpResponse) {
		DeleteMemberResponse deleteResponse;
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			deleteResponse = new DeleteMemberResponse(DeleteMemberResponse.DeleteStatus.no_login);
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		}else {
			if(memberService.deleteMember(id)) {
				deleteResponse = new DeleteMemberResponse(DeleteMemberResponse.DeleteStatus.success);
			}else {
				deleteResponse = new DeleteMemberResponse(DeleteMemberResponse.DeleteStatus.error);
				httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}
		return deleteResponse;
	}
	
	
	
	// -------------- response 객체 --------------
	
	@Getter @Setter @ToString @AllArgsConstructor @RequiredArgsConstructor
	private static class SignInResponse{
		enum LogInStatus{success, fail, deleted, error}
		@NonNull
		private LogInStatus result;
		private MemberDTO memberInfo;
	}
	
	@Getter @Setter @ToString @RequiredArgsConstructor
	private static class SignUpResponse{
		enum SignUpStatus{success, id_duplicated, error}
		@NonNull
		private SignUpStatus result;
	}
	
	@Getter @Setter @ToString @RequiredArgsConstructor
	private static class MemberIdDuplResponse{
		enum DuplStatus{success, duplicated, error}
		@NonNull
		private DuplStatus result;
	}
	
	@Getter @Setter @ToString @RequiredArgsConstructor
	private static class UpdateMemberPasswordResponse{
		enum UpdateStatus{success, no_login, empty_password, error}
		@NonNull
		private UpdateStatus result;
	}
	
	@Getter @Setter @ToString @RequiredArgsConstructor
	private static class DeleteMemberResponse{
		enum DeleteStatus{success, no_login, error}
		@NonNull
		private DeleteStatus result;
	}
}
