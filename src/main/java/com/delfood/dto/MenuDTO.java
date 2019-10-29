package com.delfood.dto;

import java.time.LocalDateTime;
import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Alias("menu")
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
  
  @NonNull
  private LocalDateTime createdAt; // 등록일
  
  @NonNull
  private LocalDateTime updatedAt; // 최종 수정일
  
  @NonNull
  private Status status; // 상태
  
  private Long priority; // 우선순위
  
  private String content; // 설명
  
  private Long menuGroupId; // 메뉴 그룹 아이디
  
}
