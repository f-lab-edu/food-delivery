package com.delfood.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString 
@Alias("owner") // 지정한 이름으로 typeAlias를 등록
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
