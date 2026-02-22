package com.calendar.support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfig {
    companion object {
        private val mysql = MySQLContainer("mysql:8.0")
            .withDatabaseName("calendar_test")
            .withUsername("test")
            .withPassword("test")
            .apply { start() }
    }

    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> = mysql
}
