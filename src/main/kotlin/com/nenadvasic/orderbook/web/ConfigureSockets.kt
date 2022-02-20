package com.nenadvasic.orderbook.web

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.time.*
import io.ktor.application.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val socketServer by inject<SocketServer>()

    routing {
        webSocket("/order-book") {
            val connection = socketServer.addClientConnection(this)
            try {
                socketServer.sendInitialContentToClient(connection)
                socketServer.readIncomingMessages(connection, incoming)
            } catch (e: Exception) {
                println(e.toString())
            } finally {
                socketServer.removeClientConnection(connection)
            }
        }
    }
}
