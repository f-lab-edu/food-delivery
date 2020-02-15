package com.delfood.dto.menu;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.apache.ibatis.type.Alias;
import com.delfood.dto.menu.MenuDTO.Status;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Alias("option")
public class OptionDTO {
  
  public enum Status {
    DEFAULT, DELETED
  }
  
  @NonNull
  private Long id; // 아이디  
  
  @NonNull
  private String name; // 이름 
  
  @NonNull
  private Long price; // 가격    
  
  private Status status; // 상태
  
  private Long menuId; // 메뉴 아이디
  
  /**
   * 옵션 추가 시 null 이 있는 지 확인.
   * 
   * @param optionInfo 옵션정보
   * @return
   */
  public static boolean hasNullDataBeforeCreate(OptionDTO optionInfo) {
    if (optionInfo.getName() == null
        || optionInfo.getPrice() == null
        || optionInfo.getMenuId() == null) {
      return true;
    }
    return false;
  }
}
