package com.delfood.service;

import com.delfood.dto.OwnerDTO;
import com.delfood.error.exception.DuplicateIdException;
import com.delfood.mapper.OwnerMapper;
import com.delfood.utils.SHA256Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;


@Service
@Log4j2
public class OwnerService {
  @Autowired
  OwnerMapper ownerMapper;

  /**
   * 사장님 회원 가입 메서드.
   * 
   * @author jun
   * @param ownerInfo 가입할 사장님의 정보
   */
  public void signUp(OwnerDTO ownerInfo) {
    // id 중복체크
    if (isDuplicatedId(ownerInfo.getId())) {
      throw new DuplicateIdException("아이디가 중복되었습니다");
    }
    String cryptoPassword = SHA256Util.encryptSHA256(ownerInfo.getPassword());
    ownerInfo.setPassword(cryptoPassword);
    int insertOwnerResult = ownerMapper.insertOwner(ownerInfo);
    if (insertOwnerResult != 1) {
      log.error("insert Owner ERROR : {}", ownerInfo);
      throw new RuntimeException("insert Owner ERROR " + ownerInfo);
    }
  }

  /**
   * 사장님 id 중복 체크 메서드.
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
  public void updateOwnerMailAndTel(String id, String password, String mail, String tel) {
    // 정보 변경시 패스워드를 입력받는다. 해당 패스워드가 틀릴 시 정보는 변경되지 않는다.
    if (ownerMapper.findByIdAndPassword(id, password) == null) {
      throw new IllegalArgumentException("패스워드가 일치하지 않습니다");
    }
    
    int result = ownerMapper.updateMailAndTel(id, mail, tel);
    if (result != 1) {
      log.error("updateOwnerMailAndTel ERROR! id : {}, mail : {}, tel : {}", id, mail, tel);
      throw new RuntimeException("password update error");
    }
  }

  /**
   * 사장 비밀번호 수정.
   * 
   * @param id 아이디
   * @param passwordAfterChange 변경할 비밀번호
   * @return
   */
  @Transactional(rollbackFor = RuntimeException.class) // runtimeException이 발생하면 rollback을 수행한다.
  public void updateOwnerPassword(String id, String passwordBeforeChange, String passwordAfterChange) {
    if (ownerMapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(passwordBeforeChange)) == null) { // 아이디와 비밀번호 불일치
      throw new IllegalArgumentException();
    } else if (passwordBeforeChange.equals(SHA256Util.encryptSHA256(passwordAfterChange))) { // 이전 패스워드와 동일한 경우
      throw new HttpStatusCodeException(HttpStatus.CONFLICT, "변경 전 패스워드와 중복됩니다") {};
    }
    String cryptoPassword = SHA256Util.encryptSHA256(passwordAfterChange);
    int result = ownerMapper.updatePassword(id, cryptoPassword);
    if (result != 1) {
      log.error("updateOwnerPassword ERROR! id : {}, password : {}", id, passwordAfterChange);
      throw new RuntimeException("password update error");
    }

  }
}
