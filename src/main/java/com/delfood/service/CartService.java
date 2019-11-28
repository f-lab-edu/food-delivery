package com.delfood.service;

import com.delfood.dao.CartDao;
import com.delfood.dto.OrdersItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
  @Autowired
  private CartDao cartDao;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  /**
   * 장바구니에 메뉴를 저장한다.
   * 장바구니는 하나의 매장에 대한 메뉴만 저장이 가능하다.
   * 다른 매장 메뉴를 저장하려고 시도할 시 에러를 발생시킨다.
   * 
   * @author jun
   * @param item 저장할 아이템
   * @param memberId 고객 아이디
   */
  public void addOrdersItem(OrdersItemDTO item, String memberId) {
    if (item.hasNullDataBeforeInsertCart()) {
      throw new NullPointerException("필수 입력 데이터가 누락되었습니다.");
    }
    
    /*
     * 같은 매장의 메뉴인지 확인한다.
     * 장바구니에 insert할때마다 해당 유효성 검증을 진행하기 때문에,
     * 현재 장바구니에 존재하는 모든 메뉴는 같은 매장의 메뉴라는 것이 보장된다.
     * 그렇기 때문에 단 하나의 메뉴만 꺼내서 입력하려는 메뉴와 비교해도 모든 장바구니의 메뉴가 같은 매장의 것이라는 것을 보장받을 수 있다.
     */
    OrdersItemDTO peakData = cartDao.findPeekByMemberId(memberId);
    // 장바구니에 아이템이 존재할 시 검증 로직을 실행
    if (peakData != null) {
      if (item.getShopId().equals(peakData.getShopId()) == false) {
        throw new IllegalArgumentException("다른 매장의 메뉴를 함께 주문할 수 없습니다.");
      }
    }
    
    cartDao.addItem(item, memberId);
  }
  
  /**
   * 고객의 장바구니에 들어있는 메뉴, 옵션들을 모두 조회한다.
   * @param memberId 고객 아이디
   * @return
   */
  public List<OrdersItemDTO> getOrdersItems(String memberId) {
    return cartDao.findAllByMemberId(memberId);
  }

  /**
   * 고객 장바구니를 비운다.
   * @param memberId 고객 아이디
   */
  public void claer(String memberId) {
    cartDao.deleteByMemberId(memberId);
  }

  /**
   * 장바구니에서 특정 메뉴를 제거한다.
   * 특정 메뉴는 index번호로 조회할 수 있으며 index가 범위를 초과하였을 때 오류를 발생시킨다.
   * @param memberId 고객 아이디
   * @param index 제거할 메뉴의 인덱스
   */
  public void deleteCartMenu(String memberId, long index) {
    long menuCount = cartDao.getMenuCount(memberId);
    if (index >= menuCount + 1 || index < 0) {
      throw new IndexOutOfBoundsException("index의 범위가 초과되었습니다.");
    }
    if (cartDao.deleteByMemberIdAndIndex(memberId, index) == false) {
      throw new RuntimeException("삭제 실패!");
    }
    
  }
}
