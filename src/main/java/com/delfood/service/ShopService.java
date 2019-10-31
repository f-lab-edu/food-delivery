package com.delfood.service;

import com.delfood.dto.ShopDTO;
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
   * @param ownerId 사장님 id
   * @param lastId 마지막으로 조회한 매장 id
   * @return
   */
  public List<ShopDTO> getMyShops(String ownerId, Long lastId) {
    return shopMapper.findByOwnerId(ownerId, lastId);
  }

  /**
   * 사장님이 가진 총 가게 개수를 불러오는 메서드.
   * @param ownerId 사장님 아이디
   * @return
   */
  public long getMyShopCount(String ownerId) {
    return shopMapper.countByOwnerId(ownerId);
  }
  
  /**
   * 한 가게의 정보를 불러오는 메소드.
   * 
   * @author jinyoung
   * 
   * @param id 가게 아이디
   * @return
   */
  public ShopDTO getMyShopInfo(Long id) {
    return shopMapper.findById(id);
  }
  
  /**
   * 메뉴 그룹 추가 시 검증 메서드.
   * 사장님 아이디와 매장 아이디가 일치하는 매장이 존재하는 지 조회한다.
   * 
   * @author jinyoung 
   * 
   * @param ownerId 사장 아이디
   * @param shopId 매장 아이디
   * @return
   */
  public boolean checkShopId(String ownerId, Long shopId) {
    return shopMapper.countByOwnerIdAndShopId(ownerId, shopId) != 1;
  }
  
}