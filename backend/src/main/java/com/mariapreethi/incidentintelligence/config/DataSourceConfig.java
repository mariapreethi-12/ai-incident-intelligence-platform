package com.mariapreethi.incidentintelligence.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    DataSource dataSource() {
        String databaseUrl = System.getenv().getOrDefault(
                "DATABASE_URL",
                "postgres://incident_user:incident_pass@localhost:5432/incident_intelligence"
        );
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");

        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            dataSource.setUrl(databaseUrl);
            dataSource.setUsername(System.getenv().getOrDefault("DATABASE_USERNAME", "incident_user"));
            dataSource.setPassword(System.getenv().getOrDefault("DATABASE_PASSWORD", "incident_pass"));
            return dataSource;
        }

        URI uri = URI.create(databaseUrl);
        String[] credentials = uri.getUserInfo() == null ? new String[]{"", ""} : uri.getUserInfo().split(":", 2);
        String query = uri.getQuery() == null ? "" : "?" + uri.getQuery();
        String port = uri.getPort() == -1 ? "" : ":" + uri.getPort();
        dataSource.setUrl("jdbc:postgresql://" + uri.getHost() + port + uri.getPath() + query);
        dataSource.setUsername(credentials.length > 0 ? decode(credentials[0]) : "");
        dataSource.setPassword(credentials.length > 1 ? decode(credentials[1]) : "");
        return dataSource;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
