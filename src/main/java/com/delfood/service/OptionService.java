package com.delfood.service;

import com.delfood.dto.menu.OptionDTO;
import com.delfood.dto.order.item.OrderItemDTO;
import com.delfood.dto.order.item.OrderItemOptionDTO;
import com.delfood.mapper.OptionMapper;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class OptionService {

  @Autowired
  OptionMapper optionMapper;


  /**
   * 옵션 추가.
   * @param optionInfo 옵션정보
   */
  public void addOption(OptionDTO optionInfo) {
    if (optionInfo.hasNullDataBeforeCreate(optionInfo)) {
      log.error("insert option has null data {}", optionInfo);
      throw new RuntimeException("insert option has null data!");
    }
    
    if (optionMapper.insertOption(optionInfo) != 1) {
      log.error("insert Option error! {}", optionInfo);
      throw new RuntimeException("insert Option error!");
    }
  }
  
  /**
   * 옵션리스트 추가.
   * 
   * @author jinyoung
   * 
   * @param optionList 추가할 옵션 리스트
   * @param menuId 메뉴 아이디
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addOptionList(List<OptionDTO> optionList, Long menuId) {
    optionMapper.insertOptionList(optionList, menuId);
  }
  
  /**
   * 옵션 삭제.
   * @param id 옵션 아이디
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void deleteOption(Long id) {
    int result = optionMapper.deleteOption(id);
    if (result != 1) {
      log.error("delete option error! id : {}",id);
      throw new RuntimeException("delete option error!");
    }
  }
  
  public OptionDTO getOption(Long id) {
    return optionMapper.findById(id);
  }

  public long totalPrice(List<OrderItemOptionDTO> options) {
    return optionMapper.totalPrice(options);
  }
  
  public OptionDTO getOptionInfo(Long id) {
    return optionMapper.findById(id);
  }

  
  
}
