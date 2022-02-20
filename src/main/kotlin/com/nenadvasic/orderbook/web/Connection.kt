package com.nenadvasic.orderbook.web

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(private val session: DefaultWebSocketServerSession) {
    companion object {
        var lastId = AtomicInteger(1)
    }

    val name = "client${lastId.getAndIncrement()}"

    suspend fun send(msg: String) {
        session.send(Frame.Text(msg))
    }
}
