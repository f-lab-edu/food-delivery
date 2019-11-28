package com.delfood.controller;

import com.delfood.aop.MemberLoginCheck;
import com.delfood.dto.ShopCategoryDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.service.MemberService;
import com.delfood.service.ShopSearchService;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import java.util.List;
import javax.servlet.http.HttpSession;
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

  @Autowired
  private MemberService memberService;

  /**
   * 메인화면에서 큰 카테고리들을 조회한다. Ex) 치킨, 피자, 중국집 등
   * 
   * @author jun
   * @return
   */
  @GetMapping
  public GetShopCategoriesResponse getShopCategories() {
    List<ShopCategoryDTO> categories = shopSearchService.getCategories();
    return new GetShopCategoriesResponse(categories);
  }

  /**
   * 해당 지역 매장들 중 해당 카테고리를 가지고 있고 해당 지역에 배달을 갈 수 있으며 현제 OPEN중인 매장을 조회한다.
   * 
   * @author jun
   * @param categoryId 조회하고자 하는 매장들의 카테고리 아이디
   * @param session 접속한 사용자의 세션
   * @return
   */
  @GetMapping("/available/shops")
  @MemberLoginCheck
  public GetShopByCategoryIdAndTownCodeResponse getShopsByCategoryIdAndTownCode(
      @RequestParam(required = true) Long categoryId, HttpSession session) {
    String memberId = SessionUtil.getLoginMemberId(session);
    String townCode = memberService.getTownCode(memberId);

    return new GetShopByCategoryIdAndTownCodeResponse(
            shopSearchService.shopSearchByCategory(categoryId, townCode));
  }



  // ----------------------- Response 객체 -----------------------
  @Getter
  @AllArgsConstructor
  private static class GetShopCategoriesResponse {
    private List<ShopCategoryDTO> categories;
  }

  @Getter
  @AllArgsConstructor
  private static class GetShopByCategoryIdAndTownCodeResponse {
    private List<ShopDTO> shopsInfo;
  }


}
