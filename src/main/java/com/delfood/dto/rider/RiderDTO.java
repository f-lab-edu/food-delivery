package com.delfood.dto.rider;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.delfood.utils.SHA256Util;

@Getter
@NoArgsConstructor
public class RiderDTO {
  @NonNull
  private String id;
  
  @NonNull
  private String password;
  
  @NonNull
  private String name;
  
  @NonNull
  private String tel;
  
  @NonNull
  private String mail;
  
  @Nullable
  private Status status = Status.DEFAULT;
  
  @Nullable
  private LocalDateTime createdAt;
  
  @Nullable
  private LocalDateTime updatedAt;
  
  
  public enum Status {
    DEFAULT, DELETED
  }

  /**
   * RiderDTO Class Builder.
   * @param id 아이디
   * @param password 비밀번호
   * @param name 이름
   * @param tel 휴대전화 번호
   * @param mail 메일
   * @param status 계정 상태
   * @param createdAt 회원가입일
   * @param updatedAt 회원정보 수정일
   */
  @Builder
  public RiderDTO(String id, String password, String name, String tel, String mail, Status status,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.password = password;
    this.name = name;
    this.tel = tel;
    this.mail = mail;
    this.status = status == null ? Status.DEFAULT : status;
    this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
  }
  
  /**
   * null이 허용되지 않는 필드에 null값이 있는지 확인한다.
   * @author jun
   * @return
   */
  public boolean hasNullData() {
    return  Objects.isNull(this.id)
        ||  Objects.isNull(this.password)
        ||  Objects.isNull(this.name)
        ||  Objects.isNull(this.tel)
        ||  Objects.isNull(this.mail);
  }
  
  /**
   * 객체를 복사하여 패스워드를 암호화한 객체를 생성하여 리턴한다.
   * @author jun
   * @param riderInfo 암호화할 회원 정보
   * @return
   */
  public static RiderDTO encryptDTO(RiderDTO riderInfo) {
    String encryptPassword = SHA256Util.encryptSHA256(riderInfo.getPassword());
    return RiderDTO.builder()
        .id(riderInfo.getId())
        .password(encryptPassword)
        .name(riderInfo.getName())
        .tel(riderInfo.getTel())
        .mail(riderInfo.getMail())
        .status(riderInfo.getStatus())
        .createdAt(riderInfo.getCreatedAt())
        .updatedAt(riderInfo.getUpdatedAt())
        .build();
  }
  
  @JsonIgnore
  public String getPassword() {
    return this.password;
  }
}
