package com.delfood.service;

import com.delfood.dao.FcmDao;
import com.delfood.dto.push.PushMessage;
import com.delfood.dto.push.PushMessageForOne;
import com.delfood.dto.push.PushMessageForTopic;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PushService {
  /*
   * 하나의 아이디로 여러 기기에서 접속할 수 있기때문에 아이디와 토큰은 1 : N의 관계를 가진다.
   * 그렇기 때문에 토큰을 List형태로 저장하였다.
   * 토큰은 사용하지 않으면 한달 후에 삭제된다.
   */
  
  @Autowired
  private FcmDao fcmDao;
  
  private static final String FCM_PRIVATE_KEY_PATH =
      "delfood-8385c-firebase-adminsdk-p6vk2-9d667ced9e.json";

  /**
   * FCM 기본 설정을 진행한다.
   * @author jun
   */
  @PostConstruct
  public void init() {
    try {
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(
              GoogleCredentials
                  .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream()))
          .build();
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
        log.info("Firebase application has been initialized");
      }
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }


  
  /**
   * 1명의 사용자에게 푸시 메세지를 전송한다.
   * @author jun
   * @param messageInfo 전송할 푸시 정보
   */
  public void send(PushMessageForOne messageInfo) {
    Message message = Message.builder()
        .setToken(messageInfo.getToken())
        .putData("title", messageInfo.getTitle())
        .putData("message", messageInfo.getMessage())
        .putData("time", LocalDateTime.now().toString())
        .build();

    String response;
    try {
      response = FirebaseMessaging.getInstance().send(message);
      log.info("Sent message: " + response);
    } catch (FirebaseMessagingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  /**
   * 해당 토픽을 가진 사용자들에게 메세지를 전송한다.
   * @author jun
   * @param messageInfo 전송할 푸시 정보
   */
  public void send(PushMessageForTopic messageInfo) {
    Message message = Message.builder()
        .setTopic(messageInfo.getTopic())
        .putData("title", messageInfo.getTitle())
        .putData("message", messageInfo.getMessage())
        .putData("time", LocalDateTime.now().toString())
        .build();
    String response;
    
    try {
      response = FirebaseMessaging.getInstance().send(message);
      log.info("Sent message: " + response);
    } catch (FirebaseMessagingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  /**
   * 고객 아이디로 로그인한 기기에 메세지를 보낸다.
   * @param messageInfo 푸시 정보
   * @param memberId 고객 아이디
   */
  public void sendMessageToMember(PushMessage messageInfo, String memberId) {
    List<String> tokens = fcmDao.getMemberTokens(memberId);
    List<Message> messages = tokens.stream().map(token -> Message.builder()
        .putData("title", messageInfo.getTitle())
        .putData("message", messageInfo.getMessage())
        .putData("time", LocalDateTime.now().toString())
        .setToken(token)
        .build()).collect(Collectors.toList());
    
    BatchResponse response;
    try {
      response = FirebaseMessaging.getInstance().sendAll(messages);
      log.info("Sent message: " + response);
    } catch (FirebaseMessagingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  /**
   * 사장님에게 푸시 메세지를 전송한다.
   * @param messageInfo 전송할 푸시 정보
   * @param ownerId 사장님 아이디
   */
  public void sendMessageToOwner(PushMessage messageInfo, String ownerId) {
    List<String> tokens = fcmDao.getOwnerTokens(ownerId);
    List<Message> messages = tokens.stream().map(token -> Message.builder()
        .putData("title", messageInfo.getTitle())
        .putData("message", messageInfo.getMessage())
        .putData("time", LocalDateTime.now().toString())
        .setToken(token)
        .build()).collect(Collectors.toList());
    
    BatchResponse response;
    try {
      response = FirebaseMessaging.getInstance().sendAll(messages);
      log.info("Sent message: " + response);
    } catch (FirebaseMessagingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  
  /**
   * 회원 토큰정보를 저장한다.
   * @author jun
   * @param memberId 고객 아이디
   * @param token 토큰 정보
   */
  public void setMemberToken(String memberId, String token) {
    fcmDao.addMemberToken(memberId, token);
  }
  
  /**
   * 사장님 토큰정보를 저장한다.
   * @author jun
   * @param ownerId 사장님 아이디
   * @param token 토큰 정보
   */
  public void setOwnerToken(String ownerId, String token) {
    fcmDao.addOwnerToken(ownerId, token);
  }
  
  /**
   * 고객 토큰 정보를 조회한다.
   * @author jun
   * @param memberId 고객 아이디
   * @return
   */
  public List<String> getMemberTokens(String memberId) {
    return fcmDao.getMemberTokens(memberId);
  }
  
  /**
   * 사장님 토큰 정보를 조회한다.
   * @param ownerId 사장님 아이디
   * @return
   */
  public List<String> getOwnerTokens(String ownerId) {
    return fcmDao.getOwnerTokens(ownerId);
  }
}
