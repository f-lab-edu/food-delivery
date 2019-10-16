package com.delfood.mapper;

import com.delfood.dto.ShopDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopMapper {
  public int insertShop(ShopDTO shopInfo);
}
