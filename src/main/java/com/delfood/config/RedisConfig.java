package com.delfood.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.SQLException;
import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class RedisConfig {
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Value("${spring.redis.password}")
  private String redisPwd;

  @Value("${expir.default}")
  private long defaultExpireSecond;
  

  /*
   * Class <=> Json간 변환을 담당한다.
   * 
   * json => object 변환시 readValue(File file, T.class) => json File을 읽어 T 클래스로 변환 readValue(Url url,
   * T.class) => url로 접속하여 데이터를 읽어와 T 클래스로 변환 readValue(String string, T.class) => string형식의
   * json데이터를 T 클래스로 변환
   * 
   * object => json 변환시 writeValue(File file, T object) => object를 json file로 변환하여 저장
   * writeValueAsBytes(T object) => byte[] 형태로 object를 저장 writeValueAsString(T object) => string 형태로
   * object를 json형태로 저장
   * 
   * json을 포매팅(개행 및 정렬) writerWithDefaultPrettyPrint().writeValueAs... 를 사용하면 json파일이 포맷팅하여 저장된다.
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
    return mapper;
  }

  /*
   * Redis Connection Factory library별 특징 
   * 1. Jedis - 멀티쓰레드환경에서 쓰레드 안전을 보장하지 않는다.
   *          - Connection pool을 사용하여 성능, 안정성 개선이 가능하지만 Lettuce보다 상대적으로 하드웨어적인 자원이 많이 필요하다.
   *          - 비동기 기능을 제공하지 않는다.
   * 
   * 2. Lettuce - Netty 기반 redis client library 
   *            - 비동기로 요청하기 때문에 Jedis에 비해 높은 성능을 가지고 있다.
   *            - TPS, 자원사용량 모두 Jedis에 비해 우수한 성능을 보인다는 테스트 사례가 있다.
   * 
   * Jedis와 Lettuce의 성능 비교  https://jojoldu.tistory.com/418
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setPort(redisPort);
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPassword(redisPwd);
    LettuceConnectionFactory lettuceConnectionFactory =
        new LettuceConnectionFactory(redisStandaloneConfiguration);
    return lettuceConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    // json 형식으로 데이터를 받을 때
    // 값이 깨지지 않도록 직렬화한다.
    // 저장할 클래스가 여러개일 경우 범용 JacksonSerializer인 GenericJackson2JsonRedisSerializer를 이용한다
    // 참고 https://somoly.tistory.com/134
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(serializer);
    redisTemplate.setEnableTransactionSupport(true); // transaction 허용
    
    return redisTemplate;
  }

  /**
   * Redis Cache를 사용하기 위한 cache manager 등록.<br>
   * 커스텀 설정을 적용하기 위해 RedisCacheConfiguration을 먼저 생성한다.<br>
   * 이후 RadisCacheManager를 생성할 때 cacheDefaults의 인자로 configuration을 주면 해당 설정이 적용된다.<br>
   * RedisCacheConfiguration 설정<br>
   * disableCachingNullValues - null값이 캐싱될 수 없도록 설정한다. null값 캐싱이 시도될 경우 에러를 발생시킨다.<br>
   * entryTtl - 캐시의 TTL(Time To Live)를 설정한다. Duraction class로 설정할 수 있다.<br>
   * serializeKeysWith - 캐시 Key를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다.<br>
   * serializeValuesWith - 캐시 Value를 직렬화-역직렬화 하는데 사용하는 Pair를 지정한다. 
   * Value는 다양한 자료구조가 올 수 있기 때문에 GenericJackson2JsonRedisSerializer를 사용한다.
   * 
   * @author jun
   * @param redisConnectionFactory Redis와의 연결을 담당한다.
   * @return
   */
  @Bean
  public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
      ObjectMapper objectMapper) {
    RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
        .disableCachingNullValues()
        .entryTtl(Duration.ofSeconds(defaultExpireSecond))
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair
            .fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair
            .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(configuration).build();
  }
  
  
}
