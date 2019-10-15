package com.delfood.mapper;

import com.delfood.dto.OwnerDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerMapper {
  public int insertOwner(OwnerDTO ownerInfo);
  
  public int idCheck(String id);
}
