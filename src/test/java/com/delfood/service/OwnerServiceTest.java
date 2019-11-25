package com.delfood.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delfood.dto.OwnerDTO;
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
   * 회원가입 성공 테스트.
   */
  @Test
  public void signUp_success() {
    OwnerDTO ownerInfo = OwnerDTO.builder()
                            .id("ljy2134")
                            .password(SHA256Util.encryptSHA256("2134"))
                            .name("이진영")
                            .mail("asdf@naver.com")
                            .tel("010-3333-3333")
                            .build();
    
    given(mapper.insertOwner(ownerInfo)).willReturn(1);
    service.signUp(ownerInfo);
  }
  
  /**
   * 회원가입 실패 테스트.
   */
  @Test(expected = RuntimeException.class)
  public void signUp_fail() {
    OwnerDTO ownerInfo = OwnerDTO.builder()
        .id("ljy2134")
        .password(SHA256Util.encryptSHA256("2134"))
        .name("이진영")
        .mail("asdf@naver.com")
        .tel("010-3333-3333")
        .build();
    
    given(mapper.insertOwner(ownerInfo)).willReturn(0);
    service.signUp(ownerInfo);
  }

}
