package com.delfood.mapper;

import com.delfood.dto.ShopCategoryDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopSearchMapper {
  /**
   * 모든 카테고리를 조회한다.
   * 카테고리는 16개를 초과하지 않는다.
   * @author jun
   * @return
   */
  public List<ShopCategoryDTO> findAll();
  
  /**
   * Id에 따른 카테고리를 조회한다.
   * @author jun
   * @return
   */
  public ShopCategoryDTO findById();
}
