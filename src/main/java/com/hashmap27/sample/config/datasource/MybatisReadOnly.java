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

import javax.sql.DataSource;
import java.io.IOException;

@MapperScan(
        basePackages = "com.hashmap27.sample"
        , annotationClass = UseReadOnlyDataSource.class
        , sqlSessionFactoryRef = "readOnlySessionFactory"
)
@Configuration
public class MybatisReadOnly {

    @Bean("readOnly")
    @ConfigurationProperties(prefix = "spring.multi-datasource.readonly")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /** ReadOnly DataSource 획득 */
    @Bean(name = "readOnlyDataSource")
    @Primary
    public DataSource getDataSource(@Qualifier("readOnly") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    /** MyBatis SqlSessionFactory */
    @Bean(name = "readOnlySessionFactory")
    @Primary
    public SqlSessionFactoryBean sqlFactory(@Qualifier("readOnlyDataSource") final DataSource dataSource, final ApplicationContext applicationContext) throws IOException {
        return setSqlFactory(new SqlSessionFactoryBean(), dataSource, applicationContext);
    }
    public SqlSessionFactoryBean setSqlFactory(SqlSessionFactoryBean sqlFactory, DataSource dataSource, ApplicationContext applicationContext) throws IOException {
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setMapperLocations(applicationContext.getResources("classpath:mapper/readonly/*.xml"));
        sqlFactory.setTypeAliasesPackage("com.hashmap27.**.domain.**");
        sqlFactory.setTypeHandlersPackage("com.hashmap27.**.domain.**");

        return sqlFactory;
    }

    @Bean(name = "readOnlySessionTemplate")
    public SqlSessionTemplate getSessionTemplate(@Qualifier("readOnlySessionFactory") final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
