package com.nenadvasic.orderbook

import com.nenadvasic.orderbook.web.configureRouting
import com.nenadvasic.orderbook.web.configureSockets
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    // DI
    install(Koin) {
        modules(koinModule)
    }

    DatabaseFactory.init(environment.config)

    configureRouting()
    configureSockets()
}
