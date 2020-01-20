package com.delfood.service.rider;

import com.delfood.dto.rider.RiderDTO;
import com.delfood.error.exception.DuplicateException;
import com.delfood.error.exception.IdDeletedException;
import com.delfood.mapper.RiderInfoMapper;
import com.delfood.utils.SHA256Util;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class RiderInfoService {
  
  @Autowired
  private RiderInfoMapper riderInfoMapper;
  
  private static final IllegalArgumentException passwordMismatchException =
      new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

  /**
   * 해당 아이디가 중복된 아이디인지 확인한다.
   * 
   * @author jun
   * @param riderId 중복인지 검사할 아이디
   * @return
   */
  public boolean isDuplicatedId(@NonNull String riderId) {
    return riderInfoMapper.isExistById(riderId);
  }
  
  /**
   * 라이더 회원가입을 진행한다.
   * @param riderInfo 회원 가입 정보
   */
  @Transactional
  public void signUp(@NonNull RiderDTO riderInfo) {
    if (isDuplicatedId(riderInfo.getId())) {
      throw new DuplicateException("아이디 \"" + riderInfo.getId() + "\" 는 이미 가입한 아이디입니다.");
    }
    
    riderInfoMapper.insertRider(riderInfo);
  }

  /**
   * 라이더 로그인을 진행한다.
   * @param id 아이디
   * @param password 암호화 전 비밀번호
   * @return
   */
  public RiderDTO signIn(@NonNull String id, @NonNull String password) {
    String encryptedPassword = SHA256Util.encryptSHA256(password);
    RiderDTO riderInfo = getRiderInfo(id, encryptedPassword);
    
    if (RiderDTO.Status.DELETED.equals(riderInfo.getStatus())) {
      log.info("signIn - 삭제 회원 로그인 시도. id : {}, password : {}", id, encryptedPassword);
      throw new IdDeletedException("Rider의 계정이 삭제 상태입니다. 로그인할 수 없습니다.");
    }
    
    
    
    return riderInfo;
  }

  /**
   * 라이더의 비밀번호를 변경한다.
   * @param id 라이더 아이디
   * @param passwordBeforeChange 변경 전 비밀번호
   * @param passwordAfterChange 변경할 비밀번호
   */
  @Transactional
  public void changePassword(@NonNull String id, @NonNull String passwordBeforeChange,
      String passwordAfterChange) {
    if (isEffective(id, passwordBeforeChange) == false) {
      throw passwordMismatchException;
    }
    
    String encryptedPasswordAfter = SHA256Util.encryptSHA256(passwordAfterChange);
    riderInfoMapper.updatePassword(id, encryptedPasswordAfter);
  }
  
  /**
   * 라이더 계정 정보를 조회한다.
   * 일치하는 계정이 없을 시 예외를 발생시킨다.
   * @author jun
   * @param id 조회할 라이더 계정 아이디
   * @param encryptedPassword 암호화를 진행한 비밀번호
   * @return
   */
  public RiderDTO getRiderInfo(@NonNull String id, @NonNull String encryptedPassword) {
    RiderDTO riderInfo = riderInfoMapper.findByIdAndPassword(id, encryptedPassword);
    
    if (Objects.isNull(riderInfo)) {
      log.info("회원 정보 없음. id : {}, password : {}", id, encryptedPassword);
      throw new IllegalArgumentException("id 또는 password가 일치하는 회원 정보가 없습니다.");
    }
    
    return riderInfo;
  }

  /**
   * 라이더 계정을 삭제상태로 만든다.
   * @param id 삭제할 라이더 아이디
   * @param password 삭제하기 전 유효성 검사를 위한 비밀번호
   */
  @Transactional
  public void deleteAccount(@NonNull String id, @NonNull String password) {
    if (isEffective(id, password) == false) {
      log.info("회원 삭제를 시도하였지만 실패하였습니다. 원인 : 비밀번호 불일치. id : {}", id);
      throw passwordMismatchException;
    }
    
    riderInfoMapper.updateStatusAsDeleted(id);
  }
  
  /**
   * 아이디와 비밀번호를 기반으로 유효한 아이디인지, 아이디와 비밀번호가 일치하는지 검사한다.
   * @author jun
   * @param id 검사할 아이디
   * @param password 검사할 비밀번ㄹ호
   * @return
   */
  public boolean isEffective(@NonNull String id, @NonNull String password) {
    String encryptedPassword = SHA256Util.encryptSHA256(password);
    return riderInfoMapper.isExistAndEffectiveByIdAndPassword(id, encryptedPassword);
  }

  /**
   * 라이더의 메일 주소를 변경한다.
   * @author jun
   * @param id 메일을 변경할 아이디
   * @param password 유효성 검사를 위한 비밀번호
   * @param mail 변경할 메일 주소
   */
  @Transactional
  public void changeMail(@NonNull String id, @NonNull String password, @NonNull String mail) {
    if (isEffective(id, password) == false) {
      throw passwordMismatchException;
    }
    
    riderInfoMapper.updateMail(id, mail);
  }

  public boolean hasDelivery(String riderId) {
    return riderInfoMapper.hasDelivery(riderId);
  }
}
