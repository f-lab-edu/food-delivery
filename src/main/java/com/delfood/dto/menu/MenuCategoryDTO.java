package com.delfood.dto.menu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuCategoryDTO {
  // 주력 메뉴
  // KOREAN, SCHOOL_FOOD, CAFE, JAPANESE,
  // CHICKEN, PIZZA, ASIAN, WESTERN,
  // CHINESE, BOSSAM, MIDNIGHT_MEAL, SOUP,
  // LUNCHBOX, FAST_FOOD, FRANCHISE
  private Long id;
  private String name;
}
