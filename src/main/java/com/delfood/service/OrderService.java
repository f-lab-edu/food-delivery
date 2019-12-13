package com.delfood.service;

import com.delfood.dto.AddressDTO;
import com.delfood.dto.ItemsBillDTO;
import com.delfood.dto.ItemsBillDTO.MenuInfo;
import com.delfood.dto.ItemsBillDTO.ShopInfo;
import com.delfood.dto.ItemsBillDTO.MenuInfo.OptionInfo;
import com.delfood.error.exception.order.TotalPriceMismatchException;
import com.delfood.dto.MemberDTO;
import com.delfood.dto.MenuDTO;
import com.delfood.dto.OptionDTO;
import com.delfood.dto.OrderDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
import com.delfood.mapper.OptionMapper;
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
  
  @Autowired
  private ShopService shopService;
  
  @Autowired
  private AddressService addressService;
  
  /**
   * <b>미완성 로직</b><br>
   * 주문 요청을 진행한다.
   * 사용자가 주문 요청시 전달받은 가격과, 서버에서 직접 비교한 가격을 비교하여 다르면 예외처리 할 예정.
   * @param memberId 고객 아이디
   * @param items 주문한 아이템들
   * @return
   */
  @Transactional
  public void order(String memberId, List<OrderItemDTO> items, long totalPriceFromClient) {
    // 클라이언트가 계산한 금액과 서버에서 계산한 금액이 같은지 비교
    long totalPriceFromServer = totalPrice(items);
    if (totalPriceFromServer != totalPriceFromClient) {
      log.error("Total Price Mismatch! client price : {}, server price : {}",
          totalPriceFromClient,
          totalPriceFromServer);
      throw new TotalPriceMismatchException("Total Price Mismatch!");
    }
    
    // 주문 준비 작업. 결제 전.
    Long orderId = preOrder(memberId, items);
    
    // 결제 진행
    
    
    // 계산서 발행
    
    
    // 사장님에게 알림(푸시)
    
  }
  
  /**
   * 주문 테이블에 insert를 진행한다.
   * 주문 메뉴, 주문 옵션이 추가된다.
   * 
   * @param memberId 고객 아이디
   * @param items 주문할 아이템들
   * @return
   */
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
  
  
  /**
   * 고객이 선정한 메뉴의 총 계산서를 출력한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param items 주문 상품들. 메뉴, 옵션 리스트가 존재한다.
   * @return
   */
  public ItemsBillDTO getBill(String memberId, List<OrderItemDTO> items) {
    // 고객 주소 정보 추출
    AddressDTO addressInfo = memberService.getMemberInfo(memberId).getAddressInfo();
    // 매장 정보 추출
    ShopInfo shopInfo = shopService.getShopByMenuId(items.get(0).getMenuId());
    // 거리 계산
    double distanceMeter =
        addressService.getDistanceMeter(shopInfo.getAddressCode(),
            addressInfo.getBuildingManagementNumber());
    ItemsBillDTO bill = new ItemsBillDTO(memberId, addressInfo, shopInfo, distanceMeter);
    
    for (OrderItemDTO item : items) {
      MenuDTO menuInfo = menuService.getMenuInfo(item.getMenuId());
      MenuInfo billMenuInfo = MenuInfo.builder()
          .id(menuInfo.getId())
          .name(menuInfo.getName())
          .price(menuInfo.getPrice())
          .build();
      for (OrderItemOptionDTO orderItemOption : item.getOptions()) {
        OptionDTO optionInfo = optionService.getOptionInfo(orderItemOption.getOptionId());
        OptionInfo billOptionInfo = OptionInfo.builder()
                                              .id(optionInfo.getId())
                                              .name(optionInfo.getName())
                                              .price(optionInfo.getPrice())
                                              .build();
        billMenuInfo.getOptions().add(billOptionInfo);
      }
      bill.getMenus().add(billMenuInfo);
    }
    return bill;
  }
  
  /**
   * 아이템들의 총 가격을 계산한다.
   * @author jun
   * @param items 계산할 아이템들
   * @return 총 가격
   */
  public long totalPrice(List<OrderItemDTO> items) {
    long totalPrice = 0L;
    for (OrderItemDTO item : items) {
      long optionsPrice = 0L;
      MenuDTO menuInfo = menuService.getMenuInfo(item.getMenuId());
      for (OrderItemOptionDTO orderItemOption : item.getOptions()) {
        OptionDTO optionInfo = optionService.getOptionInfo(orderItemOption.getOptionId());
        optionsPrice += optionInfo.getPrice();
      }
      long menuPrice = menuInfo.getPrice();
      totalPrice += optionsPrice + menuPrice;
    }
    return totalPrice;
  }
  
  public double addressDistance(String startAddressCode, String endAddressCode) {
    return addressService.getDistanceMeter(startAddressCode, endAddressCode);
  }
  

}
