package com.delfood.dto.push;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PushMessageForTopic extends PushMessage{
  private String topic;
  
  @Builder
  public PushMessageForTopic(String topic, String title, String message) {
    super(title, message);
    this.topic = topic;
  }
}
