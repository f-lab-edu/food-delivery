package com.delfood.service;

import com.delfood.dto.ShopDTO;
import com.delfood.mapper.DMLOperationResult;
import com.delfood.mapper.ShopMapper;
import lombok.extern.log4j.Log4j2;
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
  public void newShop(ShopDTO shopInfo) {
    int insertShop = shopMapper.insertShop(shopInfo);
    if (insertShop != 1) {
      log.error("insert ERROR - {}", shopInfo);
      throw new RuntimeException("Shop insert ERROR");
    }
  }
}
