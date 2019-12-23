package com.delfood.dto.address;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Position {
  private double xPos;
  private double yPos;
  
  @Builder
  public Position(double xPos, double yPos) {
    this.xPos = xPos;
    this.yPos = yPos;
  }
  
  /**
   * 대상 위치와의 거리를 계산한다.
   * @author jun
   * @param position 거리를 계산할 위치
   * @return
   */
  public double distanceMeter(Position position) {
    return Math.sqrt(
        Math.pow(this.xPos - position.getXPos(), 2) + Math.pow(this.yPos - position.getYPos(), 2));
  }
}
