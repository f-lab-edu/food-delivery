package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.MemberDTO;
import com.delfood.error.exception.DuplicateIdException;
import com.delfood.service.MemberService;
import com.delfood.utils.SessionUtil;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Java Logging
 * <p>
 * Log4j 속도와 유연성을 고려하여 디자인되어있어 속도에 최적화 되어있다. 멀티 스레드 환경에서 안전하다.
 * </p>
 * <p>
 * SLF4J 로깅에 대한 추상 레이어를 제공한다. 로깅의 인터페이스 역할을 한다. 공통 인터페이스 역할을 하기 때문에 구현체의 종류와 상관 없이 일관된 로깅코드를 작성할 수
 * 있다. slf4j만으로는 로깅을 실행할 수 없어서 commons logging, log4j, logback 등의 로깅 구현체를 적용해야 한다.
 * </p>
 * Logback Log4j를 토대로 만든 새로운 Logging 라이브러리이다. SLF4J를 통해 다른 로깅 프레임워크를 logback으로 통합할 수 있다. Log4j보다 10배
 * 높은 속도 퍼포먼스를 보이도록 설계되어있으며 메모리 효율을 개선하였다. 설정 파일 변경시 서버 재가동 없이도 자동 변경 갱신이 이루어진다. 로깅 I/O시 Failure에 대한
 * 복구를 서버 중지 없이도 지원하고있다. Logback사용을 위해서는 SLF4J와 함께 사용해야 한다.(Logback은 SLF4J의 구현체이다)
 * <p>
 * Log4j2 멀티 스레드 환경에서 Logback보다 10배 높은 성능 퍼포먼스를 기대할 수 있다. Log4j, Logback에 존재하는 동기화 이슈 문제를 해결하였다. 멀티
 * 스레드 환경 로깅이 필요하다면 Log4j2를 사용하는 것이 성능면에서 유리하다. 사용자 정의 로그레벨과 람다 표현식을 지원한다. Log4j2 자체적으로 직접 사용할 수는
 * 있지만 일반적으로는 SLF4J와 함께 사용한다.
 * </p>
 * 
 * @Log @Slf4j @Log4j2 등 어노테이션 적용시 자동으로 log 필드를 만들고 해당 클래스의 이름으로 로거 객체를 생성하여 할당한다.
 * 
 * @author 정준
 *
 */
@RestController
@RequestMapping("/members/")
@Log4j2
public class MemberController {
  @Autowired
  private MemberService memberService;

  /**
   * 로그인한 사용자가 마이페이지를 눌렀을 때 보여줄 사용자 정보를 반환한다.
   * 
   * @param session 현재 사용자의 세션
   * @return MemberDTO
   */
  @GetMapping("myInfo")
  @MemberLoginCheck
  public MemberInfoResponse memberInfo(HttpSession session) {
    String id = SessionUtil.getLoginMemberId(session);
    MemberDTO memberInfo = memberService.getMemberInfo(id);
    return new MemberInfoResponse(memberInfo);
  }

  /**
   * 회원가입 시 아이디의 중복체크를 진행한다. 아이디 중복체크는 회원가입 아이디 입력 후, 회원가입 요청시 두번 진행한다. 아이디 중복체크를 한 후 회원가입 버튼을 누를 때
   * 까지 동일한 아이디로 누군가 가입한다면 PK Error가 발생되고 실제로 회원가입이 진행되지 않을 수 있기 때문에 회원가입을 눌렀을 때 한번 더 실행하는 것이 좋다.
   * 
   * @param id 중복체크를 진행할 고객 아이디
   * @return
   */
  @GetMapping("duplicated/{id}")
  public boolean idCheck(@PathVariable @NotNull String id) {
    boolean idDuplicated = memberService.isDuplicatedId(id);
    return idDuplicated;
  }

  /*
   * @NotNull 메서드의 파라미터, 리턴값에 사용할 수 있다. 파라미터가 null로 전달되면 예외를 던진다. 클래스 멤버에도 적용할 수 있는데 이 경우 setter,
   * constructor를 사용할 때 null 주입 시 NullPointerException을 발생시킨다.
   */
  /**
   * 고객이 입력한 정보로 회원가입을 진행한다. 보낸 값들 중 NULL값이 있으면 "NULL_ARGUMENT" 를 리턴한다. 회원가입 요청을 보내기 전 먼저 ID 중복체크를
   * 진행한다. ID 중복시 403 상태코드를 반환한다. 회원가입 성공시 201 상태코드를 반환한다.
   * 
   * @param memberInfo 회원가입을 요청한 정보
   * @return
   * 
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@RequestBody @NotNull MemberDTO memberInfo) {
    if (MemberDTO.hasNullDataBeforeSignup(memberInfo)) {
      throw new NullPointerException("회원가입시 필수 데이터를 모두 입력해야 합니다.");
    }
    memberService.insertMember(memberInfo);
  }

  /**
   * 회원 로그인을 진행한다. Login 요청시 id, password가 NULL일 경우 NullPointerException을 throw한다.
   */
  @PostMapping("login")
  public ResponseEntity<LoginResponse> login(@RequestBody @NonNull MemberLoginRequest loginRequest,
      HttpSession session) {
    ResponseEntity<LoginResponse> responseEntity = null;
    String id = loginRequest.getId();
    String password = loginRequest.getPassword();
    LoginResponse loginResponse;
    MemberDTO memberInfo = memberService.login(id, password);

    if (memberInfo == null) {
      // ID, Password에 맞는 정보가 없을 때
      loginResponse = LoginResponse.FAIL;
      responseEntity = new ResponseEntity<MemberController.LoginResponse>(loginResponse,
          HttpStatus.UNAUTHORIZED);
    } else if (MemberDTO.Status.DEFAULT.equals(memberInfo.getStatus())) {
      // 성공시 세션에 ID를 저장
      loginResponse = LoginResponse.success(memberInfo);
      SessionUtil.setLoginMemberId(session, id);
      responseEntity = new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
    } else {
      // 예상하지 못한 오류일 경우
      log.error("login ERROR" + responseEntity);
      throw new RuntimeException("login ERROR!");
    }

    return responseEntity;
  }

  /**
   * 회원 로그아웃 메서드.
   * 
   * @author jun
   * @param session 현제 접속한 세션
   * @return 로그인 하지 않았을 시 401코드를 반환하고 result:NO_LOGIN 반환 로그아웃 성공시 200 코드를 반환
   */
  @GetMapping("logout")
  @MemberLoginCheck
  public void logout(HttpSession session) {
    SessionUtil.logoutMember(session);
  }

  /**
   * 회원 비밀번호 변경.
   * 원래 비밀번호, 변경할 비밀번호를 모두 입력해야 한다.
   * 변경 전 비밀번호가 일치하지 않을 시 비밀번호가 변경되지 않는다.
   * @param session 현재 로그인한 사용자의 세션
   * @return
   */
  @PatchMapping("password")
  @MemberLoginCheck
  public void updateMemberInfo(HttpSession session,
      @RequestBody @NotNull UpdateMemberPasswordRequest passwordRequest) {
    String passwordBeforeChange = passwordRequest.getPasswordBeforeChange();
    String passwordAfterChange = passwordRequest.getPasswordAfterChange();
    String id = SessionUtil.getLoginMemberId(session);
    if (passwordAfterChange == null || passwordBeforeChange == null) { // 유효성 검사
      throw new NullPointerException("패스워드를 입력해주세요");
    } else {
      memberService.updateMemberPassword(id, passwordBeforeChange, passwordAfterChange);
    }
  }

  /**
   * 회원을 삭제 상태로 변환시킨다. delete 쿼리가 아닌 update 쿼리가 진행된다. MEMBER 테이블의 status 컬럼을 delete로 바꾸어 비활성 상태로
   * 만든다.
   * 
   * @param session 현재 로그인한 사용자의 세션
   * @return
   */
  @DeleteMapping("myInfo")
  @MemberLoginCheck
  public void deleteMemberInfo(HttpSession session) {
    String id = SessionUtil.getLoginMemberId(session);
    memberService.deleteMember(id);
    // 회원 탈퇴시 로그아웃 시켜야 하기 때문에 세션 정보를 날린다
    SessionUtil.logoutMember(session);
  }

  /**
   * 회원 주소 변경.
   * 
   * @param memberInfo 회원 주소 정보
   * @param session 현재 로그인한 고객의 세션
   */
  @PatchMapping("address")
  @MemberLoginCheck
  public ResponseEntity<UpdateMemberAddressResponse> updateMemberAddress(
      @RequestBody @NotNull UpdateMemberAddressRequest memberInfo, HttpSession session) {
    ResponseEntity<UpdateMemberAddressResponse> responseEntity = null;
    String addressCode = memberInfo.getAddressCode();
    String addressDetail = memberInfo.getAddressDetail();
    String id = SessionUtil.getLoginMemberId(session);

    if (addressCode == null) {
      // 요청한 주소가 null일 때
      responseEntity = new ResponseEntity<UpdateMemberAddressResponse>(
          UpdateMemberAddressResponse.EMPTY_ADDRESS, HttpStatus.BAD_REQUEST);
    } else if (addressDetail == null) {
      responseEntity = new ResponseEntity<UpdateMemberAddressResponse>(
          UpdateMemberAddressResponse.EMPTY_ADDRESS_DETAIL, HttpStatus.BAD_REQUEST);
    } else {
      // 모든 조건을 충족할 때
      memberService.updateMemberAddress(id, addressCode, addressDetail);
      responseEntity =
          new ResponseEntity<MemberController.UpdateMemberAddressResponse>(HttpStatus.OK);
    }

    return responseEntity;

  }



  // -------------- response 객체 --------------

  @Getter
  @AllArgsConstructor
  @RequiredArgsConstructor
  private static class LoginResponse {
    enum LoginStatus {
      SUCCESS, FAIL, DELETED
    }

    @NonNull
    private LoginStatus result;
    private MemberDTO memberInfo;

    // success의 경우 memberInfo의 값을 set해줘야 하기 때문에 new 하도록 해준다.
    private static final LoginResponse FAIL = new LoginResponse(LoginStatus.FAIL);
    private static LoginResponse success(MemberDTO memberInfo) {
      return new LoginResponse(LoginStatus.SUCCESS, memberInfo);
    }


  }

  @Getter
  private static class UpdateMemberAddressResponse {
    enum UpdateStatus {
      EMPTY_ADDRESS, EMPTY_ADDRESS_DETAIL
    }

    @NonNull
    private UpdateStatus message;

    private static final UpdateMemberAddressResponse EMPTY_ADDRESS =
        new UpdateMemberAddressResponse(UpdateStatus.EMPTY_ADDRESS);
    private static final UpdateMemberAddressResponse EMPTY_ADDRESS_DETAIL =
        new UpdateMemberAddressResponse(UpdateStatus.EMPTY_ADDRESS_DETAIL);

    public UpdateMemberAddressResponse(UpdateStatus message) {
      this.message = message;
    }
  }

  @Getter
  @AllArgsConstructor
  private static class MemberInfoResponse {
    private MemberDTO memberInfo;
  }

  // --------------------------------- request 객체 ---------------------------------

  @Setter
  @Getter
  private static class UpdateMemberPasswordRequest {
    @NonNull
    private String passwordBeforeChange;
    @NonNull
    private String passwordAfterChange;
  }

  @Setter
  @Getter
  private static class MemberLoginRequest {
    @NonNull
    private String id;
    @NonNull
    private String password;
  }
  @Setter
  @Getter
  private static class UpdateMemberAddressRequest {
    private String addressCode;
    private String addressDetail;
  }
}
