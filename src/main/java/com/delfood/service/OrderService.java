package com.delfood.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.delfood.dto.MemberDTO;
import com.delfood.dto.OrderFormDTO;

@Service
public class OrderService {

  @Autowired
  AddressService addressService;
  
  @Autowired
  MemberService memberService;
  
  /**
   * 주문 신청을 위한 기본 정보 조회.
   * 
   * @param memberId 회원 아이디
   * 
   * @return 주소, 상세주소, 총금액
   */
  public OrderFormDTO getOrderForm(String memberId) {
    MemberDTO memberInfo = memberService.getMemberInfo(memberId);
    return null;
  }
  
}
