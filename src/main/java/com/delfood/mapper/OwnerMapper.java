package com.delfood.mapper;

import com.delfood.dto.OwnerDTO;

public interface OwnerMapper {
	
    public OwnerDTO findByIdAndPassword(String id, String password);
	
}
