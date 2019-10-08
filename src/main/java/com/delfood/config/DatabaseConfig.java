package com.delfood.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(basePackages = "com.delfood.mapper")
@EnableTransactionManagement // DataSourceTransactionManager Bean을 Transaction Manager로 사용
public class DatabaseConfig {

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    sessionFactory.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/*.xml"));
    return sessionFactory.getObject();
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
      throws Exception {
    final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
    return sqlSessionTemplate;
  }


}


