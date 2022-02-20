package com.nenadvasic.orderbook

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.MySQLContainer

class TestContainer {
    companion object {
        val container: MySQLContainer<Nothing> = MySQLContainer("mysql")
    }

    fun initDatabase(): Database {
        container.start()

        val database = Database.connect(
            url = container.jdbcUrl,
            user = container.username,
            password = container.password,
            driver = container.driverClassName,
        )

        Flyway.configure().dataSource(container.jdbcUrl, container.username, container.password).load().migrate()

        return database
    }
}
