package com.calendar.support

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class DatabaseCleanup(
    @PersistenceContext private val entityManager: EntityManager,
) {
    private val tableNames: List<String> by lazy {
        entityManager.createNativeQuery(
            "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
        ).resultList
            .filterIsInstance<String>()
            .filter { it != "flyway_schema_history" }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun execute() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate()
        tableNames.forEach {
            entityManager.createNativeQuery("TRUNCATE TABLE `$it`").executeUpdate()
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate()
    }
}
