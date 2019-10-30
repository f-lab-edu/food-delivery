package com.delfood.aop;

import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.delfood.controller.CommonResponse;
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
  
  @Around("@annotation(com.delfood.aop.OwnerLoginCheck)")
  public ResponseEntity<CommonResponse> ownerLoginCheck(ProceedingJoinPoint pjp) throws Throwable {
    log.debug("AOP - Owner Login Check Started");
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if(ownerId == null) {
      log.debug("AOP - Owner Login Check Result - NO_LOGIN");
      return CommonResponse.NO_LOGIN_RESPONSE;
    }
    
    Object proceed = pjp.proceed();
    log.debug("AOP - Owner Login Check Result - SUCCESS");
    
    return (ResponseEntity<CommonResponse>) proceed;
  }
  
  
  @Around("@annotation(com.delfood.aop.OwnerShopCheck)")
  public ResponseEntity<CommonResponse> ownerShopCheck(ProceedingJoinPoint pjp) throws Throwable {
    log.debug("AOP - Owner Shop Check Started");
    
    
    HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
    String ownerId = SessionUtil.getLoginOwnerId(session);
    
    if(ownerId == null) {
      log.debug("AOP - Owner Shop Check Result - NO_LOGIN");
      return CommonResponse.NO_LOGIN_RESPONSE;
    }
    
    Object[] args = pjp.getArgs();
    Long shopId = (Long) args[0];
    
    if (!shopService.isShopOwner(shopId, ownerId)) {
      log.debug("AOP - Owner Shop Check Result - UNAUTHORIZED");
      return CommonResponse.UNAUTHORIZED_RESPONSE;
    }
    
    Object proceed = pjp.proceed();
    
    
    log.debug("AOP - Owner Shop Check Result - SUCCESS");
    return (ResponseEntity<CommonResponse>) proceed;
  }
}
