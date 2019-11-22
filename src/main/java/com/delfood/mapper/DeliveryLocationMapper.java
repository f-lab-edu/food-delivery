package com.delfood.mapper;

import com.delfood.dto.DeliveryLocationDTO;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryLocationMapper {

  DeliveryLocationDTO findById(Long id);

  /**
   * 배달 지역을 추가한다.
   * 
   * @author jun
   * @param shopId 배달 지역을 추가할 매장의 id
   * @param townCodes 배달 지역들(읍면동코드)
   */
  public int insertDeliveryLocation(Long shopId, Set<String> townCodes);


  /**
   * 해당 배달 지역으로 매장을 조회하고, 그 매장의 주인이 해당 사용자인지 확인한다. 1이 나오면 주인이 맞다.
   * 
   * @author jun
   * @param deliveryLocationId 배달 지역 id
   * @param ownerId 사장님 아이디
   * @return
   */
  public long countByOwnerIdAndDeliveryLocationId(Long deliveryLocationId, String ownerId);

  /**
   * 배달지역 삭제 메서드.
   * 
   * @param deliveryLocationId 삭제할 배달 지역 아이디
   * @return
   */
  public int deleteDeliveryLocation(Long deliveryLocationId);

  /**
   * 매장의 모든 배달 가능 지역을 조회한다.
   * @author jun
   * @param shopId 배달가능지역을 조회할 매장의 id
   * @return
   */
  public List<DeliveryLocationDTO> findByShopId(Long shopId);
  
}
