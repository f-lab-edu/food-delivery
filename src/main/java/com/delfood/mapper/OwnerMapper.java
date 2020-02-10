package com.delfood.mapper;

import org.springframework.stereotype.Repository;
import com.delfood.dto.owner.OwnerDTO;

@Repository
public interface OwnerMapper {
  public int insertOwner(OwnerDTO ownerInfo);
  
  public int idCheck(String id);
  
  public OwnerDTO findByIdAndPassword(String id, String password);

  public OwnerDTO findById(String id);

  public int updatePassword(String id, String password);

  public int updateMailAndTel(String id, String mail, String tel);


}
