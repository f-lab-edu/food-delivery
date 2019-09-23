package com.delfood.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 고객 
@Getter @Setter @ToString
public class MemberDTO {
	    // 아이디 
	    private String id;
	    // 패스워드 
	    private String password;
	    // 이름 
	    private String name;
	    // 핸드폰번호 
	    private String tel;
	    // 이메일 
	    private String mail;
	    // 상태 
	    private String status;
	    // 회원가입일 
	    private LocalDateTime createdAt;
	    // 최종 수정일 
	    private LocalDateTime updatedAt;
	    // 주소
	    private String address;
	    // 상세 주소
	    private String addressDetail;
	    
	    // Member 모델 복사
	    public void copyData(MemberDTO param)
	    {
	        this.id = param.getId();
	        this.password = param.getPassword();
	        this.name = param.getName();
	        this.tel = param.getTel();
	        this.mail = param.getMail();
	        this.status = param.getStatus();
	        this.createdAt = param.getCreatedAt();
	        this.updatedAt = param.getUpdatedAt();
	    }
}