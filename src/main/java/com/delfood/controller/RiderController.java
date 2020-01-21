package com.delfood.controller;

import com.delfood.aop.LoginCheck;
import com.delfood.aop.OwnerShopCheck;
import com.delfood.aop.LoginCheck.UserType;
import com.delfood.aop.RiderLoginCheck;
import com.delfood.dto.rider.AcceptDeliveryRequestDTO;
import com.delfood.dto.rider.DeliveryRiderDTO;
import com.delfood.dto.rider.AcceptDeliveryRequestDTO.RequestResult;
import com.delfood.dto.rider.DeliveryInfoDTO;
import com.delfood.dto.rider.DeliveryOrderInfo;
import com.delfood.dto.rider.RiderDTO;
import com.delfood.error.exception.DuplicateException;
import com.delfood.error.exception.TargetNotFoundException;
import com.delfood.service.OrderService;
import com.delfood.service.delivery.DeliveryService;
import com.delfood.service.rider.RiderInfoService;
import com.delfood.utils.SessionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import lombok.Builder;
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
import org.springframework.web.bind.annotation.PutMapping;
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
  
  @Autowired
  private OrderService orderService;
  
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
  
  /**
   * 라이더의 메일 주소를 변경한다.
   * @param session 현제 세션
   * @param request 요청 정보
   */
  @PatchMapping("mail")
  public void updateMail(HttpSession session, @RequestBody UpdateMailRequest request) {
    String id = SessionUtil.getLoginRiderId(session);
    riderInfoService.changeMail(id, request.getPassword(), request.getUpdateMail());
  }
  
  /**
   * 주문에 대한 배달 매칭 요청을 진행한다.
   * @param request 요청 정보
   * @param session 현재 세션
   * @return 
   */
  @PostMapping("delivery/accept")
  @LoginCheck(type = UserType.RIDER)
  public AcceptDeliveryRequestDTO deliveryAccept(@RequestBody DeliveryAcceptRequest request,
      HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    return deliveryService.acceptDeliveryRequest(riderId, request.getOrderId());
  }

  /**
   * 해당 주문의 배달을 완료했다는 요청을 받는다.
   * @author jun
   * @param request 요청 정보
   * @param session 현제 세션
   */
  @PatchMapping("delivery/complete")
  @LoginCheck(type = UserType.RIDER)
  public void deliveryComplete(@RequestBody DeliveryCompleteRequest request, HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    if (deliveryService.isRiderOrder(riderId, request.getOrderId()) == false) {
      log.info("주문 완료 권한 없음. 주문 아이디 : {}, 라이더 아이디 : {}", request.getOrderId(), riderId);
      throw new IllegalArgumentException("해당 라이더가 배달중인 주문이 아닙니다. 권한이 없습니다.");
    }
    deliveryService.deliveryComplete(request.getOrderId());
  }
  
  /**
   * 라이더의 현제 위치 정보를 업데이트한다.
   * 새로운 정보를 덧씌우는 것이기 때문에 put으로 매핑하였다.
   * @author jun
   * @param info 요청 정보
   * @param session 현재 세션
   */
  @PutMapping("delivery/available")
  @LoginCheck(type = UserType.RIDER)
  public void updateDeliveryRiderInfo(DeliveryRiderDTO info, HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    
    if (riderInfoService.hasDelivery(riderId)) {
      throw new DuplicateException("이미 진행중인 배달이 있습니다. 한번에 하나의 배달만 가능합니다.");
    }
    
    deliveryService.updateRider(info);
  }
  
  /**
   * 라이더를 배당 매칭 대기 명단에서 제거한다.
   * @author jun
   * @param session 현재 세션
   */
  @DeleteMapping("delivery/available")
  @LoginCheck(type = UserType.RIDER)
  public void deletDeliveryRiderInfo(HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    deliveryService.delete(riderId);
  }
  
  /**
   * 라이더의 모든 배달 현황을 확인한다.
   * @param session 현제 세션
   * @return
   */
  @GetMapping("delivery")
  @LoginCheck(type = UserType.RIDER)
  public List<DeliveryInfoDTO> getAllDeliveryList(Long lastViewedOrderId, HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    return deliveryService.getMyAllDeliveries(riderId, lastViewedOrderId);
  }
  
  /**
   * 현재 배달중인 주문 정보를 확인한다.
   * @param session 현제 세션
   * @return
   */
  @GetMapping("delivery/current")
  @LoginCheck(type = UserType.RIDER)
  public DeliveryInfoDTO getCurrentDelivery(HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    DeliveryInfoDTO currentDelivery = deliveryService.getCurrentDelivery(riderId);
    if (Objects.isNull(currentDelivery)) {
      throw new TargetNotFoundException("현재 라이더님은 배달중이 아닙니다.");
    }
    return currentDelivery;
  }
  
  /**
   * 오늘 배달한 배달료를 조회한다.
   * @param session 현제 세션
   * @return
   */
  @GetMapping("delivery/bills/today")
  @LoginCheck(type = UserType.RIDER)
  public TodayDeliveryBillsResponse getTodayDeliveyBills(HttpSession session) {
    String riderId = SessionUtil.getLoginRiderId(session);
    List<DeliveryOrderInfo> todayDeliveryBills = deliveryService.getTodayDeliveryBills(riderId);
    return TodayDeliveryBillsResponse.builder()
        .deliveries(todayDeliveryBills)
        .riderId(riderId)
        .build();
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
  
  @Getter
  private static class DeliveryCompleteRequest {
    @NonNull
    private Long orderId;
  }
  
  @Getter
  private static class TodayDeliveryBillsResponse {
    private List<DeliveryOrderInfo> deliveries;
    private long totalCost;
    private String riderId;
    
    @Builder
    public TodayDeliveryBillsResponse(@NonNull List<DeliveryOrderInfo> deliveries,
        @NonNull String riderId) {
      this.deliveries = deliveries;
      this.riderId = riderId;
      initCost();
    }
    
    private void initCost() {
      this.totalCost = deliveries.stream().mapToLong(e -> e.getDeliveryCost()).sum();
    }
  }
  
  
}
