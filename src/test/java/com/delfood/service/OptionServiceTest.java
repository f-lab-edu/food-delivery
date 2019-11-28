package com.delfood.service;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import com.delfood.dto.OptionDTO;
import com.delfood.dto.OptionDTO.Status;
import com.delfood.mapper.OptionMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Log4j2
public class OptionServiceTest {

  @InjectMocks
  OptionService service;
  
  @Mock
  OptionMapper mapper;

  public OptionDTO generateOption() {
    OptionDTO option = new OptionDTO();
    option.setId(1L);
    option.setMenuId(1L);
    option.setName("Test Option Name");
    option.setPrice(10000L);
    option.setStatus(Status.DEFAULT);
    return option;
  }
  
  @Test
  public void addOptionTest_옵션_추가_성공() {
    OptionDTO option = generateOption();
    given(mapper.insertOption(option)).willReturn(1);
    service.addOption(option);
  }
  
  @Test
  public void addOptionTest_옵션_추가_실패_null() {
    try {
      OptionDTO option = generateOption();
      option.setName(null);
      service.addOption(option);
      log.info("name null check 실패");
      fail();
    } catch (RuntimeException e) {
      log.info("name null check 성공");
    }
    
    try {
      OptionDTO option = generateOption();
      option.setPrice(null);
      service.addOption(option);
      log.info("price null check 실패");
      fail();
    } catch (RuntimeException e) {
      log.info("price null check 성공");
    }
    
    try {
      OptionDTO option = generateOption();
      option.setMenuId(null);
      service.addOption(option);
      log.info("menuId null check 실패");
      fail();
    } catch (RuntimeException e) {
      log.info("menuId null check 성공");
    }
  }
  
  @Test(expected = RuntimeException.class)
  public void addOptionTest_옵션_추가_실패_DB에러() {
    OptionDTO option = generateOption();
    given(mapper.insertOption(option)).willReturn(0);
    service.addOption(option);
  }
  
  @Test
  public void addOptionList_옵션_리스트_추가_성공() {
    List<OptionDTO> options = new ArrayList<OptionDTO>();
    for (long l = 1; l <= 3; l++) {
      OptionDTO option = generateOption();
      option.setId(l);
      options.add(option);
    }
    
    given(mapper.insertOptionList(options, 1L)).willReturn(1);
    service.addOptionList(options, 1L);
  }
  
}
