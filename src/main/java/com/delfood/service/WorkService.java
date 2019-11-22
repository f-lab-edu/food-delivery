package com.delfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.delfood.mapper.WorkMapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class WorkService {
  @Autowired
  private WorkMapper workMapper;

  /**
   * 영업 시작을 기록한다.
   * @author jun
   * @param shopId 영업을 시작할 매장의 id
   */
  @Transactional(rollbackFor = RuntimeException.class)
  public void addWork(Long shopId) {
    int result = workMapper.insertWorkToOpen(shopId);
    if (result != 1) {
      log.error("add work ERROR! shopId : {}, result : {}", shopId, result);
      throw new RuntimeException("add work ERROR!");
    }
  }
  
  /**
   * 가장 최근에 오픈한 영업에 영업 종료를 기록한다.
   * @author jun
   * @param shopId 영업 종료를 기록할 매장의 id
   */
  @Transactional
  public void closeWork(Long shopId) {
    int result = workMapper.updateWorkToClose(shopId);
    if (result != 1) {
      log.error("close work ERROR! shopId : {}, result : {}", shopId, result);
      throw new RuntimeException("close Work ERROR!");
    }
  }
}
