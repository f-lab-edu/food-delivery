package com.delfood.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
  
  /**
   * 어노테이선 `@Async`를 사용할 때 설정을 진행한다.
   * 해당 설정으로 '@Async`를 사용하기 위해서는 value값에 해당 빈의 이름을 사용해야한다.
   * @return
   */
  @Bean("asyncTask")
  public Executor threadPoolExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("asyncTask-"); // thread 이름 설정
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(100); // 최대 스레드 개수
    executor.setQueueCapacity(0);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    return executor;
  }
}
