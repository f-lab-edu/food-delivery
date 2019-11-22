package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.delfood.dto.MemberDTO;
import com.delfood.mapper.MemberMapper;
import com.delfood.utils.SHA256Util;
import java.time.LocalDateTime;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


// Mockito에서 사용하는 목 객체를 사용하기 위해 적용한다.
@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {
  /*
   * '@Mock'이 붙은 목 객체를 해당 어노테이션이 선언된 객체에 주입할 수 있다.
   * Dao객체를 주입하기 위해서는 Dao에 '@Mock'을, Service에' @InjectMocks'를 붙여주어야한다. 
   */
  @InjectMocks
  MemberService service;
  
  // Mock객체를 생성한다.
  @Mock
  MemberMapper mapper;
  
  // 새로운 멤버 객체를 생성하여 반환한다.
  public MemberDTO generateMember() {
    MemberDTO member = new MemberDTO();
    member.setId("testMemberId");
    member.setPassword("testMemberPassword");
    member.setName("testUserName");
    member.setTel("010-1111-2222");
    member.setMail("test@mail.com");
    member.setAddressCode("3023010100100090018000001");
    member.setAddressDetail("102호");
    member.setStatus(MemberDTO.Status.DEFAULT);
    member.setCreatedAt(LocalDateTime.now());
    member.setUpdatedAt(LocalDateTime.now());
    
    return member;
  }
  
  @Test
  public void insertMemberTest_고객_회원가입_성공() {
    MemberDTO member = generateMember();
    given(mapper.insertMember(member)).willReturn(1);
    service.insertMember(member);
  }
  
  @Test(expected = RuntimeException.class)
  public void insertMemberTest_고객_회원가입_실패() {
    MemberDTO member = generateMember();
    given(mapper.insertMember(member)).willReturn(0);
    service.insertMember(member);
  }
  
  @Test
  public void loginTest_고객_로그인_성공() {
    MemberDTO member = generateMember();
    given(mapper.findByIdAndPassword("testMemberId", 
          SHA256Util.encryptSHA256("testMemberPassword")))
      .willReturn(member);
    
    assertThat(service.login("testMemberId", "testMemberPassword")).isEqualTo(member);
  }
  
  @Test
  public void loginTest_고객_로그인_실패_테스트_비밀번호_불일치() {
    given(mapper.findByIdAndPassword("testMemberId",
        SHA256Util.encryptSHA256("aaaa")))
      .willReturn(null);
    
    assertThat(service.login("testMemberId", "aaaa")).isNull();
  }
  
  @Test
  public void updateMemberPasswordTest_고객_비밀번호_변경_성공() {
    MemberDTO member = generateMember();
    given(mapper.updateMemberPassword(member.getId(), SHA256Util.encryptSHA256("1234")))
        .willReturn(1);
    
    service.updateMemberPassword(member.getId(), "1234");
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMemberPasswordTest_고객_비밀번호_변경_실패() {
    MemberDTO member = generateMember();
    given(mapper.updateMemberPassword(member.getId(), SHA256Util.encryptSHA256("1234")))
        .willReturn(0);
    
    service.updateMemberPassword(member.getId(), "1234");
  }
  
  @Test
  public void deleteMemberTest_고객_삭제_성공() {
    MemberDTO member = generateMember();
    given(mapper.deleteMember(member.getId()))
      .willReturn(1);
    
    service.deleteMember(member.getId());
  }
  
  @Test(expected = RuntimeException.class)
  public void deleteMember_고객_삭제_실패() {
    MemberDTO member = generateMember();
    given(mapper.deleteMember(member.getId()))
      .willReturn(0);
    
    service.deleteMember(member.getId());
  }
  
  @Test
  public void updateMemberAddress_고객_주소_변경_성공() {
    MemberDTO member = generateMember();
    given(mapper.updateMemberAddress(member.getId(), "3023010100100090028000002", "1023호"))
      .willReturn(1);
    
    service.updateMemberAddress(member.getId(), "3023010100100090028000002", "1023호");
  }
  
  @Test(expected = RuntimeException.class)
  public void updateMemberAddress_고객_주소_변경_실패() {
    MemberDTO member = generateMember();
    given(mapper.updateMemberAddress(member.getId(), "3023010100100090028000002", "1023호"))
      .willReturn(0);
    
    service.updateMemberAddress(member.getId(), "3023010100100090028000002", "1023호");
  }
  
  @Test
  public void getTownCode_고객_읍면동코드_조회() {
    MemberDTO member = generateMember();
    given(mapper.findTownCodeById(member.getId()))
      .willReturn(member.getAddressCode().substring(0, 10));
    
    assertThat(service.getTownCode(member.getId())).isEqualTo("3023010100");
  }
}
