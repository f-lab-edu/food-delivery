package com.delfood.mapper;

import com.delfood.dto.OwnerDTO;

import org.springframework.stereotype.Repository;

@Repository
public interface OwnerMapper {
  public int insertOwner(OwnerDTO ownerInfo);
  
  public int idCheck(String id);
  
  OwnerDTO findByIdAndPassword(String id, String password);

  OwnerDTO findById(String id);

  int updatePassword(String id, String password);

  int updateMailAndTel(String id, String mail, String tel);


}
