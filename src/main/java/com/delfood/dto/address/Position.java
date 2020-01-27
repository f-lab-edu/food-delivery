package com.delfood.dto.address;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Position {
  private double coordinateX;
  private double coordinateY;
  
  @Builder
  public Position(double coordinateX, double coordinateY) {
    this.coordinateX = coordinateX;
    this.coordinateY = coordinateY;
  }
  
  /**
   * 대상 위치와의 거리를 계산한다.
   * @author jun
   * @param position 거리를 계산할 위치
   * @return
   */
  public double distanceMeter(Position position) {
    return Math.sqrt(Math.pow(this.coordinateX - position.getCoordinateX(), 2)
        + Math.pow(this.coordinateY - position.getCoordinateY(), 2));
  }
}
