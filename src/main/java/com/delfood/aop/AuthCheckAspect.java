package com.delfood.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.codehaus.commons.compiler.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.delfood.service.ShopService;
import com.delfood.utils.SessionUtil;
import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
@SuppressWarnings("unchecked")
public class AuthCheckAspect {
  @Autowired
  private ShopService shopService;
  
  /**
   * session에서 owner 로그인을 체크한다.
   * 로그인되어있지 않을 시 해당 메서드 로직을 중지시킨 후 리턴한다.
   * @OwnerLoginCheck 해당 어노테이션이 적용된 메서드를 검사한다.
   * @author jun
   * @param jp 조인포인트
   * @throws Throwable 발생 가능한 예외
   */
  @Before("@annotation(com.delfood.aop.OwnerLoginCheck)")
  public void ownerLoginCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Owner Login Check Started");
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if(ownerId == null) {
      log.debug("AOP - Owner Login Check Result - NO_LOGIN");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
  }
  
  
  /**
   * 세션에서 사장님 로그인을 체크 한다.
   * 그 후 입력받은 파라미터 값 중 매장 id를 검색하여 해당 매장이 접속한 사장님의 것인지 검사한다.
   * @author jun
   * @param jp 조인포인트
   * @throws Throwable 발새 가능한 예외
   */
  @Before("@annotation(com.delfood.aop.OwnerShopCheck) && @annotation(ownerShopCheck)")
  public void ownerShopCheck(JoinPoint jp, OwnerShopCheck ownerShopCheck) throws Throwable {
    log.debug("AOP - Owner Shop Check Started");
    
    
    HttpSession session =
        ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest()
            .getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);

    if (ownerId == null) {
      log.debug("AOP - Owner Shop Check Result - NO_LOGIN");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
    
    Object[] args = jp.getArgs();
    
    // 메소드 파라미터 추출
    MethodSignature signature = (MethodSignature) jp.getSignature();
    Method method = signature.getMethod();
    Parameter[] parameters = method.getParameters();
    
    Long shopId = null;
    
    // 파라미터의 이름과 어노테이션의 value를 비교하여 검사
    for (int i = 0; i < parameters.length; i++) {
      String parameterName = parameters[i].getName();
      if (StringUtils.equals(ownerShopCheck.value(), parameterName)) {
        shopId = (Long) args[i];
      }
    }

    // 어노테이션 value로 설정된 값과 같은 변수 이름이 없을 경우 예외처리
    if (Objects.isNull(shopId)) {
      throw new IllegalArgumentException("OwnerShopCheck 어노테이션 설정이 잘못되었습니다. value와 변수 명을 일치시켜주세요.");
    }
    
    
    if (!shopService.isShopOwner(shopId, ownerId)) {
      log.debug("AOP - Owner Shop Check Result - UNAUTHORIZED");
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED") {};
    }
  }
  
  /**
   * 고객의 로그인을 체크한다.
   * @author jun
   * @param jp 조인포인튼
   * @throws Throwable 발생 가능한 예외
   */
  @Before("@annotation(com.delfood.aop.MemberLoginCheck)")
  public void memberLoginCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Member Login Check Started");
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String memberId = SessionUtil.getLoginMemberId(session);
    
    if (memberId == null) {
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
    }
  }
  
  /**
   * 라이더 로그인을 체크한다.
   * @author jun
   * @param jp 조인포인트
   * @throws Throwable 발생 가능한 예외 설정
   */
  @Before("@annotation(com.delfood.aop.RiderLoginCheck)")
  public void riderLoginCheck(JoinPoint jp) throws Throwable {
    log.debug("AOP - Rider Login Check Started");

    HttpSession session =
        ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest()
            .getSession();
    String riderId = SessionUtil.getLoginRiderId(session);
    
    if (Objects.isNull(riderId)) {
      throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "RIDER_NO_LOGIN") {};
    }
  }
  
  /**
   * 공통 로그인 체크 AOP.
   * 고객, 사장님, 라이더의 로그인 체크 기능을 하나로 모아두었다.
   * @param jp 조인포인트
   * @throws Throwable 발생 가능한 예외
   */
  @Before("@annotation(com.delfood.aop.LoginCheck) && @ annotation(loginCheck)")
  public void loginCheck(JoinPoint jp, LoginCheck loginCheck) throws Throwable {
    log.debug("AOP - Login Check Started");

    HttpSession session =
        ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest()
            .getSession();
    
    if (LoginCheck.UserType.MEMBER.equals(loginCheck.type())) {
      memberLoginCheck(jp);
    }
    
    if (LoginCheck.UserType.OWNER.equals(loginCheck.type())) {
      ownerLoginCheck(jp);
    }
    
    if (LoginCheck.UserType.RIDER.equals(loginCheck.type())) {
      riderLoginCheck(jp);
    }
    
   
  }
  
}
