package com.nenadvasic.orderbook

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    lateinit var database: Database

    fun init(appConfig: ApplicationConfig) {
        val config = HikariConfig().apply {
            jdbcUrl = appConfig.propertyOrNull("db.jdbcUrl")?.getString()
            driverClassName = appConfig.propertyOrNull("db.driver")?.getString()
            username = appConfig.propertyOrNull("db.user")?.getString()
            password = appConfig.propertyOrNull("db.password")?.getString()
            maximumPoolSize = appConfig.propertyOrNull("db.maxPoolSize")?.getString()!!.toInt()
        }

        val dataSource = HikariDataSource(config)
        database = Database.connect(dataSource)
        Flyway.configure().dataSource(dataSource).load().migrate()
    }
}
