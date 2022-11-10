package com.danamon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        basePackages = { "com.danamon.persistence.repository" })
public class RekeningKoranConfig {
    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(System.getenv("TRANSACTION_URL") !=null ? System.getenv("TRANSACTION_URL") : env.getProperty("spring.datasource.url"));
        dataSource.setUsername(System.getenv("TRANSACTION_USERNAME") != null ? System.getenv("TRANSACTION_USERNAME") : env.getProperty("spring.datasource.username"));
        dataSource.setPassword(System.getenv("TRANSACTION_PASSWORD") != null ? System.getenv("TRANSACTION_PASSWORD") : env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.danamon.persistence.domain");
        em.setPersistenceUnitName("entityManagerFactory");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.datasource.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.datasource.jpa.hibernate.dialect"));
        properties.put("hibernate.show-sql", env.getProperty("spring.datasource.jpa.show-sql"));
        properties.put("hikari.connectionTimeout", env.getProperty("transaction.datasource.jpa.hikari.connectionTimeout"));
        properties.put("hikari.maximumPoolSize", env.getProperty("transaction.datasource.jpa.hikari.maximumPoolSize"));
        properties.put("hikari.idleTimeout", env.getProperty("transaction.datasource.jpa.hikari.idleTimeout"));
        properties.put("hikari.maxLifetime", env.getProperty("transaction.datasource.jpa.hikari.maxLifetime"));
        properties.put("hikari.minimumIdle", env.getProperty("transaction.datasource.jpa.hikari.minimumIdle"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
