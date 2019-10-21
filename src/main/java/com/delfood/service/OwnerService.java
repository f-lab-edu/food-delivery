package com.delfood.service;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.OperationResult;
import com.delfood.mapper.OwnerMapper;
import com.delfood.utils.SHA256Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
  
  /**
   * 사장 정보 조회.
   * 
   * @param id 아이디
   * @param password 패스워드
   * @return  id, name, mail, tel, createAt, updatedAt, status
   */
  public OwnerDTO getOwner(String id, String password) {
    String cryptoPassword = SHA256Util.encryptSHA256(password);
    OwnerDTO ownerInfo = ownerMapper.findByIdAndPassword(id, cryptoPassword);
    return ownerInfo;
  }

  /**
   * 사장 정보 조회.
   * 
   * @param id 아이디
   * @return id, name, mail, tel, createAt, updatedAt, status
   */
  public OwnerDTO getOwner(String id) {
    return ownerMapper.findById(id);
  }

  /**
   * 사장 이메일, 전화번호 수정.
   * 
   * @param id 아이디
   * @param mail 변경할 이메일
   * @param tel 변경할 전화번호
   * 
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public OperationResult updateOwnerMailAndTel(String id, String mail, String tel) {
    int result = ownerMapper.updateMailAndTel(id, mail, tel);
    if (result == 1) {
      return OperationResult.SUCCESS; // 정상 수행
    } else if (result == 0) {
      return OperationResult.NONE_CHANGED; // 데이터가 변경되지 않음
    } else {
      throw new RuntimeException("password update error : " + OperationResult.TOO_MANY_CHANGED);
    }
  }

  /**
   * 사장 비밀번호 수정.
   * 
   * @param id 아이디
   * @param password 변경할 비밀번호
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class) // runtimeException이 발생하면 rollback을 수행한다.
  public OperationResult updateOwnerPassword(String id, String password) {
    String cryptoPassword = SHA256Util.encryptSHA256(password);
    int result = ownerMapper.updatePassword(id, cryptoPassword);
    if (result == 1) {
      return OperationResult.SUCCESS;
    } else if (result == 0) {
      return OperationResult.NONE_CHANGED;
    } else {
      throw new RuntimeException("password update error : " + OperationResult.TOO_MANY_CHANGED);
    }

  }
}
