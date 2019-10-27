package com.delfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @Alias("menuGroup")
public class MenuGroupDTO {
  
 // 기본, 삭제
 public enum Status{
   DEFAULT, DELETED
 }
 
 private Long id; // 아이디
 
 @NonNull
 private String name; // 이름
 
 private String content; // 설명
 
 @NonNull
 private LocalDateTime createdAt; // 등록일
 
 @NonNull
 private LocalDateTime updatedAt; // 최종 수정일
 
 @NonNull
 private Status status; // 상태
 
 private Integer priority; // 우선순위
 
 @NonNull
 private Long shopId; // 가게 아이디
 
 private List<MenuDTO> menus; // 해당 메뉴 그룹에 포함된 메뉴들
 
}
