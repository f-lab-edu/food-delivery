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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   * 회원 로그인 기능 수행.
   * 
   * @param loginRequest 로그인 요청 ( id, password )
   * @return
   */
  @PostMapping("login")
  public ResponseEntity<OwnerLoginResponse> login(@RequestBody OwnerLoginRequest loginRequest,
      HttpSession session) {
    OwnerDTO ownerInfo = ownerService.login(loginRequest.getId(), loginRequest.getPassword());
    OwnerLoginResponse ownerLoginResponse;
    ResponseEntity<OwnerLoginResponse> responseEntity;

    if (ownerInfo == null) { // 아이디와 비밀번호가 일치하지 않거나, 회원정보가 없음
      ownerLoginResponse = OwnerLoginResponse.FAIL;
      responseEntity =
          new ResponseEntity<OwnerLoginResponse>(ownerLoginResponse, HttpStatus.UNAUTHORIZED);
    } else if (Status.DEFAULT.equals(ownerInfo.getStatus())) { // 성공
      ownerLoginResponse = OwnerLoginResponse.success(ownerInfo);
      session.setAttribute("LOGIN_OWNER_ID", ownerInfo.getId());
      responseEntity = new ResponseEntity<OwnerLoginResponse>(ownerLoginResponse, HttpStatus.OK);
    } else if (Status.DELETED.equals(ownerInfo.getStatus())) { // 삭제된 계정일 때
      ownerLoginResponse = OwnerLoginResponse.DELETED;
      responseEntity = new ResponseEntity<OwnerController.OwnerLoginResponse>(ownerLoginResponse,
          HttpStatus.UNAUTHORIZED);
    } else { // 예상치 못한 에러처리
      log.error("login error {} ", loginRequest);
      throw new RuntimeException("login error");
    }
    return responseEntity;
  }


  /**
   * 사장님 로그아웃.
   * 
   * @param session 현재 사용자 세션
   * @return
   */
  @PostMapping("logout")
  public ResponseEntity<logoutResponse> logout(HttpSession session) {
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    if (id != null) {
      session.invalidate();
      return new ResponseEntity<OwnerController.logoutResponse>(
          logoutResponse.SUCCESS, HttpStatus.OK);
    } else {
      return new ResponseEntity<OwnerController.logoutResponse>(
          logoutResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
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
      OwnerDTO ownerInfo = ownerService.ownerInfo(id);
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
  @PutMapping
  public ResponseEntity<UpdateOwnerMailAndTelResponse> updateOwnerInfo(
      @RequestBody UpdateOwnerMailAndTelRequest updateRequest, HttpSession session) {

    String mail = updateRequest.getMail();
    String tel = updateRequest.getTel();
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    ResponseEntity<UpdateOwnerMailAndTelResponse> responseEntity;

    if (mail == null) {
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerMailAndTelResponse>(
          UpdateOwnerMailAndTelResponse.EMPTY_MAIL, HttpStatus.BAD_REQUEST);
    } else if (tel == null) {
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerMailAndTelResponse>(
          UpdateOwnerMailAndTelResponse.EMPTY_TEL, HttpStatus.BAD_REQUEST);
    } else if (id == null) {
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerMailAndTelResponse>(
          UpdateOwnerMailAndTelResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    } else {
      DMLOperationError dmlOperationError = ownerService.updateOwnerMailAndTel(id, mail, tel);

      if (dmlOperationError == DMLOperationError.SUCCESS) {
        responseEntity = new ResponseEntity<OwnerController.UpdateOwnerMailAndTelResponse>(
            UpdateOwnerMailAndTelResponse.SUCCESS, HttpStatus.OK);
      } else {
        log.error("Member mail and tel update ERROR : {}", updateRequest);
        throw new RuntimeException("Member mail and tel update ERROR");
      }
    }
    return responseEntity;
  }

  /**
   * 사장 패스워드 변경.
   * 
   * @param passwordResquest 변경전 패스워드, 변경할 패스워드을 담은 요청 객체
   * @param session 현재 사용자의 세션
   * @return
   */
  @PutMapping("password")
  public ResponseEntity<UpdateOwnerPasswordResponse> updatePassword(
      @RequestBody UpdateOwnerPasswordRequest passwordResquest, HttpSession session) {
    String id = (String) session.getAttribute("LOGIN_OWNER_ID");
    String password = passwordResquest.getPassword();
    String newPassword = passwordResquest.getNewPassword();

    ResponseEntity<UpdateOwnerPasswordResponse> responseEntity;


    if (id == null) { // 비 로그인 상태
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerPasswordResponse>(
          UpdateOwnerPasswordResponse.NO_LOGIN, HttpStatus.UNAUTHORIZED);
    } else if (ownerService.login(id, password) == null) { // 아이디와 비밀번호 불일치
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerPasswordResponse>(
          UpdateOwnerPasswordResponse.PASSWORD_MISMATCH, HttpStatus.BAD_REQUEST);
    } else if (newPassword == null) {
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerPasswordResponse>(
          UpdateOwnerPasswordResponse.EMPTY_PASSOWRD, HttpStatus.BAD_REQUEST);
    } else if (password.equals(newPassword)) { // 이전 패스워드와 동일한 경우
      responseEntity = new ResponseEntity<OwnerController.UpdateOwnerPasswordResponse>(
          UpdateOwnerPasswordResponse.PASSWORD_DUPLICATED, HttpStatus.CONFLICT);
    } else {
      DMLOperationError dmlOperationError = ownerService.updateOwnerPassword(id, newPassword);

      if (DMLOperationError.SUCCESS.equals(dmlOperationError)) {
        responseEntity = new ResponseEntity<OwnerController.UpdateOwnerPasswordResponse>(
            UpdateOwnerPasswordResponse.SUCCESS, HttpStatus.OK);
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
  private static class UpdateOwnerMailAndTelResponse {
    enum UpdateStatus {
      SUCCESS, NO_LOGIN, EMPTY_MAIL, EMPTY_TEL
    }

    @NonNull
    private UpdateStatus result;

    private static final UpdateOwnerMailAndTelResponse SUCCESS =
        new UpdateOwnerMailAndTelResponse(UpdateStatus.SUCCESS);
    private static final UpdateOwnerMailAndTelResponse NO_LOGIN =
        new UpdateOwnerMailAndTelResponse(UpdateStatus.NO_LOGIN);
    private static final UpdateOwnerMailAndTelResponse EMPTY_MAIL =
        new UpdateOwnerMailAndTelResponse(UpdateStatus.EMPTY_MAIL);
    private static final UpdateOwnerMailAndTelResponse EMPTY_TEL =
        new UpdateOwnerMailAndTelResponse(UpdateStatus.EMPTY_TEL);
  }

  @Getter
  @RequiredArgsConstructor
  private static class UpdateOwnerPasswordResponse {
    enum UpdateStatus {
      SUCCESS, NO_LOGIN, EMPTY_PASSOWRD, PASSWORD_MISMATCH, PASSWORD_DUPLICATED
    }

    @NonNull
    private UpdateStatus result;

    private static final UpdateOwnerPasswordResponse SUCCESS =
        new UpdateOwnerPasswordResponse(UpdateStatus.SUCCESS);
    private static final UpdateOwnerPasswordResponse NO_LOGIN =
        new UpdateOwnerPasswordResponse(UpdateStatus.NO_LOGIN);
    private static final UpdateOwnerPasswordResponse EMPTY_PASSOWRD =
        new UpdateOwnerPasswordResponse(UpdateStatus.EMPTY_PASSOWRD);
    private static final UpdateOwnerPasswordResponse PASSWORD_MISMATCH =
        new UpdateOwnerPasswordResponse(UpdateStatus.PASSWORD_MISMATCH);
    private static final UpdateOwnerPasswordResponse PASSWORD_DUPLICATED =
        new UpdateOwnerPasswordResponse(UpdateStatus.PASSWORD_DUPLICATED);
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


