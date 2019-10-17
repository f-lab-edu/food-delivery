package com.delfood.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

// 고객
@Getter
@Setter
@ToString
public class MemberDTO {
  public enum Status {
    DEFAULT, DELETED
  }

  // 아이디
  @NonNull
  private String id;
  // 패스워드
  @NonNull
  private String password;
  // 이름
  @NonNull
  private String name;
  // 핸드폰번호
  @NonNull
  private String tel;
  // 이메일
  @NonNull
  private String mail;
  // 상태
  private Status status;
  // 회원가입일
  private LocalDateTime createdAt;
  // 최종 수정일
  private LocalDateTime updatedAt;
  // 주소
  @Nullable
  private String address;
  // 상세 주소
  @Nullable
  private String addressDetail;


  /**
   * Member의 데이터를 방어적으로 복사한다
   * @param param 복사할 회원의 정보
   */
  public void copyData(MemberDTO param) {
    this.id = param.getId();
    this.password = param.getPassword();
    this.name = param.getName();
    this.tel = param.getTel();
    this.mail = param.getMail();
    this.status = param.getStatus();
    this.createdAt = param.getCreatedAt();
    this.updatedAt = param.getUpdatedAt();
  }
  
  /**
   * 회원가입 전 필수 데이터중 null값이 있는지 검사한다.
   * null값이 존재하여 회원가입 진행이 불가능 하다면 false를 반환한다.
   * 검사 후 이상이 없다면 true를 반환한다.
   * @param memberInfo 검사할 회원의 정보
   * @return
   */
  public static boolean hasNullDataBeforeSignup(MemberDTO memberInfo) {
    if (memberInfo.getId() == null || memberInfo.getPassword() == null
        || memberInfo.getName() == null || memberInfo.getTel() == null
        || memberInfo.getMail() == null) {
      return false;
    }

    return true;
  }


}
