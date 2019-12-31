package com.delfood.dto.push;

import lombok.Getter;
import lombok.NonNull;
import org.joda.time.LocalDateTime;

@Getter
public class PushMessage {
  @NonNull
  private String title;
  @NonNull
  private String message;
  
  private LocalDateTime generatedTime;
  
  public PushMessage(String title, String message) {
    this.title = title;
    this.message = message;
    this.generatedTime = LocalDateTime.now();
  }
}
