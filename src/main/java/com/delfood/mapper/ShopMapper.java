package com.delfood.mapper;

import com.delfood.dto.ShopDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopMapper {
  public int insertShop(ShopDTO shopInfo);

  /**
   * 매장 상태를 'DELETE'로 바꾼다. <br>
   * <b><u>해당 메서드 사용 전 반드시 작업자의 권한을 먼저 확인하여야 한다.</u></b>
   * 
   * @author jun
   * @param shopId 삭제상태로 변경할 매장의 id
   * @return
   */
  public int deleteShop(Long shopId);

  /**
   * 매장의 정보를 변경한다. <br>
   * <b><u>해당 메서드 사용 전 반드시 작업자의 권한을 먼저 확인하여야 한다.</u></b>
   * 변경할 수 있는 정보 
   * 이름, 배달 형태, 주력 메뉴, 전화번호, 가게 소개, 
   * 최소 주문금액, 안내 및 혜택, 운영시간, 배달 지역, 
   * 주문 타입, 원산지 정보
   * 
   * @author jun
   * @param shopInfo 변경할 매장의 정보
   * @return
   */
  public int updateShop(ShopDTO shopInfo);
  
  /**
   * 매장 id로 매장 정보를 조회한다.
   * @author jun
   * @param id 조회할 매장의 id
   * @return
   */
  public ShopDTO findById(Long id);

  /**
   * 사장 id로 가지고 있는 매장을 모두 조회한다.<br>
   * 한 사장당 많은 매장을 가지기는 어려우므로 페이징 처리를 따로 하지는 않았다.
   * @author jun
   * @param ownerId 매장을 가진 사장의 id
   * @return
   */
  public List<ShopDTO> findByOwnerId(String ownerId);
}
