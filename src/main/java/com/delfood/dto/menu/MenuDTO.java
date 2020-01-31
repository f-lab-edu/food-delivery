package com.delfood.dto.menu;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.type.Alias;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("menu")
@EqualsAndHashCode(of = {"id"})
public class MenuDTO {
  
  // 기본, 삭제, 숨김, 품절
  public enum Status {
    DEFAULT, DELETED, HIDDEN, SOLDOUT
  }
  
  private Long id; // 아이디
  
  @NonNull
  private String name; // 이름
  
  private Long price; // 가격
  
  private String photo; // 사진 (경로 저장)
  
  private LocalDateTime createdAt; // 등록일
  
  private LocalDateTime updatedAt; // 최종 수정일
  
  private Status status; // 상태
  
  private Long priority; // 우선순위
  
  private String content; // 설명
  
  private Long menuGroupId; // 메뉴 그룹 아이디
  
  private List<OptionDTO> optionList; // 옵션 리스트
  
  /**
   * 메뉴 등록 필수 조건인 이름, 가격, 메뉴 그룹 아이디가 있는지 없는지 확인한다.
   * 
   * @author jinyoung
   * 
   * @param menuInfo 메뉴 등록에 필요한 정보
   * @return
   */
  public static boolean hasNullDataBeforeAdd(MenuDTO menuInfo) {
    return menuInfo.getName() == null || menuInfo.getPrice() == null
        || menuInfo.getMenuGroupId() == null;
  }
}
