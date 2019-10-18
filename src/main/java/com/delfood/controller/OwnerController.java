package com.delfood.controller;

import com.delfood.dto.OwnerDTO;
import com.delfood.dto.OwnerDTO.Status;
import com.delfood.mapper.DMLOperationError;
import com.delfood.service.OwnerService;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners/")
@Log4j2
public class OwnerController {
  @Autowired
  private OwnerService ownerService;

  /**
   * 사장님 회원가입 메서드.
   * 
   * @author jun
   * @param ownerInfo 회원가입할 사장님 정보
   */
  @PostMapping
  public ResponseEntity<SignUpResponse> signUp(@RequestBody OwnerDTO ownerInfo) {
    if (OwnerDTO.hasNullDataBeforeSignUp(ownerInfo)) {
      throw new NullPointerException("사장님 회원가입에 필요한 정보에 NULL이 존재합니다.");
    }

    // id 중복체크
    if (ownerService.isDuplicatedId(ownerInfo.getId())) {
      return new ResponseEntity<OwnerController.SignUpResponse>(SignUpResponse.ID_DUPLICATED,
          HttpStatus.CONFLICT);
    }

    ownerService.signUp(ownerInfo);
    return new ResponseEntity<OwnerController.SignUpResponse>(SignUpResponse.SUCCESS,
        HttpStatus.CREATED);
  }

  /**
   * id 중복 체크 메서드.
   * 
   * @author jun
   * @param id 중복체크를 진행할 사장님 ID
   * @return 중복된 아이디 일시 true
   */
  @GetMapping("idCheck/{id}")
  public ResponseEntity<IdDuplResponse> idCheck(@PathVariable("id") String id) {
    boolean isDupl = ownerService.isDuplicatedId(id);
    if (isDupl) {
      return new ResponseEntity<OwnerController.IdDuplResponse>(IdDuplResponse.ID_DUPLICATED,
          HttpStatus.CONFLICT);
    } else {
      return new ResponseEntity<OwnerController.IdDuplResponse>(IdDuplResponse.SUCCESS,
          HttpStatus.OK);
    }
  }



  /**
   * 회원 로그인 기능 수행.
   * @param loginRequest 로그인 요청 ( id, password )
   * @return
   */
  @PostMapping("login")
  public ResponseEntity<OwnerLoginResponse> login(@RequestBody OwnerLoginRequest loginRequest,
      HttpSession session) {
    OwnerDTO ownerInfo = ownerService.getOwner(loginRequest.getId(), loginRequest.getPassword());
    OwnerLoginResponse ownerLoginResponse;
    ResponseEntity<OwnerLoginResponse> responseEntity;

    if (ownerInfo == null) { // 아이디와 비밀번호가 일치하지 않거나, 회원정보가 없음
      ownerLoginResponse = OwnerLoginResponse.FAIL;
      responseEntity =
          new ResponseEntity<OwnerLoginResponse>(ownerLoginResponse, HttpStatus.UNAUTHORIZED);
    } else { // 회원 정보가 존재
      Status ownerStatus = ownerInfo.getStatus();
      if (ownerStatus == Status.DEFAULT) { 
        ownerLoginResponse = OwnerLoginResponse.success(ownerInfo);
        session.setAttribute("LOGIN_OWNER_ID", ownerInfo.getId());
        responseEntity = new ResponseEntity<OwnerLoginResponse>(ownerLoginResponse, HttpStatus.OK);
      } else {
        ownerLoginResponse = OwnerLoginResponse.DELETED;
        responseEntity = new ResponseEntity<OwnerController.OwnerLoginResponse>(ownerLoginResponse,
            HttpStatus.UNAUTHORIZED);
      }
    }
    return responseEntity;
  }


  /**
   * 사장님 로그아웃.
   * 
   * @param session 현재 사용자 세션
   * @return
   */
  @GetMapping("logout")
  public ResponseEntity<logoutResponse> logout(HttpSession session) {
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    if (id != null) {
      session.invalidate();
      return new ResponseEntity<OwnerController.logoutResponse>(logoutResponse.SUCCESS,
          HttpStatus.OK);
    } else {
      return new ResponseEntity<OwnerController.logoutResponse>(logoutResponse.NO_LOGIN,
          HttpStatus.UNAUTHORIZED);
    }
  }


  /**
   * 로그인한 사장의 정보를 조회.
   * 
   * @param session 현재 사용자 세션
   * @return
   */
  @GetMapping("myInfo")
  public ResponseEntity<OwnerDTO> ownerInfo(HttpSession session) {
    ResponseEntity<OwnerDTO> responseEntity = null;
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    if (id == null) {
      responseEntity = new ResponseEntity<OwnerDTO>(HttpStatus.UNAUTHORIZED);
    } else {
      OwnerDTO ownerInfo = ownerService.getOwner(id);
      responseEntity = new ResponseEntity<OwnerDTO>(ownerInfo, HttpStatus.OK);
    }
    return responseEntity;
  }

  /**
   * 사장 이메일, 전화번호 변경.
   * 
   * @param updateRequest 이메일, 전화번호를 포함한 update 객체
   * @param session 현재 사용자 세션
   * @return
   */
  @PatchMapping
  public ResponseEntity<UpdateOwnerResponse> updateOwnerInfo(
      @RequestBody UpdateOwnerMailAndTelRequest updateRequest, HttpSession session) {

    String mail = updateRequest.getMail();
    String tel = updateRequest.getTel();
    String password = updateRequest.getPassword();
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");

    if (id == null) { // 로그인 상태가 아닌 경우
      return new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    }
    
    if (ownerService.getOwner(id, password) == null) {
      return new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.PASSWORD_MISMATCH, HttpStatus.UNAUTHORIZED);
    }
    
    if (mail == null && tel == null) { // 변경하려는 정보가 둘 다 null일 경우
      return new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.EMPTY_CONTENT, HttpStatus.BAD_REQUEST);
    }

    DMLOperationError dmlOperationError = ownerService.updateOwnerMailAndTel(id, mail, tel);
    if (dmlOperationError == DMLOperationError.SUCCESS) {
      return new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.SUCCESS, HttpStatus.OK);
    } else {
      log.error("Member mail and tel update ERROR : {}", updateRequest);
      throw new RuntimeException("Member mail and tel update ERROR");
    }
  }

  /**
   * 사장 패스워드 변경.
   * 
   * @param passwordResquest 변경전 패스워드, 변경할 패스워드을 담은 요청 객체
   * @param session 현재 사용자의 세션
   * @return
   */
  @PatchMapping("password")
  public ResponseEntity<UpdateOwnerResponse> updatePassword(
      @RequestBody UpdateOwnerPasswordRequest passwordResquest, HttpSession session) {
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    String password = passwordResquest.getPassword();
    String newPassword = passwordResquest.getNewPassword();

    ResponseEntity<UpdateOwnerResponse> responseEntity;


    if (id == null) { // 비 로그인 상태
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    } else if (password == null || newPassword == null) { // 비밀번호나 새 비밀번호를 입력하지 않은 경우
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.EMPTY_PASSOWRD, HttpStatus.BAD_REQUEST);
    } else if (ownerService.getOwner(id, password) == null) { // 아이디와 비밀번호 불일치
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.PASSWORD_MISMATCH, HttpStatus.UNAUTHORIZED);
    } else if (password.equals(newPassword)) { // 이전 패스워드와 동일한 경우
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerResponse>(
          UpdateOwnerResponse.PASSWORD_DUPLICATED, HttpStatus.CONFLICT);
    } else {
      DMLOperationError dmlOperationError = ownerService.updateOwnerPassword(id, newPassword);

      if (DMLOperationError.SUCCESS.equals(dmlOperationError)) {
        responseEntity = new ResponseEntity<OwnerController.UpdateOwnerResponse>(
            UpdateOwnerResponse.SUCCESS, HttpStatus.OK);
      } else {
        log.error("Password Update Error {}", passwordResquest);
        throw new RuntimeException("Password Update Error");
      }
      
    }
    return responseEntity;
  }



  // ============= Requset 객체 ================

  @Setter
  @Getter
  private static class OwnerLoginRequest {
    @NonNull
    private String id;
    @NonNull
    private String password;
  }

  @Setter
  @Getter
  private static class UpdateOwnerMailAndTelRequest {
    @NonNull
    private String password;
    @NonNull
    private String mail;
    @NonNull
    private String tel;
  }

  @Setter
  @Getter
  private static class UpdateOwnerPasswordRequest {
    @NonNull
    private String password;
    @NonNull
    private String newPassword;
  }


  // ============ resopnse 객체 =====================

  @Getter
  @RequiredArgsConstructor
  private static class SignUpResponse {
    enum SignUpStatus {
      SUCCESS, ID_DUPLICATED
    }

    @NonNull
    private SignUpStatus result;

    private static final SignUpResponse SUCCESS = new SignUpResponse(SignUpStatus.SUCCESS);
    private static final SignUpResponse ID_DUPLICATED =
        new SignUpResponse(SignUpStatus.ID_DUPLICATED);
  }

  @Getter
  @RequiredArgsConstructor
  private static class IdDuplResponse {
    enum DuplStatus {
      SUCCESS, ID_DUPLICATED
    }

    @NonNull
    private DuplStatus result;

    private static final IdDuplResponse SUCCESS = new IdDuplResponse(DuplStatus.SUCCESS);
    private static final IdDuplResponse ID_DUPLICATED =
        new IdDuplResponse(DuplStatus.ID_DUPLICATED);
  }
  
  
  @Getter
  @AllArgsConstructor
  @RequiredArgsConstructor
  private static class OwnerLoginResponse {
    enum LoginStatus {
      SUCCESS, FAIL, DELETED, ERROR
    }

    @NonNull
    private LoginStatus result;
    private OwnerDTO ownerInfo;

    private static final OwnerLoginResponse FAIL = new OwnerLoginResponse(LoginStatus.FAIL);
    private static final OwnerLoginResponse DELETED = new OwnerLoginResponse(LoginStatus.DELETED);

    private static OwnerLoginResponse success(OwnerDTO ownerInfo) {
      return new OwnerLoginResponse(LoginStatus.SUCCESS, ownerInfo);
    }

  }

  @Getter
  @RequiredArgsConstructor
  private static class UpdateOwnerResponse {
    enum UpdateStatus {
      SUCCESS, NO_LOGIN, EMPTY_CONTENT, EMPTY_PASSOWRD, PASSWORD_MISMATCH, PASSWORD_DUPLICATED
    }

    @NonNull
    private UpdateStatus result;

    private static final UpdateOwnerResponse SUCCESS =
        new UpdateOwnerResponse(UpdateStatus.SUCCESS);
    private static final UpdateOwnerResponse NO_LOGIN =
        new UpdateOwnerResponse(UpdateStatus.NO_LOGIN);
    private static final UpdateOwnerResponse EMPTY_CONTENT =
        new UpdateOwnerResponse(UpdateStatus.EMPTY_CONTENT);
    private static final UpdateOwnerResponse EMPTY_PASSOWRD =
        new UpdateOwnerResponse(UpdateStatus.EMPTY_PASSOWRD);
    private static final UpdateOwnerResponse PASSWORD_MISMATCH =
        new UpdateOwnerResponse(UpdateStatus.PASSWORD_MISMATCH);
    private static final UpdateOwnerResponse PASSWORD_DUPLICATED =
        new UpdateOwnerResponse(UpdateStatus.PASSWORD_DUPLICATED);
  }


  @Getter
  @RequiredArgsConstructor
  private static class logoutResponse {
    enum logoutStatus {
      SUCCESS, NO_LOGIN
    }

    @NonNull
    private logoutStatus result;

    private static final logoutResponse SUCCESS = new logoutResponse(logoutStatus.SUCCESS);
    private static final logoutResponse NO_LOGIN = new logoutResponse(logoutStatus.NO_LOGIN);

  }

}


