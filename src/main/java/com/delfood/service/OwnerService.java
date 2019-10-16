package com.delfood.service;

import com.delfood.dto.OwnerDTO;
import com.delfood.mapper.DMLOperationError;
import com.delfood.mapper.OwnerMapper;
import com.delfood.utils.SHA256Util;
import javax.management.RuntimeErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;


@Service
public class OwnerService {

  @Autowired
  private OwnerMapper ownerMapper;

  /**
   * 사장님 로그인.
   * 
   * @param id 아이디
   * @param password 패스워드
   * @return
   */
  public OwnerDTO login(String id, String password) {
    String cryptoPassword = SHA256Util.encryptSHA256(password);
    OwnerDTO ownerInfo = ownerMapper.findByIdAndPassword(id, cryptoPassword);
    return ownerInfo;
  }

  /**
   * 사장 정보 조회.
   * 
   * @return id, name, mail, tel, createAt, updatedAt, status
   */
  public OwnerDTO ownerInfo(String id) {
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
  public DMLOperationError updateOwnerMailAndTel(String id, String mail, String tel) {
    int result = ownerMapper.updateMailAndTel(id, mail, tel);
    if (result == 1) {
      return DMLOperationError.SUCCESS; // 정상 수행
    } else if (result == 0) {
      return DMLOperationError.NONE_CHANGED; // 데이터가 변경되지 않음
    } else {
      throw new RuntimeException("password update error : " + DMLOperationError.TOO_MANY_CHANGED);
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
  public DMLOperationError updateOwnerPassword(String id, String password) {
    String cryptoPassword = SHA256Util.encryptSHA256(password);
    int result = ownerMapper.updatePassword(id, cryptoPassword);
    if (result == 1) {
      return DMLOperationError.SUCCESS;
    } else if (result == 0) {
      return DMLOperationError.NONE_CHANGED;
    } else {
      throw new RuntimeException("password update error : " + DMLOperationError.TOO_MANY_CHANGED);
    }

  }


}
