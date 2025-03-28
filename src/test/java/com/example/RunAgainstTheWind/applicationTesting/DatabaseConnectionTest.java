package com.example.RunAgainstTheWind.applicationTesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseConnectionTest {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void testDatabaseConnection() throws Exception {
        assertThat(dataSource).isNotNull();
        try (var connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(1)).isTrue();
        }
    }
    
    @Test
    public void testJdbcTemplateConnection() {
        // Verify that JdbcTemplate can execute a simple query
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }
    
    @Test
    public void testDatabaseMetadata() throws Exception {
        try (var connection = dataSource.getConnection()) {
            var metaData = connection.getMetaData();
            assertThat(metaData.getDatabaseProductName()).contains("PostgreSQL");
        }
    }
}