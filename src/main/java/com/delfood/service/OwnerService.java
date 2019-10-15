package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.DMLOperationError;
import com.delfood.mapper.OwnerMapper;

@Service
public class OwnerService {
  @Autowired
  OwnerMapper ownerMapper;

  /**
   * 사장님 회원 가입 메서드
   * @author jun
   * @param ownerInfo 가입할 사장님의 정보
   * @return 가입후 성공 여부. 실패한다면 throw Exception된다.
   */
  public DMLOperationError signUp(OwnerDTO ownerInfo) {
    int insertOwnerResult = ownerMapper.insertOwner(ownerInfo);
    if (insertOwnerResult == 1) {
      return DMLOperationError.SUCCESS;
    } else {
      throw new RuntimeException("insert Owner ERROR " + ownerInfo);
    }
  }
  
  /**
   * 사장님 id 중복 체크 메서드
   * @author jun
   * @param id 중복 체크할 id
   * @return 중복 id일시 true
   */
  public boolean isDuplicatedId(String id) {
    return ownerMapper.idCheck(id) == 1;
  }
}
