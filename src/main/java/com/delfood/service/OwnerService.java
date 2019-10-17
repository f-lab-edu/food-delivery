package com.delfood.service;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.OwnerMapper;
import com.delfood.utils.SHA256Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OwnerService {
  @Autowired
  OwnerMapper ownerMapper;

  /**
   * 사장님 회원 가입 메서드
   * 
   * @author jun
   * @param ownerInfo 가입할 사장님의 정보
   */
  public void signUp(OwnerDTO ownerInfo) {
    String cryptoPassword = SHA256Util.encryptSHA256(ownerInfo.getPassword());
    ownerInfo.setPassword(cryptoPassword);
    int insertOwnerResult = ownerMapper.insertOwner(ownerInfo);
    if (insertOwnerResult != 1) {
      log.error("insert Owner ERROR : {}", ownerInfo);
      throw new RuntimeException("insert Owner ERROR " + ownerInfo);
    }
  }

  /**
   * 사장님 id 중복 체크 메서드
   * 
   * @author jun
   * @param id 중복 체크할 id
   * @return 중복 id일시 true
   */
  public boolean isDuplicatedId(String id) {
    return ownerMapper.idCheck(id) == 1;
  }
}
