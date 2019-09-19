package com.delfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession // session을 redis에 저장
/*
 * @EnableRedisHttpSession
 * springSessionRepositoryFilter Bean을 생성한다. 이 필터를 통해 스프링 세션 <=> 레디스간 연결을 지원해줄 수 있다.
 * 이를 통해 세션에 저장하는 사용자의 로그인 정보가 redis에 저장되고, 여러 WAS를 사용하더라도 세션 정보를 하나의 redis에서 관리할 수 있다.
 */
public class FoodDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryApplication.class, args);
	}

}
