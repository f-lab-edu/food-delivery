package com.delfood.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface WorkMapper {
  public int insertWorkToOpen(Long shopId);
  
  public int updateWorkToClose(Long shopId);
}
