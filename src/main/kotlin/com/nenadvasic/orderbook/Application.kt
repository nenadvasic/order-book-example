package com.nenadvasic.orderbook

import com.nenadvasic.orderbook.plugins.configureRouting
import com.nenadvasic.orderbook.plugins.configureSockets
import io.ktor.application.*
import com.nenadvasic.orderbook.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureRouting()
    configureRouting()
    configureSockets()
}
