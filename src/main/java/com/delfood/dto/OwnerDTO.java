package com.delfood.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("owner")
@Getter @Setter @ToString
public class OwnerDTO {
	
	private String id;
	private String password;
	private String name;
	private String mail;
	private String tel;
	private Date createdAt;
	private Date updatedAt;
	private String status;
}
