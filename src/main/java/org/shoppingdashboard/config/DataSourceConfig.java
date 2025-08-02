package org.shoppingdashboard.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class DataSourceConfig {

    private HikariDataSource hikariDataSource;

    @Bean
    public HikariDataSource dataSource() {
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/shopping-dashboard");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("072204@Akash");
        hikariDataSource.setMaximumPoolSize(10);
        return hikariDataSource;
    }

    @PreDestroy
    public void closeDataSource() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }
}