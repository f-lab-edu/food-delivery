package com.delfood.controller;

import com.delfood.aop.LoginCheck;
import com.delfood.aop.LoginCheck.UserType;
import com.delfood.aop.RiderLoginCheck;
import com.delfood.dto.rider.RiderDTO;
import com.delfood.service.delivery.DeliveryService;
import com.delfood.service.rider.RiderInfoService;
import com.delfood.utils.SessionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/riders/")
public class RiderController {
  
  @Autowired
  private RiderInfoService riderInfoService;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private DeliveryService deliveryService;
  
  /**
   * 아이디 중복 체크.
   * @author jun
   * @param riderId 중복체크할 아이디
   * @return 중복된 아이디라면 true
   */
  @GetMapping("duplicated/id/{riderId}")
  public boolean isDuplicatedId(@PathVariable(name = "riderId") String riderId) {
    return riderInfoService.isDuplicatedId(riderId);
  }
  
  /**
   * 라이더 회원가입.
   * @author jun
   * @param riderInfo 회원가입할 아이디 정보
   * @throws JsonProcessingException 로그를 기록할 때 직렬화중 생길 수 있는 예외
   */
  @PostMapping("signUp")
  @ResponseStatus(code = HttpStatus.CREATED)
  public void signUp(@RequestBody RiderDTO riderInfo) throws JsonProcessingException {
    if (riderInfo.hasNullData()) {
      log.info("회원가입 필수 데이터 누락. 요청 정보 : {}", objectMapper.writeValueAsString(riderInfo));
      throw new NullPointerException("라이더 회원가입에 필수 데이터가 누락되었습니다.");
    }
    
    RiderDTO encryptRiderInfo = RiderDTO.encryptDTO(riderInfo);
    riderInfoService.signUp(encryptRiderInfo);
  }
  
  /**
   * 라이더 로그인을 진행한다.
   * 
   * @author jun
   * @param request id, password 정보
   * @param session 현재 세션
   * @return
   */
  @PostMapping("login")
  public RiderDTO signIn(@RequestBody SignInRequest request, HttpSession session) {
    if (Objects.isNull(SessionUtil.getLoginRiderId(session)) == false) {
      logout(session);
    }
    
    RiderDTO riderInfo = riderInfoService.signIn(request.getId(), request.getPassword());
    SessionUtil.setLoginRiderId(session, riderInfo.getId());
    return riderInfo;
  }
  
  
  /**
   * 라이더 로그아웃을 진행한다.
   * @author jun
   * @param session 사용자의 세션
   */
  @GetMapping("logout")
  public void logout(HttpSession session) {
    SessionUtil.logoutRider(session);
  }
  
  /**
   * 라이더의 비밀번호를 변경한다.
   * @param session 사용자의 세션
   * @param request 변경전 비밀번호, 변경할 비밀번호 정보
   */
  @PatchMapping("password")
  @LoginCheck(type = UserType.RIDER)
  public void updatePassword(HttpSession session, @RequestBody UpdatePasswordRequest request) {
    String id = SessionUtil.getLoginRiderId(session);
    riderInfoService.changePassword(id, request.getPasswordBeforechange(),
        request.getPasswordAfterChange());
  }
  
  /**
   * 라이더의 계정을 삭제한다.
   * 삭제가 완료된다면 로그아웃된다.
   * @param session 현제 사용자의 세션
   * @param password 유효성 검사를 위한 계정 비밀번호
   */
  @DeleteMapping
  @LoginCheck(type = UserType.RIDER)
  public void deleteRiderAccount(HttpSession session, String password) {
    String id = SessionUtil.getLoginRiderId(session);
    riderInfoService.deleteAccount(id, password);
    SessionUtil.logoutRider(session);
  }
  
  @PatchMapping("mail")
  public void updateMail(HttpSession session, @RequestBody UpdateMailRequest request) {
    String id = SessionUtil.getLoginRiderId(session);
    riderInfoService.changeMail(id, request.getPassword(), request.getUpdateMail());
  }
  
  @PostMapping("delivery/accept")
  @LoginCheck(type = UserType.RIDER)
  public void deliveryAccept(@RequestBody DeliveryAcceptRequest request, HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
	deliveryService.acceptDeliveryRequest(riderId, request.getOrderId());
  }

  // Request
  @Getter
  private static class SignInRequest {
    @NonNull
    private String id;
    
    @NonNull
    private String password;
  }

  @Getter
  private static class DeliveryAcceptRequest {
    private Long orderId;
  }

  @Getter
  private static class UpdatePasswordRequest {
    @NonNull
    private String passwordBeforechange;
    
    @NonNull
    private String passwordAfterChange;
  }
  
  @Getter
  private static class UpdateMailRequest {
    @NonNull
    private String password;
    
    @NonNull
    private String updateMail;
  }
  
  
  
}
