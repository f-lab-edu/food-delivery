package com.delfood.service;

import com.delfood.dto.MemberDTO;
import com.delfood.dto.MenuDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
import com.delfood.mapper.OrderMapper;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class OrderService {
  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private MemberService memberService;
  
  @Autowired
  private MenuService menuService;
  
  @Autowired
  private OptionService optionService;

  @Transactional
  public Long order(String memberId, List<OrderItemDTO> items) {
    // 주문 준비 작업. 결제 전, 거리계산 전
    Long orderId = preOrder(memberId, items);
    Long totalPrice = totalPrice(items);
    return totalPrice;
  }
  
  @Transactional
  private Long preOrder(String memberId, List<OrderItemDTO> items) {
    OrderDTO order = new OrderDTO();
    MemberDTO memberInfo = memberService.getMemberInfo(memberId);
    order.setMemberId(memberId);
    order.setAddressCode(memberInfo.getAddressCode());
    order.setAddressDetail(memberInfo.getAddressDetail());

    orderMapper.addOrder(order);
    Long orderId = order.getId();

    log.debug("addOrder Finished");
    log.debug("order id : {}", orderId);

    for (OrderItemDTO item : items) {
      item.setOrderId(orderId);

      orderMapper.addOrderItem(item);
      Long orderItemId = item.getId();
      log.debug("order item id : {}", orderItemId);

      orderMapper.addOrderItemOptions(item.getOptions(), orderItemId);
      log.debug("addOrderItemOption Finished");
    }
    log.debug("order successed");

    return order.getId();
  }
  
  
  public Long totalPrice(List<OrderItemDTO> items) {
    long totalPrice = 0L;
    for (OrderItemDTO item : items) {
      MenuDTO menuInfo = menuService.getMenuInfoWithOptios(item.getMenuId());
      long menuPrice = menuInfo.getPrice();
      log.debug("menu option : ", menuInfo.getOptionList());
      long optionPrice = optionService.totalPrice(item.getOptions());
                             
      log.info("menu info : {}", menuInfo);
      log.info("menu price : {}", menuPrice);
      log.info("option price: {}", optionPrice);
      totalPrice += (menuPrice + optionPrice) * item.getCount();
    }
    
    return totalPrice;
  }

}
