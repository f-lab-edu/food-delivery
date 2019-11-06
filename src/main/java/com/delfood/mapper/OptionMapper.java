package com.delfood.mapper;

import com.delfood.dto.OptionDTO;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionMapper {

  /**
   * 옵션 추가.
   * 
   * @author jinyoung
   * 
   * @param optionInfo 옵션 정보
   * @return
   */
  public int insertOption(OptionDTO optionInfo);
  
  /**
   * 옵션 삭제.
   * 
   * @author jinyoung
   * 
   * @param id 옵션 아이디
   * @return
   */
  public int deleteOption(Long id);
}
