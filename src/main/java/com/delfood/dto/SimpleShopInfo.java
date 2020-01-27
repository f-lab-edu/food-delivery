package com.delfood.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class SimpleShopInfo {
  private String id;
  private String name;
  
  @Builder
  public SimpleShopInfo(@NonNull String id, @NonNull String name) {
    this.id = id;
    this.name = name;
  }
}
