/**
 * 
 */
package com.spazomatic.jobyjob.profile.config;


import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:application.properties" })
@ComponentScan({ "com.spazomatic.jobyjob.profile" })
public class PersistenceConfig {

  @Autowired
  private Environment env;

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
     LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
     sessionFactory.setDataSource(restDataSource());
     sessionFactory.setPackagesToScan(new String[] { "com.spazomatic.jobyjob.profile" });
     sessionFactory.setHibernateProperties(hibernateProperties());

     return sessionFactory;
  }

  @Bean
  public DataSource restDataSource() {
     BasicDataSource dataSource = new BasicDataSource();
     dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
     dataSource.setUrl(env.getProperty("jdbc.url"));
     dataSource.setUsername(env.getProperty("jdbc.username"));
     dataSource.setPassword(env.getProperty("jdbc.password"));

     return dataSource;
  }

  @Bean
  @Autowired
  public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
     HibernateTransactionManager txManager = new HibernateTransactionManager();
     txManager.setSessionFactory(sessionFactory);

     return txManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
     return new PersistenceExceptionTranslationPostProcessor();
  }

  Properties hibernateProperties() {
     return new Properties() {
        {
           setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
           setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
           setProperty("hibernate.globally_quoted_identifiers", "true");
        }
     };
  }
}
