package com.delfood.controller;

import com.delfood.aop.LoginCheck;
import com.delfood.aop.LoginCheck.UserType;
import com.delfood.dto.owner.OwnerDTO;
import com.delfood.dto.owner.OwnerDTO.Status;
import com.delfood.aop.MemberLoginCheck;
import com.delfood.aop.OwnerLoginCheck;
import com.delfood.error.exception.DuplicateIdException;
import com.delfood.service.OwnerService;
import com.delfood.service.PushService;
import com.delfood.utils.SessionUtil;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners/")
@Log4j2
public class OwnerController {
  @Autowired
  private OwnerService ownerService;
  
  @Autowired
  private PushService pushService;

  /**
   * 사장님 회원가입 메서드.
   * 아이디가 중복되었다면 에러를 발생시킨다.
   * @author jun
   * @param ownerInfo 회원가입할 사장님 정보
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@RequestBody OwnerDTO ownerInfo) {
    if (OwnerDTO.hasNullDataBeforeSignUp(ownerInfo)) {
      throw new NullPointerException("사장님 회원가입에 필요한 정보에 NULL이 존재합니다.");
    }
    ownerService.signUp(ownerInfo);
  }

  /**
   * id 중복 체크 메서드.
   * 
   * @author jun
   * @param id 중복체크를 진행할 사장님 ID
   * @return 중복된 아이디 일시 true
   */
  @GetMapping("duplicated/{id}")
  public boolean idCheck(@PathVariable("id") String id) {
    boolean isDupl = ownerService.isDuplicatedId(id);
    return isDupl;
  }



  /**
   * 회원 로그인 기능 수행.
   * 
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
        SessionUtil.setLoginOwnerId(session, loginRequest.getId());
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
  @LoginCheck(type = UserType.OWNER)
  public void logout(HttpSession session) {
    SessionUtil.logoutOwner(session);
  }


  /**
   * 로그인한 사장의 정보를 조회.
   * 
   * @param session 현재 사용자 세션
   * @return
   */
  @GetMapping("myInfo")
  @LoginCheck(type = UserType.OWNER)
  public OwnerInfoResponse ownerInfo(HttpSession session) {
    String id = SessionUtil.getLoginOwnerId(session);
    OwnerDTO ownerInfo = ownerService.getOwner(id);
    return new OwnerInfoResponse(ownerInfo);
  }

  /**
   * 사장 이메일, 전화번호 변경.
   * 
   * @param updateRequest 이메일, 전화번호를 포함한 update 객체
   * @param session 현재 사용자 세션
   * @return
   */
  @PatchMapping
  @LoginCheck(type = UserType.OWNER)
  public void updateOwnerInfo(
      @RequestBody UpdateOwnerMailAndTelRequest updateRequest, HttpSession session) {

    String mail = updateRequest.getMail();
    String tel = updateRequest.getTel();
    String password = updateRequest.getPassword();
    String id = SessionUtil.getLoginOwnerId(session);

    if (mail == null && tel == null) { // 변경하려는 정보가 둘 다 null일 경우
      throw new NullPointerException("변경할 정보를 입력해야 합니다.");
    }

    ownerService.updateOwnerMailAndTel(id, password, mail, tel);
  }

  /**
   * 사장 패스워드 변경.
   * 
   * @param passwordResquest 변경전 패스워드, 변경할 패스워드을 담은 요청 객체
   * @param session 현재 사용자의 세션
   * @return
   */
  @PatchMapping("password")
  @LoginCheck(type = UserType.OWNER)
  public void updatePassword(
      @RequestBody UpdateOwnerPasswordRequest passwordResquest, HttpSession session) {
    String id = SessionUtil.getLoginOwnerId(session);
    String passwordBeforeChange = passwordResquest.getPasswordBeforeChange();
    String passwordAfterChange = passwordResquest.getPasswordAfterChange();

    if (passwordBeforeChange == null || passwordAfterChange == null) { // 비밀번호나 새 비밀번호를 입력하지 않은 경우
      throw new NullPointerException();
    }
    ownerService.updateOwnerPassword(id, passwordBeforeChange, passwordAfterChange);
  }
  
  @PostMapping("token")
  @LoginCheck(type = UserType.OWNER)
  public void addToken(HttpSession session, String token) {
    String ownerId = SessionUtil.getLoginOwnerId(session);
    pushService.addOwnerToken(ownerId, token);
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
    private String passwordBeforeChange;
    @NonNull
    private String passwordAfterChange;
  }


  // ===================== resopnse 객체 =====================

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
  @AllArgsConstructor
  private static class OwnerInfoResponse {
    private OwnerDTO ownerInfo;
  }
}


