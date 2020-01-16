package com.delfood.mapper;

import com.delfood.dto.OptionDTO;
import com.delfood.dto.OrderItemDTO;
import com.delfood.dto.OrderItemOptionDTO;
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
  public int insertOptionList(List<OptionDTO> optionList, Long menuId);
  
  /**
   * 옵션 삭제.
   * 
   * @author jinyoung
   * 
   * @param id 옵션 아이디
   * @return
   */
  public int deleteOption(Long id);

  /**
   * 옵션 가격을 계산한다.
   * @param options 계산할 옵아이템들
   * @return
   */
  public long totalPrice(List<OrderItemOptionDTO> options);

  /**
   * 옵션 조회.
   * 
   * @author jun
   * @param id 옵션 아이디
   * @return
   */
  public OptionDTO findById(Long id);
}
