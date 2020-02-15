package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import com.delfood.dto.owner.OwnerDTO;
import com.delfood.error.exception.DuplicateException;
import com.delfood.mapper.OwnerMapper;
import com.delfood.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class OwnerServiceTest {

  @InjectMocks // 의존성 주입이 필요한 mock에 설정, @Mock으로 등록된 객체를 주입시켜준다.
  OwnerService service;
  
  @Mock // mock 생성
  OwnerMapper mapper;
  
  /**
   * owner 정보 생성.
   * @return
   */
  public OwnerDTO generateOwner() {
    return OwnerDTO.builder()
    .id("ljy2134")
    .password(SHA256Util.encryptSHA256("2134"))
    .name("이진영")
    .mail("asdf@naver.com")
    .tel("010-3333-3333")
    .build();
  }
  
  /**
   * 회원가입 성공 테스트.
   */
  @Test
  public void signUp_success() {
    OwnerDTO ownerInfo = generateOwner();
    
    given(mapper.insertOwner(ownerInfo)).willReturn(1);
    given(mapper.idCheck(ownerInfo.getId())).willReturn(0);
    
    service.signUp(ownerInfo);
  }
  
  /**
   * 회원가입 실패 테스트. (DB insert 실패)
   */
  @Test(expected = RuntimeException.class)
  public void signUp_fail() {
    OwnerDTO ownerInfo = generateOwner();
    
    given(mapper.idCheck(ownerInfo.getId())).willReturn(0);
    given(mapper.insertOwner(ownerInfo)).willReturn(0);
    
    service.signUp(ownerInfo);
  }

  /**
   * 회원가입 실패 테스트. (아이디 중복 발생)
   */
  @Test(expected = RuntimeException.class)
  public void signUp_fail2() {
    OwnerDTO ownerInfo = generateOwner();
    
    given(mapper.idCheck(ownerInfo.getId())).willReturn(1);
    
    service.signUp(ownerInfo);
  }
  
  /**
   * 아이디 중복 체크 성공 테스트.
   */
  @Test
  public void isDuplicatedId_success() {
    String duplicatedId = "duplicatedId";
    String noDuplicatedId = "noDuplicatedId";
    given(mapper.idCheck(duplicatedId)).willReturn(1);
    given(mapper.idCheck(noDuplicatedId)).willReturn(0);
    
    assertThat(service.isDuplicatedId(duplicatedId)).isTrue();
    assertThat(service.isDuplicatedId(noDuplicatedId)).isFalse();
  }
  
  /**
   * 사장 정보 조회 성공 테스트.
   */
  @Test
  public void getOwner_success() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String password = ownerInfo.getPassword();
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(password)))
      .willReturn(ownerInfo);
    given(mapper.findById(id)).willReturn(ownerInfo);
    
    assertThat(service.getOwner(id, password)).isEqualTo(ownerInfo);
    assertThat(service.getOwner(id)).isEqualTo(ownerInfo);
  }
  
  /**
   * 사장 이메일, 전화번호 수정 성공 테스트.
   */
  @Test
  public void updateOwnerMailAndTel_success() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String password = ownerInfo.getPassword();
    String mail = ownerInfo.getMail();
    String tel = ownerInfo.getTel();
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(password))).willReturn(ownerInfo);
    given(mapper.updateMailAndTel(id, mail, tel)).willReturn(1);

    service.updateOwnerMailAndTel(id, password, mail, tel);
  }
  
  /**
   * 사장 이메일, 전화번호 수정 실패 테스트. (아이디, 패스워드 검증 실패)
   */
  @Test(expected = IllegalArgumentException.class)
  public void updateOwnerMailAndTel_fail() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String password = ownerInfo.getPassword();
    String mail = ownerInfo.getMail();
    String tel = ownerInfo.getTel();
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(password))).willReturn(null);
    
    service.updateOwnerMailAndTel(id, password, mail, tel);
  }
  
  /**
   * 사장 이메일, 전화번호 수정 실패 테스트. (DB update 실패)
   */
  @Test(expected = RuntimeException.class)
  public void updateOwnerMailAndTel_fail2() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String password = ownerInfo.getPassword();
    String mail = ownerInfo.getMail();
    String tel = ownerInfo.getTel();
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(password))).willReturn(ownerInfo);
    given(mapper.updateMailAndTel(id, mail, tel)).willReturn(0);
    
    service.updateOwnerMailAndTel(id, password, mail, tel);
  }
  
  
  /**
   * 사장 비밀번호 수정 성공 테스트.
   */
  @Test
  public void updateOwnerPassword_success() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String beforePassword = ownerInfo.getPassword();
    String afterPassword = "asdfasdf";
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(beforePassword)))
      .willReturn(ownerInfo);
    given(mapper.updatePassword(id, SHA256Util.encryptSHA256(afterPassword))).willReturn(1);
    
    service.updateOwnerPassword(id, beforePassword, afterPassword);
  }
  
  /**
   * 사장 비밀번호 수정 실패 테스트. (아이디와 기존 패스워드 매칭 실패)
   */
  @Test(expected = IllegalArgumentException.class)
  public void updateOwnerPassword_fail() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String beforePassword = "strangePassword";
    String afterPassword = "asdfasdf";
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(beforePassword)))
      .willReturn(null);
    
    service.updateOwnerPassword(id, beforePassword, afterPassword);
  }
  
  /**
   * 사장 비밀번호 수정 실패 테스트. (변경 전 패스워드와 변경 후 패스워드가 일치)
   */
  @Test(expected = DuplicateException.class)
  public void updateOwnerPassword_fail2() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String beforePassword = ownerInfo.getPassword();
    String afterPassword = ownerInfo.getPassword();
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(beforePassword)))
      .willReturn(ownerInfo);
    
    service.updateOwnerPassword(id, beforePassword, afterPassword);
  }
  
  /**
   * 사장 비밀번호 수정 실패 테스트. (변경 전 패스워드와 변경 후 패스워드가 일치)
   */
  @Test(expected = RuntimeException.class)
  public void updateOwnerPassword_fail3() {
    OwnerDTO ownerInfo = generateOwner();
    String id = ownerInfo.getId();
    String beforePassword = ownerInfo.getPassword();
    String afterPassword = "afterPassword";
    
    given(mapper.findByIdAndPassword(id, SHA256Util.encryptSHA256(beforePassword)))
      .willReturn(ownerInfo);
    given(mapper.updatePassword(id, SHA256Util.encryptSHA256(afterPassword))).willReturn(0);
    
    service.updateOwnerPassword(id, beforePassword, afterPassword);
  }
}
