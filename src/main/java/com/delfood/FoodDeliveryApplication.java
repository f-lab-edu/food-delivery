package com.delfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession // session을 redis에 저장
/*
 * @EnableRedisHttpSession springSessionRepositoryFilter Bean을 생성한다. 이 필터를 통해 스프링 세션 <=> 레디스간 연결을
 * 지원해줄 수 있다. 이를 통해 세션에 저장하는 사용자의 로그인 정보가 redis에 저장되고, 여러 WAS를 사용하더라도 세션 정보를 하나의 redis에서 관리할 수 있다.
 * 
 * 사용자가 로그인 하면 서버에서 세션이 발급되고 쿠키 데이터가 redis에 저장된다.
 * 세션에 로그인한 사용자의 id를 저장한다.
 * 
 * 이후 사용자가 로그인 한 자신의 정보가 필요할 경우 서버로 세션 쿠키데이터를 보내고
 * 서버에서는 그 쿠키 데이터를 이용하여 redis에 id값이 존재하는지 조회한다.
 * 
 * id값이 존재한다면 서버에서는 이 세션의 주인이 이 id를 가진 사용자라고 확인하게 되고 관련된 정보를 사용할 수 있도록 조회한다.
 */
@EnableAspectJAutoProxy // 최상위 클래스에 적용해야 AOP를 찾을 수 있도록 만들어준다.
@EnableCaching // Spring에서 Caching을 사용하겠다고 선언한다.
public class FoodDeliveryApplication {

  public static void main(String[] args) {
    SpringApplication.run(FoodDeliveryApplication.class, args);
  }

}
