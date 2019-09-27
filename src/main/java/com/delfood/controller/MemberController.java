package com.delfood.controller;


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
import com.delfood.mapper.MapperDMLResponse;
import com.delfood.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/members/")
@Log4j2
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
		int cnt = memberService.idCheck(id);
		if(cnt != 0) {
			// 아이디가 중복되어있을 때
			duplResponse = MemberIdDuplResponse.DUPLICATED;
			responseEntity = new ResponseEntity<>(duplResponse, HttpStatus.LOCKED);
		}else {
			// 아이디가 중복되어있지 않을 때
			duplResponse = MemberIdDuplResponse.SUCCESS;
			responseEntity = new ResponseEntity<>(duplResponse, HttpStatus.OK);
		}
		return responseEntity;
	}
	
	
	/**
	 * 고객이 입력한 정보로 회원가입을 진행한다.
	 * 보낸 값들 중 NULL값이 있으면 "NULL_ARGUMENT" 를 리턴한다.
	 * 회원가입 요청을 보내기 전 먼저 ID 중복체크를 진행한다.
	 * @param memberInfo
	 * @return 
	 *  
	 */
	@PostMapping
	public ResponseEntity<SignUpResponse> signUp(@RequestBody MemberDTO memberInfo) {
		ResponseEntity<SignUpResponse> responseEntity = null;
		SignUpResponse result;
		MemberDTO.NullColumn checkNull = memberInfo.checkNull(memberInfo);
		
		// 전달된 인수중 NULL값이 있으면 오류를 낸다
		if(!checkNull.equals(MemberDTO.NullColumn.NOT_NULL)) {
			log.error(checkNull + " IS NULL!");
			return responseEntity = new ResponseEntity<>(SignUpResponse.NULL_ARGUMENT,HttpStatus.BAD_REQUEST);
		}
		
		
		String id = memberInfo.getId();
		int cnt = memberService.idCheck(id);
		if(cnt != 0) {
			// 중복 아이디일 때
			result = SignUpResponse.ID_DUPLICATED;
			responseEntity = new ResponseEntity<SignUpResponse>(result, HttpStatus.LOCKED);
		}else {
			// 중복 아이디가 아닐 때
			memberService.insertMember(memberInfo);
			result = SignUpResponse.SUCCESS;
			responseEntity = new ResponseEntity<SignUpResponse>(result, HttpStatus.CREATED);
		}
		return responseEntity;
	}
	
	/*
	 * 회원 로그인을 진행한다.
	 */
	@PostMapping("logIn")
	public ResponseEntity<SignInResponse> signIn(@RequestBody MemberDTO memberDTO, HttpSession session){
		ResponseEntity<SignInResponse> responseEntity = null;
		String id = memberDTO.getId();
		String password = memberDTO.getPassword();
		SignInResponse loginResponse;
		MemberDTO memberInfo = memberService.signIn(id, password);
		
		if(memberInfo == null) {
			// id 또는 password 확인 필요(DB에서 가져온 정보가 없을 때)
			loginResponse = SignInResponse.FAIL;
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
		}else if("DEFAULT".equals(memberInfo.getStatus())) {
			// 성공시 세션에 ID를 저장
			loginResponse = SignInResponse.success(memberInfo);
			session.setAttribute("LOGIN_MEMBER_ID", memberInfo.getId());
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.OK);
		}else if("DELETED".equals(memberInfo.getStatus())) {
			// 삭제된 경우
			loginResponse = SignInResponse.DELETED;
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.UNAUTHORIZED);
		}else {
			// 예상하지 못한 오류일 경우
			loginResponse = SignInResponse.ERROR;
			responseEntity = new ResponseEntity<SignInResponse>(loginResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			log.error(responseEntity);
		}
		
		return responseEntity;
	}
	
	/**
	 * 회원 비밀번호 변경
	 * @param session
	 * @return
	 */
	@PutMapping("password")
	public ResponseEntity<UpdateMemberPasswordResponse> updateMemberInfo(HttpSession session,@RequestBody UpdateMemberPasswordRequest passwordRequest){
		String password = passwordRequest.getPassword();
		String newPassword = passwordRequest.getNewPassword();
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		ResponseEntity<UpdateMemberPasswordResponse> responseEntity = null;
		UpdateMemberPasswordResponse updateResponse;

		
		if(id == null) {
			// 로그인하지 않았을 때
			updateResponse = UpdateMemberPasswordResponse.NO_LOGIN;
			responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.UNAUTHORIZED);
		}else if(memberService.signIn(id, password) == null){
			// 원래 패스워드가 일치하지 않음
			updateResponse = UpdateMemberPasswordResponse.PASSWORD_MISMATCH;
			responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.UNAUTHORIZED);
		}else if(newPassword == null){
			updateResponse = UpdateMemberPasswordResponse.EMPTY_PASSWORD;
			responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.BAD_REQUEST);
		}else {
			MapperDMLResponse dmlResponse = memberService.updateMemberPassword(id, newPassword);
			if(dmlResponse == MapperDMLResponse.SUCCESS) {
				updateResponse = UpdateMemberPasswordResponse.SUCCESS;
				responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.OK);
			}else {
				updateResponse = UpdateMemberPasswordResponse.ERROR;
				responseEntity = new ResponseEntity<UpdateMemberPasswordResponse>(updateResponse, HttpStatus.INTERNAL_SERVER_ERROR);
				log.error(dmlResponse + " - ERROR! \n" + responseEntity);
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
			deleteResponse= DeleteMemberResponse.NO_LOGIN;
			responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.UNAUTHORIZED);
		}else {
			MapperDMLResponse dmlResponse = memberService.deleteMember(id);
			if(dmlResponse == MapperDMLResponse.SUCCESS) {
				deleteResponse = DeleteMemberResponse.SUCCESS;
				responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.OK);
			}else {
				deleteResponse = DeleteMemberResponse.ERROR;
				responseEntity = new ResponseEntity<DeleteMemberResponse>(deleteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
				log.error(dmlResponse + " - ERROR! \n" + responseEntity);
			}
		}
		return responseEntity;
	}
	/**
	 * 회원 주소 변경
	 * @param memberInfo
	 * @param session
	 */
	@PutMapping("address")
	public ResponseEntity<UpdateMemberAddressResponse> updateMemberAddress(@RequestBody MemberDTO memberInfo, HttpSession session) {
		ResponseEntity<UpdateMemberAddressResponse> responseEntity = null;
		String address = memberInfo.getAddress();
		String addressDetail = memberInfo.getAddressDetail();
		String id = (String) session.getAttribute("LOGIN_MEMBER_ID");
		
		if(address == null || addressDetail == null) {
			// 요청한 주소가 null일 때
			if(address==null)
				responseEntity = new ResponseEntity<MemberController.UpdateMemberAddressResponse>(UpdateMemberAddressResponse.EMPTY_ADDRESS, HttpStatus.BAD_REQUEST);
			else if(addressDetail==null)
				responseEntity = new ResponseEntity<MemberController.UpdateMemberAddressResponse>(UpdateMemberAddressResponse.EMPTY_ADDRESS_DETAIL, HttpStatus.BAD_REQUEST);
		}else if(id == null) {
			// 로그인을 안했을 때
			responseEntity = new ResponseEntity<MemberController.UpdateMemberAddressResponse>(UpdateMemberAddressResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
		}else {
			MapperDMLResponse dmlResponse = memberService.updateMemberAddress(id, address, addressDetail);
			if(dmlResponse == MapperDMLResponse.SUCCESS) {
				// 성공시
				responseEntity = new ResponseEntity<MemberController.UpdateMemberAddressResponse>(UpdateMemberAddressResponse.SUCCESS, HttpStatus.OK);
			}else {
				// 알수없는 오류 발생시
				responseEntity = new ResponseEntity<MemberController.UpdateMemberAddressResponse>(UpdateMemberAddressResponse.ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
				log.error(dmlResponse + " - ERROR! \n" + responseEntity);
			}
			
		}
		
		return responseEntity;
		
	}
	
	
	
	// -------------- response 객체 --------------
	
	@Getter @AllArgsConstructor @RequiredArgsConstructor
	private static class SignInResponse{
		enum LogInStatus{SUCCESS, FAIL, DELETED, ERROR}
		@NonNull
		private LogInStatus result;
		private MemberDTO memberInfo;
		
		// success의 경우 memberInfo의 값을 set해줘야 하기 때문에 new 하도록 해준다.
		
		private static final SignInResponse FAIL = new SignInResponse(LogInStatus.FAIL);
		private static final SignInResponse DELETED = new SignInResponse(LogInStatus.DELETED);
		private static final SignInResponse ERROR = new SignInResponse(LogInStatus.ERROR);
		
		private static SignInResponse success(MemberDTO memberInfo) {
			return new SignInResponse(LogInStatus.SUCCESS, memberInfo);
		}
		
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class SignUpResponse{
		enum SignUpStatus{SUCCESS, DUPLICATED, ERROR, NULL_ARGUMENT}
		@NonNull
		private SignUpStatus result;
		
		private static final SignUpResponse SUCCESS = new SignUpResponse(SignUpStatus.SUCCESS);
		private static final SignUpResponse ID_DUPLICATED = new SignUpResponse(SignUpStatus.DUPLICATED);
		private static final SignUpResponse ERROR = new SignUpResponse(SignUpStatus.ERROR);
		private static final SignUpResponse NULL_ARGUMENT = new SignUpResponse(SignUpStatus.NULL_ARGUMENT);
		
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class MemberIdDuplResponse{
		enum DuplStatus{SUCCESS, DUPLICATED, ERROR}
		@NonNull
		private DuplStatus result;
		
		private static final MemberIdDuplResponse SUCCESS = new MemberIdDuplResponse(DuplStatus.SUCCESS);
		private static final MemberIdDuplResponse DUPLICATED = new MemberIdDuplResponse(DuplStatus.DUPLICATED);
		private static final MemberIdDuplResponse ERROR = new MemberIdDuplResponse(DuplStatus.ERROR);
		
		
	}
	
	@Getter  @RequiredArgsConstructor
	private static class UpdateMemberPasswordResponse{
		enum UpdateStatus{SUCCESS, NO_LOGIN, EMPTY_PASSWORD, PASSWORD_MISMATCH, ERROR}
		@NonNull
		private UpdateStatus result;
		
		private static final UpdateMemberPasswordResponse SUCCESS = new UpdateMemberPasswordResponse(UpdateStatus.SUCCESS);
		private static final UpdateMemberPasswordResponse NO_LOGIN = new UpdateMemberPasswordResponse(UpdateStatus.NO_LOGIN);
		private static final UpdateMemberPasswordResponse EMPTY_PASSWORD = new UpdateMemberPasswordResponse(UpdateStatus.EMPTY_PASSWORD);
		private static final UpdateMemberPasswordResponse PASSWORD_MISMATCH = new UpdateMemberPasswordResponse(UpdateStatus.PASSWORD_MISMATCH);
		private static final UpdateMemberPasswordResponse ERROR = new UpdateMemberPasswordResponse(UpdateStatus.ERROR);
		
	}
	
	@Getter @RequiredArgsConstructor
	private static class DeleteMemberResponse{
		enum DeleteStatus{SUCCESS, NO_LOGIN, ERROR}
		@NonNull
		private DeleteStatus result;
		
		private static final DeleteMemberResponse SUCCESS = new DeleteMemberResponse(DeleteStatus.SUCCESS);
		private static final DeleteMemberResponse NO_LOGIN = new DeleteMemberResponse(DeleteStatus.NO_LOGIN);
		private static final DeleteMemberResponse ERROR = new DeleteMemberResponse(DeleteStatus.ERROR);
		
	}
	
	@Getter @RequiredArgsConstructor
	private static class UpdateMemberAddressResponse{
		enum UpdateStatus{SUCCESS, NO_LOGIN, EMPTY_ADDRESS, EMPTY_ADDRESS_DETAIL, ERROR}
		@NonNull
		private UpdateStatus result;
		
		private static final UpdateMemberAddressResponse SUCCESS = new UpdateMemberAddressResponse(UpdateStatus.SUCCESS);
		private static final UpdateMemberAddressResponse NO_LOGIN = new UpdateMemberAddressResponse(UpdateStatus.NO_LOGIN);
		private static final UpdateMemberAddressResponse EMPTY_ADDRESS = new UpdateMemberAddressResponse(UpdateStatus.EMPTY_ADDRESS);
		private static final UpdateMemberAddressResponse EMPTY_ADDRESS_DETAIL = new UpdateMemberAddressResponse(UpdateStatus.EMPTY_ADDRESS_DETAIL);
		private static final UpdateMemberAddressResponse ERROR = new UpdateMemberAddressResponse(UpdateStatus.ERROR);
	}
	
	// --------------------------------- request 객체 ---------------------------------
	
	@Setter @Getter
	private static class UpdateMemberPasswordRequest{
		private String password;
		private String newPassword;
	}
}
