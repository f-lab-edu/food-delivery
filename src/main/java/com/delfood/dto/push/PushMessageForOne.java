package com.delfood.dto.push;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.joda.time.LocalDateTime;

@Getter
public class PushMessageForOne extends PushMessage {
  @NonNull
  private String token;
  
  @Builder
  public PushMessageForOne(String token, String title, String message) {
    super(title, message);
    this.token = token;
  }
}
