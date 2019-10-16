package com.delfood.controller;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.DMLOperationError;
import com.delfood.service.OwnerService;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners/")
public class OwnerController {
  @Autowired
  private OwnerService ownerService;

  /**
   * 사장님 회원가입 메서드.
   * 
   * @author jun
   * @param ownerInfo 회원가입할 사장님 정보
   */
  @PostMapping
  public ResponseEntity<SignUpResponse> signUp(@RequestBody OwnerDTO ownerInfo) {
    if (OwnerDTO.hasNullDataBeforeSignUp(ownerInfo)) {
      throw new NullPointerException("사장님 회원가입에 필요한 정보에 NULL이 존재합니다.");
    }

    // id 중복체크
    if (ownerService.isDuplicatedId(ownerInfo.getId())) {
      return new ResponseEntity<OwnerController.SignUpResponse>(SignUpResponse.ID_DUPLICATED,
          HttpStatus.CONFLICT);
    }

    DMLOperationError signUpResult = ownerService.signUp(ownerInfo);
    if (signUpResult == DMLOperationError.SUCCESS) {
      return new ResponseEntity<OwnerController.SignUpResponse>(SignUpResponse.SUCCESS,
          HttpStatus.CREATED);
    } else {
      throw new RuntimeException("알 수 없는 오류 발생 :" + ownerInfo);
    }
  }

  /**
   * id 중복 체크 메서드.
   * 
   * @author jun
   * @param id 중복체크를 진행할 사장님 ID
   * @return 중복된 아이디 일시 true
   */
  @GetMapping("idCheck/{id}")
  public ResponseEntity<IdDuplResponse> idCheck(@PathVariable("id") String id) {
    boolean isDupl = ownerService.isDuplicatedId(id);
    if (isDupl) {
      return new ResponseEntity<OwnerController.IdDuplResponse>(IdDuplResponse.ID_DUPLICATED,
          HttpStatus.CONFLICT);
    } else {
      return new ResponseEntity<OwnerController.IdDuplResponse>(IdDuplResponse.SUCCESS,
          HttpStatus.OK);
    }
  }



  // Response 객체
  @Getter
  @RequiredArgsConstructor
  private static class SignUpResponse {
    enum SignUpStatus {
      SUCCESS, ID_DUPLICATED
    }

    @NonNull
    private SignUpStatus result;

    private static final SignUpResponse SUCCESS = new SignUpResponse(SignUpStatus.SUCCESS);
    private static final SignUpResponse ID_DUPLICATED =
        new SignUpResponse(SignUpStatus.ID_DUPLICATED);
  }

  @Getter
  @RequiredArgsConstructor
  private static class IdDuplResponse {
    enum DuplStatus {
      SUCCESS, ID_DUPLICATED
    }

    @NonNull
    private DuplStatus result;

    private static final IdDuplResponse SUCCESS = new IdDuplResponse(DuplStatus.SUCCESS);
    private static final IdDuplResponse ID_DUPLICATED =
        new IdDuplResponse(DuplStatus.ID_DUPLICATED);
  }



  // Request 객체


}
