package com.delfood.service;

import com.delfood.dto.ShopDTO;
import com.delfood.mapper.OperationResult;
import com.delfood.mapper.ShopMapper;
import lombok.extern.log4j.Log4j2;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  public void addShop(ShopDTO shopInfo) {
    int insertShop = shopMapper.insertShop(shopInfo);
    if (insertShop != 1) {
      log.error("insert ERROR - {}", shopInfo);
      throw new RuntimeException("Shop insert ERROR");
    }
  }

  /**
   * 사장님 가게들의 정보를 불러오는 메서드.
   * @param id 사장님 id
   * @param page 페이지 정보
   * @return
   */
  public List<ShopDTO> getMyShops(String id, Long page) {
    return shopMapper.findByOwnerId(id, page);
  }

  /**
   * 사장님이 가진 총 가게 개수를 불러오는 메서드.
   * @param id 사장님 아이디
   * @return
   */
  public long getMyShopCount(String id) {
    return shopMapper.countByOwnerId(id);
  }
}
