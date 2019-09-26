package com.delfood.controller;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<MemberDTO> memberInfo(HttpSession session){
		ResponseEntity<MemberDTO> responseEntity = null;
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			responseEntity =  new ResponseEntity<MemberDTO>(HttpStatus.UNAUTHORIZED) ;
		}else {
			MemberDTO memberInfo = memberService.getMemberInfo(id);
			responseEntity =  new ResponseEntity<MemberDTO>(memberInfo, HttpStatus.OK) ;
		}
		return responseEntity;
	}
	
	/**
	 * 회원가입 시 아이디의 중복체크를 진행한다.
	 * 아이디 중복체크는 회원가입 아이디 입력 후, 회원가입 요청시 두번 진행한다.
	 * 아이디 중복체크를 한 후 회원가입 버튼을 누를 때 까지 동일한 아이디로 누군가 가입한다면 PK Error가 발생되고
	 * 실제로 회원가입이 진행되지 않을 수 있기 때문에 회원가입을 눌렀을 때 한번 더 실행하는 것이 좋다.
	 * @param id
	 * @return
	 */
	@GetMapping("idCheck/{id}")
	public ResponseEntity<MemberIdDuplResponse> idCheck(@PathVariable String id) {
		ResponseEntity<MemberIdDuplResponse> responseEntity = null;
		MemberIdDuplResponse duplResponse;
		if(memberService.checkIdDuplicated(id)) {
			// 아이디가 중복되어있을 때
			duplResponse = MemberIdDuplResponse.duplicated();
			responseEntity = new ResponseEntity<>(duplResponse, HttpStatus.LOCKED);
		}else {
			// 아이디가 중복되어있지 않을 때
			duplResponse = MemberIdDuplResponse.success();
			responseEntity = new ResponseEntity<>(duplResponse, HttpStatus.OK);
		}
		return responseEntity;
	}
	
	
	/**
	 * 고객이 입력한 정보로 회원가입을 진행한다.
	 * 회원가입 요청을 보내기 전 먼저 ID 중복체크를 진행한다
	 * @param memberInfo
	 * @return 
	 * @throws NoSuchAlgorithmException 
	 *  
	 */
	@PostMapping
	public ResponseEntity<SignUpResponse> signUp(@RequestBody MemberDTO memberInfo) throws NoSuchAlgorithmException {
		ResponseEntity<SignUpResponse> responseEntity = null;
		SignUpResponse result;
		
		String id = memberInfo.getId();
		boolean checkIdDuplicated = memberService.checkIdDuplicated(id);
		if(checkIdDuplicated) {
			// 중복 아이디일 때
			result = SignUpResponse.idDuplicated();
			responseEntity = new ResponseEntity<SignUpResponse>(result, HttpStatus.LOCKED);
		}else {
			// 중복 아이디가 아닐 때
			boolean insertResponse = memberService.insertMember(memberInfo);
			if(insertResponse) {
				result = SignUpResponse.success();
				responseEntity = new ResponseEntity<SignUpResponse>(result, HttpStatus.CREATED);
			}else {
				result = SignUpResponse.error();
				responseEntity = new ResponseEntity<SignUpResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return responseEntity;
	}
	
	/*
	 * success : 로그인 성공
	 * fail : id 또는 password가 일치하지 않음
	 * deleted : 삭제된 아이디(로그인 불가)
	 * error : 그 밖에 오류가 난 경우
	 */
	@PostMapping("signIn")
	public ResponseEntity<SignInResponse> signIn(@RequestBody MemberDTO memberDTO, HttpSession session) throws NoSuchAlgorithmException {
		ResponseEntity<SignInResponse> responseEntity = null;
		String id = memberDTO.getId();
		String password = memberDTO.getPassword();
		SignInResponse loginResponse;
		MemberDTO memberInfo = memberService.signIn(id, password);
		
		if(memberInfo == null) {
			// id 또는 password 확인 필요(DB에서 가져온 정보가 없을 때)
			loginResponse = SignInResponse.fail();
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
		}else if("default".equals(memberInfo.getStatus())) {
			// 성공시 세션에 ID를 저장
			loginResponse = new SignInResponse(SignInResponse.LogInStatus.success, memberInfo);
			session.setAttribute("LOGIN_MEMBER_ID", memberInfo.getId());
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.OK);
		}else if("deleted".equals(memberInfo.getStatus())) {
			// 삭제된 경우
			loginResponse = SignInResponse.deleted();
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
		}else {
			// 예상하지 못한 오류일 경우
			loginResponse = SignInResponse.error();
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return responseEntity;
	}
	
	/**
	 * 회원 비밀번호 변경
	 * @param session
	 * @return
	 */
	@PutMapping("password")
	public ResponseEntity<UpdateMemberPasswordResponse> updateMemberInfo(HttpSession session, String password) {
		ResponseEntity<UpdateMemberPasswordResponse> responseEntity = null;
		UpdateMemberPasswordResponse updateResponse;
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			updateResponse = UpdateMemberPasswordResponse.noLogin();
			responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.UNAUTHORIZED);
		}else if(password == null){
			updateResponse = UpdateMemberPasswordResponse.emptyPassword();
			responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.BAD_REQUEST);
		}else {
			boolean result = memberService.updateMemberPassword(id, password);
			if(result) {
				updateResponse = UpdateMemberPasswordResponse.success();
				responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.OK);
			}else {
				updateResponse = UpdateMemberPasswordResponse.error();
				responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return responseEntity;
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
	public ResponseEntity<DeleteMemberResponse> deleteMemberInfo(HttpSession session) {
		ResponseEntity<DeleteMemberResponse> responseEntity = null;
		DeleteMemberResponse deleteResponse;
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		if(id == null) {
			deleteResponse= DeleteMemberResponse.noLogin();
			responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.UNAUTHORIZED);
		}else {
			if(memberService.deleteMember(id)) {
				deleteResponse = DeleteMemberResponse.success();
				responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.OK);
			}else {
				deleteResponse = DeleteMemberResponse.error();
				responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return responseEntity;
	}
	
	
	
	// -------------- response 객체 --------------
	
	@Getter @AllArgsConstructor @RequiredArgsConstructor
	private static class SignInResponse{
		enum LogInStatus{success, fail, deleted, error}
		@NonNull
		private LogInStatus result;
		private MemberDTO memberInfo;
		
		// success의 경우 memberInfo의 값을 set해줘야 하기 때문에 new 하도록 해준다.
		
		private static final SignInResponse FAIL = new SignInResponse(LogInStatus.fail);
		private static final SignInResponse DELETED = new SignInResponse(LogInStatus.deleted);
		private static final SignInResponse ERROR = new SignInResponse(LogInStatus.error);
		
		public static final SignInResponse fail() {return FAIL;}
		public static final SignInResponse deleted() {return DELETED;}
		public static final SignInResponse error() {return ERROR;}
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class SignUpResponse{
		enum SignUpStatus{success, id_duplicated, error}
		@NonNull
		private SignUpStatus result;
		
		private static final SignUpResponse SUCCESS = new SignUpResponse(SignUpStatus.success);
		private static final SignUpResponse ID_DUPLICATED = new SignUpResponse(SignUpStatus.id_duplicated);
		private static final SignUpResponse ERROR = new SignUpResponse(SignUpStatus.error);
		
		public static final SignUpResponse success() {return SUCCESS;}
		public static final SignUpResponse idDuplicated() {return ID_DUPLICATED;}
		public static final SignUpResponse error() {return ERROR;}
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class MemberIdDuplResponse{
		enum DuplStatus{success, duplicated, error}
		@NonNull
		private DuplStatus result;
		
		private static final MemberIdDuplResponse SUCCESS = new MemberIdDuplResponse(DuplStatus.success);
		private static final MemberIdDuplResponse DUPLICATED = new MemberIdDuplResponse(DuplStatus.duplicated);
		private static final MemberIdDuplResponse ERROR = new MemberIdDuplResponse(DuplStatus.error);
		
		public static final MemberIdDuplResponse success() {return SUCCESS;}
		public static final MemberIdDuplResponse duplicated() {return DUPLICATED;}
		public static final MemberIdDuplResponse error() {return ERROR;}
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class UpdateMemberPasswordResponse{
		enum UpdateStatus{success, no_login, empty_password, error}
		@NonNull
		private UpdateStatus result;
		
		private static final UpdateMemberPasswordResponse SUCCESS = new UpdateMemberPasswordResponse(UpdateStatus.success);
		private static final UpdateMemberPasswordResponse NO_LOGIN = new UpdateMemberPasswordResponse(UpdateStatus.success);
		private static final UpdateMemberPasswordResponse EMPTY_PASSWORD = new UpdateMemberPasswordResponse(UpdateStatus.success);
		private static final UpdateMemberPasswordResponse ERROR = new UpdateMemberPasswordResponse(UpdateStatus.success);
		
		public static final UpdateMemberPasswordResponse success() {return SUCCESS;}
		public static final UpdateMemberPasswordResponse noLogin() {return NO_LOGIN;}
		public static final UpdateMemberPasswordResponse emptyPassword() {return EMPTY_PASSWORD;}
		public static final UpdateMemberPasswordResponse error() {return ERROR;}
	}
	
	@Getter @RequiredArgsConstructor
	private static class DeleteMemberResponse{
		enum DeleteStatus{success, no_login, error}
		@NonNull
		private DeleteStatus result;
		
		private static final DeleteMemberResponse SUCCESS = new DeleteMemberResponse(DeleteStatus.success);
		private static final DeleteMemberResponse NO_LOGIN = new DeleteMemberResponse(DeleteStatus.no_login);
		private static final DeleteMemberResponse ERROR = new DeleteMemberResponse(DeleteStatus.error);
		
		public static final DeleteMemberResponse success() {return SUCCESS;}
		public static final DeleteMemberResponse noLogin() {return NO_LOGIN;}
		public static final DeleteMemberResponse error() {return ERROR;}
	}
}
