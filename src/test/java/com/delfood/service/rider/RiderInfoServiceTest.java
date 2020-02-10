package com.delfood.service.rider;

import static org.mockito.Mockito.when;

import com.delfood.dto.rider.RiderDTO;
import com.delfood.dto.rider.RiderDTO.Status;
import com.delfood.error.exception.DuplicateException;
import com.delfood.error.exception.IdDeletedException;
import com.delfood.mapper.RiderInfoMapper;
import com.delfood.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RiderInfoServiceTest {

  @InjectMocks
  public RiderInfoService riderInfoService;

  @Mock
  public RiderInfoMapper riderInfoMapper;

  public RiderDTO generateRider() {
    return RiderDTO.builder().id("testId").password(SHA256Util.encryptSHA256("testPassword"))
        .name("testName").status(Status.DEFAULT).tel("010-1111-2222").build();
  }

  public RiderDTO generateDeletedRider() {
    return RiderDTO.builder().id("testId").password(SHA256Util.encryptSHA256("testPassword"))
        .name("testName").status(Status.DELETED).tel("010-1111-2222").build();
  }

  @Test
  public void signUpTest_라이더_회원가입_성공() {
    RiderDTO riderInfo = generateRider();
    when(riderInfoMapper.isExistById(riderInfo.getId())).thenReturn(false);

    riderInfoService.signUp(riderInfo);
  }

  @Test(expected = DuplicateException.class)
  public void signUpTest_라이더_회원가입_실패_아이디중복() {
    RiderDTO riderInfo = generateRider();
    when(riderInfoMapper.isExistById(riderInfo.getId())).thenReturn(true);

    riderInfoService.signUp(riderInfo);
  }

  @Test
  public void signInTest_라이더_로그인_성공() {
    String id = "testId";
    String password = "testPassword";
    String encryptPassword = SHA256Util.encryptSHA256(password);

    when(riderInfoMapper.findByIdAndPassword(id, encryptPassword)).thenReturn(generateRider());
    riderInfoService.signIn(id, password);
  }

  @Test(expected = IdDeletedException.class)
  public void signInTest_라이더_로그인_실패_삭제된_아이디() {
    String id = "testId";
    String password = "testPassword";
    String encryptPassword = SHA256Util.encryptSHA256(password);

    when(riderInfoMapper.findByIdAndPassword(id, encryptPassword))
        .thenReturn(generateDeletedRider());
    riderInfoService.signIn(id, password);
  }

  @Test(expected = IllegalArgumentException.class)
  public void signInTest_라이더_로그인_실패_비밀번호_불일치() {
    String id = "testId";
    String password = "testPassword";
    String encryptPassword = SHA256Util.encryptSHA256(password);

    when(riderInfoMapper.findByIdAndPassword(id, encryptPassword)).thenReturn(null);
    riderInfoService.signIn(id, password);
  }

  @Test
  public void changePasswordTest_라이더_패스워드_변경_성공() {
    String id = "testId";
    String passwordBeforeChange = "testPassword";
    String passwordAfterChange = "afterPassword";

    when(riderInfoMapper.isExistAndEffectiveByIdAndPassword(id,
        SHA256Util.encryptSHA256(passwordBeforeChange))).thenReturn(true);

    riderInfoService.changePassword(id, passwordBeforeChange, passwordAfterChange);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void changePasswordTest_라이더_패스워드_변경_실패_패스워드_불일치() {
    String id = "testId";
    String passwordBeforeChange = "testPassword";
    String passwordAfterChange = "afterPassword";
    
    when(riderInfoMapper.isExistAndEffectiveByIdAndPassword(id,
        SHA256Util.encryptSHA256(passwordBeforeChange))).thenReturn(false);
    
    riderInfoService.changePassword(id, passwordBeforeChange, passwordAfterChange);
  }
  
  @Test
  public void deleteAccountTest_라이더_계정_삭제() {
    String id = "testId";
    String password = "testPw";
    
    when(riderInfoService.isEffective(id, password)).thenReturn(true);
    riderInfoService.deleteAccount(id, password);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void deleteAccountTest_라이더_계정_삭제_실패_비밀번호_불일치() {
    String id = "testId";
    String password = "testPw";
    
    when(riderInfoService.isEffective(id, password)).thenReturn(false);
    riderInfoService.deleteAccount(id, password);
  }
  
  @Test
  public void changeMailTest_메일주소_변경() {
    String id = "testId";
    String password = "testPw";
    String mail = "changeMail@sss.com";
    
    when(riderInfoService.isEffective(id, password)).thenReturn(true);
    riderInfoService.changeMail(id, password, mail);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void changeMailTest_메일주소_변경_실패_비밀번호_불일치() {
    String id = "testId";
    String password = "testPw";
    String mail = "changeMail@sss.com";
    
    when(riderInfoService.isEffective(id, password)).thenReturn(false);
    riderInfoService.changeMail(id, password, mail);
  }
}
