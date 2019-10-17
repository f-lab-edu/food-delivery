package com.delfood.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuCategoryDTO {
  // 주력 메뉴
  // 한식, 분식, 카페, 일식,
  // 치킨, 피자, 아시안, 양식,
  // 중국집, 족발보쌈, 야식, 찜탕,
  // 도시락, 패스트푸드, 프렌차이즈
  
  // KOREAN, SCHOOL_FOOD, CAFE, JAPANESE,
  // CHICKEN, PIZZA, ASIAN, WESTERN,
  // CHINESE, BOSSAM, MIDNIGHT_MEAL, SOUP,
  // LUNCHBOX, FAST_FOOD, FRANCHISE
  private Long id;
  private String name;
}
