package com.delfood.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
public class OwnerDTO {
  public enum Status {
    DEFAULT, DELETED
  }
  
  @NonNull
  private String id;
  @NonNull
  private String password;
  @NonNull
  private String name;
  @NonNull
  private String mail;
  @NonNull
  private String tel;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Status status;
  
  
}
