package com.delfood.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class CommonResponse {
  public enum Result {
    SUCCESS, NO_LOGIN, UNAUTHORIZED, FAIL
  }

  @NonNull
  private Result result;
  
  public static final CommonResponse SUCCESS = new CommonResponse(Result.SUCCESS);
  public static final CommonResponse NO_LOGIN = new CommonResponse(Result.NO_LOGIN);
  public static final CommonResponse UNAUTHORIZED = new CommonResponse(Result.UNAUTHORIZED);
  
  public static final ResponseEntity<CommonResponse> SUCCESS_RESPONSE = new ResponseEntity<CommonResponse>(SUCCESS, HttpStatus.OK);
  public static final ResponseEntity<CommonResponse> NO_LOGIN_RESPONSE = new ResponseEntity<CommonResponse>(NO_LOGIN, HttpStatus.UNAUTHORIZED);
  public static final ResponseEntity<CommonResponse> UNAUTHORIZED_RESPONSE = new ResponseEntity<CommonResponse>(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
  
  public CommonResponse(){
    this.result = Result.SUCCESS;
  }
}
