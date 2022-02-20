package com.nenadvasic.orderbook

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    lateinit var database: Database

    fun init(config: ApplicationConfig) {
        val config = HikariConfig().apply {
            jdbcUrl = config.propertyOrNull("db.jdbcUrl")?.getString()
            driverClassName = config.propertyOrNull("db.driver")?.getString()
            username = config.propertyOrNull("db.user")?.getString()
            password = config.propertyOrNull("db.password")?.getString()
            maximumPoolSize = config.propertyOrNull("db.maxPoolSize")?.getString()!!.toInt()
        }

        val dataSource = HikariDataSource(config)
        database = Database.connect(dataSource)
        Flyway.configure().dataSource(dataSource).load().migrate()
    }
}
