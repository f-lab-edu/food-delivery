package com.delfood.mapper;

import com.delfood.dto.OptionDTO;

import java.util.List;
import org.apache.ibatis.annotations.Param;
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
   * 다수의 옵션을 추가.
   * 
   * @param optionList 옵션 정보들을 담은 리스트
   * @return
   */
  public int insertOptionList(@Param("optionList") List<OptionDTO> optionList,
      @Param("menuId") Long menuId);
  
  
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
