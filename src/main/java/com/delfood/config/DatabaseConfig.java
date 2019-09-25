package com.delfood.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@Configuration
@MapperScan(basePackages="com.delfood.mapper")
@EnableTransactionManagement	// DataSourceTransactionManager Bean을 Transaction Manager로 사용
public class DatabaseConfig {
 
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // VFS(Virtual File System)은 파일이나 시스템 리소스 등에 접근 할 때 이용되는 클래스로 
        // boot project를 배포한 jar 파일에선 class에 접근 할 수 없어서 스프링부트 전용 VFS를 등록
        sessionFactory.setVfs(SpringBootVFS.class);  
        // TypeAlias로 설정할 클래스들이 있는 패키지를 설정하면 DTO에 @Alias("aliasName")으로 typeAlias를 설정 가능
        sessionFactory.setTypeAliasesPackage("com.delfood.dto.");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/*.xml"));
        return sessionFactory.getObject();
        
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
      final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
      return sqlSessionTemplate;
    }
 
 
}


