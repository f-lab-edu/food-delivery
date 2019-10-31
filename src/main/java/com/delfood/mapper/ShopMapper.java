package com.delfood.mapper;

import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopUpdateDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopMapper {
  /**
   * 매장 입점 신청을 한다.
   * @author jun
   * @param shopInfo 입점 신청 매장 정보
   * @return
   */
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
   * @param updateInfo 변경할 매장의 정보
   * @return
   */
  public int updateShop(ShopUpdateDTO updateInfo);
  
  /**
   * 매장 id로 매장 정보를 조회한다.
   * @author jun
   * @param id 조회할 매장의 id
   * @return
   */
  public ShopDTO findById(Long id);

  /**
   * 사장 id로 가지고 있는 매장을 조회한다.<br>
   * @author jun
   * @param ownerId 매장을 가진 사장의 id
   * @param lastId 페이지
   * @return
   */
  public List<ShopDTO> findByOwnerId(String ownerId, Long lastId);

  /**
   * 사장 id로 가지고 있는 매장의 개수를 조회한다.
   * @author jun
   * @param ownerId 사장님 id
   * @return 매장 개수
   */
  public long countByOwnerId(String ownerId);

  
  /**
   * 매장 주인이 사장님이 맞는지 확인한다.
   * @param shopId 매장 id
   * @param ownerId 사장님 id
   * @return
   */
  public long countByShopIdAndOwnerId(Long shopId, String ownerId);
  
  /**
   * 매장 상태를 OPEN으로 변경한다.
   * @param shopId 오픈할 매장의 id
   * @return
   */
  public int updateShopOpenById(Long shopId);

  /**
   * 매장 상태를 CLOSE로 변경한다.
   * @author jun
   * @param shopId 닫을 매장의 id
   * @return
   */
  public int updateShopCloseById(Long shopId);
  
  /**
   * 해당 매장이 오픈상태인지 아닌지 확인하는 메서드.
   * @author jun
   * @param shopId 오픈 상태를 확인할 매장의 id
   * @return workCondition이 OPEN이 아닐 때 1
   */
  public long countByIdIsNotOpen(Long shopId);

  /**
   * 배달 지역을 추가한다.
   * @author jun
   * @param shopId 배달 지역을 추가할 매장의 id
   * @param townCode 배달 지역(읍면동코드)
   */
  public int insertDeliveryLocation(Long shopId, String townCode);
  
  
  /**
   * 해당 배달 지역으로 매장을 조회하고, 그 매장의 주인이 해당 사용자인지 확인한다. 
   * 1이 나오면 주인이 맞다.
   * @author jun
   * @param deliveryLocationId 배달 지역 id
   * @param ownerId 사장님 아이디
   * @return
   */
  public long countByOwnerIdAndDeliveryLocationId(Long deliveryLocationId,String ownerId);

  /**
   * 배달지역 삭제 메서드.
   * @param deliveryLocationId 삭제할 배달 지역 아이디
   * @return
   */
  public int deleteDeliveryLocation(Long deliveryLocationId);

  /**
   * 해당 카테고리를 가지는 매장 정보를 가져온다.
   * 매장에서 설정한 배달 가능 지역에 포함되어 있어야 조회된다.
   * @param categoryId 검색할 음식 카테고리
   * @param townCode 검색할 배달 지역
   * @return
   */
  public List<ShopDTO> findByCategoryIdAndTownCode(Long categoryId, String townCode);


  public List<ShopDTO> findByBeOpen(String ownerId);

  public List<ShopDTO> findByBeClose(String ownerId);

}
