package com.hashmap27.sample.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@MapperScan(
        basePackages = "com.hashmap27.sample"
        , annotationClass = UseReadWriteDataSource.class
        , sqlSessionFactoryRef = "readWriteSessionFactory"
)
@EnableTransactionManagement
@Configuration
public class MybatisReadWrite {

    @Bean("readWrite")
    @ConfigurationProperties(prefix = "spring.multi-datasource.readwrite")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /** ReadWrite DataSource 획득 */
    @Bean(name = "readWriteDataSource")
    public DataSource getDataSource(@Qualifier("readWrite") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    /** Mybatis SqlSessionFactory */
    @Bean(name = "readWriteSessionFactory")
    public SqlSessionFactoryBean sqlFactory(@Qualifier("readWriteDataSource") final DataSource dataSource, final ApplicationContext applicationContext) throws IOException {
        return setSqlFactory(new SqlSessionFactoryBean(), dataSource, applicationContext);
    }
    protected SqlSessionFactoryBean setSqlFactory(SqlSessionFactoryBean sqlFactory, DataSource dataSource, ApplicationContext context) throws IOException {
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setMapperLocations(context.getResources("classpath:/mapper/readwrite/*.xml"));
        sqlFactory.setTypeAliasesPackage("com.hashmap27.**.domain.**");
        sqlFactory.setTypeHandlersPackage("com.hashmap27.**.domain.**");

        return sqlFactory;
    }

    @Bean(name = "readWirteSessionTemplate")
    public SqlSessionTemplate getSessionTemplate(@Qualifier("readWriteSessionFactory") final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Primary
    public DataSourceTransactionManager getTransactionManager(@Qualifier("readWriteDataSource") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
