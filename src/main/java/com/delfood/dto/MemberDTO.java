package com.delfood.dto;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

// 고객 
@Data
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
	    // 로그인타입 (Kakao, Facebook...)
	    private String loginType;
	    // 상태 
	    private String status;
	    // 회원가입일 
	    private LocalDateTime regDate;
	    // 최종 수정일 
	    private LocalDateTime lastUpdateDate;
	    
	    // Member 모델 복사
	    public void CopyData(MemberDTO param)
	    {
	        this.id = param.getId();
	        this.password = param.getPassword();
	        this.name = param.getName();
	        this.tel = param.getTel();
	        this.mail = param.getMail();
	        this.loginType = param.getLoginType();
	        this.status = param.getStatus();
	        this.regDate = param.getRegDate();
	        this.lastUpdateDate = param.getLastUpdateDate();
	    }
}