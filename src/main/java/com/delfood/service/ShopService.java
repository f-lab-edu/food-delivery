package com.delfood.service;

import com.delfood.dto.ShopDTO;
import com.delfood.dto.ShopUpdateDTO;
import com.delfood.mapper.ShopMapper;
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
    return shopMapper.countByShopIdAndOwnerId(shopId, ownerId) == 1;
  }
  
  /**
   * 매장 정보 변경 메서드.
   * 변경 오류시 롤백한다.
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
}
