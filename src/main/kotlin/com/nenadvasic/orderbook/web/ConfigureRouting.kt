package com.nenadvasic.orderbook.web

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import java.io.File

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondFile(File("./frontend/index.html"))
        }
    }
}
