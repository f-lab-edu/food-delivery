package com.delfood.controller;

import com.delfood.dto.ShopCategoryDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.service.ShopSearchService;
import com.delfood.service.ShopService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories/")
public class ShopSearchController {
  @Autowired
  private ShopSearchService shopSearchService;

  @Autowired
  private ShopService shopService;

  /**
   * 메인화면에서 큰 카테고리들을 조회한다. Ex) 치킨, 피자, 중국집 등
   * 
   * @author jun
   * @return
   */
  @GetMapping
  public ResponseEntity<GetShopCategoriesResponse> getShopCategories() {
    List<ShopCategoryDTO> categories = shopSearchService.getCategories();
    return new ResponseEntity<ShopSearchController.GetShopCategoriesResponse>(
        new GetShopCategoriesResponse(categories), HttpStatus.OK);
  }

  /**
   * 해당 지역 매장들 중 해당 카테고리를 가지고 있고 해당 지역에 배달을 갈 수 있으며
   * 현제 OPEN중인 매장을 조회한다.
   * @author jun
   * @param categoryId 조회하고자 하는 매장들의 카테고리 아이디
   * @param townCode 조회하고자 하는 지역 번호(읍면동 코드)
   * @return
   */
  @GetMapping("shops")
  public List<ShopDTO> getShopsByCategoryIdAndTownCode(
      @RequestParam(required = true) Long categoryId,
      @RequestParam(required = true) String townCode) {
    return shopService.findByCategoryIdAndTownCode(categoryId, townCode);
  }



  // ----------------------- Response 객체 -----------------------
  @Getter
  @AllArgsConstructor
  private static class GetShopCategoriesResponse {
    private List<ShopCategoryDTO> categories;
  }


}
