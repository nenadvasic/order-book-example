package com.nenadvasic.orderbook.web

import com.nenadvasic.orderbook.TestContainer
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import io.ktor.config.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test

// TODO Move integration tests to a separate directory
internal class ConfigureSocketsTest {
    companion object {
        val database = TestContainer().initDatabase()
    }

    @Test
    fun `should connect to order book websocket and exchange some messages`() {
        val new = ConfigFactory.defaultApplication()
            .withValue("db.jdbcUrl", ConfigValueFactory.fromAnyRef(TestContainer.container.jdbcUrl))
            .withValue("db.driver", ConfigValueFactory.fromAnyRef(TestContainer.container.driverClassName))
            .withValue("db.user", ConfigValueFactory.fromAnyRef(TestContainer.container.username))
            .withValue("db.password", ConfigValueFactory.fromAnyRef(TestContainer.container.password))
            .withValue("db.maxPoolSize", ConfigValueFactory.fromAnyRef(1))

        val testEnv = createTestEnvironment {
            config = HoconApplicationConfig(new)
        }

        withApplication(testEnv) {
            handleWebSocketConversation("/order-book") { incoming, outgoing ->
                val orderBook = (incoming.receive() as Frame.Text).readText()
                val tradeHistory = (incoming.receive() as Frame.Text).readText()

                // println(orderBook)
                // println(tradeHistory)

                val placeOrderRequest = """
                    {"type":"PlaceOrder","data":"{\"price\":\"6000\",\"side\":\"SELL\",\"amount\":\"9999999\"}"}
                """.trimIndent()

                outgoing.send(Frame.Text(placeOrderRequest))
                outgoing.close()
            }
        }
    }
}
