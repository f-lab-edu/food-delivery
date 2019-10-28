package com.delfood.service;

import com.delfood.dto.AddressDTO;
import com.delfood.dto.DeliveryLocationDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopUpdateDTO;
import com.delfood.mapper.ShopMapper;
import com.delfood.mapper.WorkMapper;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class ShopService {
  @Autowired
  private ShopMapper shopMapper;

  @Autowired
  private WorkService workService;
  
  @Autowired
  private AddressService addressService;

  /**
   * 매장 정보 삽입 메서드.
   * 
   * @author jun
   * @param shopInfo 삽입할 매장의 데이터
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addShop(ShopDTO shopInfo) {
    int insertShop = shopMapper.insertShop(shopInfo);
    if (insertShop != 1) {
      log.error("insert ERROR - {}", shopInfo);
      throw new RuntimeException("Shop insert ERROR");
    }
  }

  /**
   * 사장님 가게들의 정보를 불러오는 메서드.
   * 
   * @param ownerId 사장님 id
   * @param lastId 마지막으로 조회한 매장 id
   * @return
   */
  public List<ShopDTO> getMyShops(String ownerId, Long lastId) {
    return shopMapper.findByOwnerId(ownerId, lastId);
  }

  /**
   * 사장님이 가진 총 가게 개수를 불러오는 메서드.
   * 
   * @param ownerId 사장님 아이디
   * @return
   */
  public long getMyShopCount(String ownerId) {
    return shopMapper.countByOwnerId(ownerId);
  }

  /**
   * 매장 주인이 사장님인지 조회한다.
   * 
   * @param shopId 매장 id
   * @param ownerId 사장님 id
   * @return 주인이 맞으면 true
   */
  public boolean isShopOwner(Long shopId, String ownerId) {
    if (shopId == null || ownerId == null) {
      log.error("isShopOwner ERROR! id is null. shopId : {}, ownerId : {}", shopId, ownerId);
      throw new NullPointerException("isShopOwner ERROR!");
    }
    return shopMapper.countByShopIdAndOwnerId(shopId, ownerId) == 1L;
  }

  /**
   * 매장 정보 변경 메서드. 변경 오류시 롤백한다.
   * 
   * @author jun
   * @param updateInfo 변경할 정보
   */
  @Transactional
  public void updateShop(ShopUpdateDTO updateInfo) {
    int updateResult = shopMapper.updateShop(updateInfo);
    if (updateResult != 1) {
      log.error("update shop ERROR! updateInfo : {}", updateInfo);
      throw new RuntimeException("shop update ERROR!");
    }
  }

  /**
   * 매장 영업 시작 메서드. 영업 시작을 기록하고 매장의 상태를 OPEN으로 변경한다.
   * 
   * @author jun
   * @param shopId 영업을 시작할 매장 id
   */
  @Transactional
  public void openShop(Long shopId) {
    workService.addWork(shopId);
    int openResult = shopMapper.updateShopOpenById(shopId);
    if (openResult != 1) {
      log.error("open shop ERROR! shopId : {}, openResult : {}", shopId, openResult);
      throw new RuntimeException("open shop ERROR!");
    }
  }

  /**
   * 매장 영업 종료 메서드. 영업 종료를 기록하고 매장의 상태를 CLOSE로 변경한다.
   * 
   * @author jun
   * @param shopId 영업을 종료할 매장 id
   */
  @Transactional
  public void closeShop(Long shopId) {
    workService.closeWork(shopId);
    int closeResult = shopMapper.updateShopCloseById(shopId);
    if (closeResult != 1) {
      log.error("close Shop ERROR! shopId : {}, closeResult : {}", shopId, closeResult);
    }

  }

  /**
   * 매장이 OPEN상태가 아닌지 체크하는 메서드. 매장이 OPEN이 아니라면 닫을 수 없기 때문에 체크한다.
   * 
   * @author jun
   * @param shopId 체크할 매장의 id
   * @return 매장이 오픈상태가 아니라면 true
   */
  public boolean notOpenCheck(Long shopId) {
    long isNotOpenResult = shopMapper.countByIdIsNotOpen(shopId);
    return isNotOpenResult == 1;
  }

  /**
   * 배달가능지역 추가 메서드.
   * 
   * @author jun
   * @param shopId 배달 지역을 추가할 매장의 id
   * @param townCode 읍면동코드. ADDRESS PK의 첫 10자리와 같다
   */
  @Transactional
  public void addDeliveryLocation(Long shopId, String townCode) {
    int result = shopMapper.insertDeliveryLocation(shopId, townCode);
    if (result != 1) {
      log.error("addDelivertLocation ERROR! shopId : {}, townCode : {}, result : {}", shopId,
          townCode, result);
      throw new RuntimeException("addDelivertLocation ERROR!");
    }

  }

  /**
   * 해당 배달 지역을 삭제할 권한이 사용자에게 있는지 검사하는 메서드.
   * 
   * @author jun
   * @param deliveryLocationId 배달 지역 id
   * @param ownerId 사장님 id
   * @return 권한이 있다면 true
   */
  public boolean isShopOwnerByDeliveryLocationId(Long deliveryLocationId, String ownerId) {
    long result = shopMapper.countByOwnerIdAndDeliveryLocationId(deliveryLocationId, ownerId);
    return result == 1;
  }
  
  /**
   * 배달 지역을 삭제한다.
   * @author jun
   * @param deliveryLocationId 삭제할 배달 지역 id
   */
  @Transactional
  public void deleteDeliveryLocation(Long deliveryLocationId) {
    int result = shopMapper.deleteDeliveryLocation(deliveryLocationId);
    if (result != 1) {
      throw new RuntimeException("delete Deliveery Location ERROR");
    }
  }

  public ShopDTO getShop(Long shopId) {
    return shopMapper.findById(shopId);
  }

  public List<AddressDTO> getDeliveryLocations(Long shopId) {
    return addressService.getTownInfoByShopId(shopId);
  }
}
