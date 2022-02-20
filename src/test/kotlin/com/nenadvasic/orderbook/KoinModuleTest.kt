package com.nenadvasic.orderbook

import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.TradeRepository
import com.nenadvasic.orderbook.service.OrderBookService
import com.nenadvasic.orderbook.service.OrderMatchingService
import com.nenadvasic.orderbook.web.SocketServer
import io.mockk.mockk
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.get

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KoinModuleTest : KoinTest {
    @Test
    fun `should verify Koin modules`() {
        koinApplication {
            modules(koinModule)
        }
    }

    @Test
    fun `should inject all components`() {
        startKoin {
            modules(koinModule)
        }

        DatabaseFactory.database = mockk()

        val orderRepository = get<OrderRepository>()
        val tradeRepository = get<TradeRepository>()
        val orderBookService = get<OrderBookService>()
        val orderMatchingService = get<OrderMatchingService>()
        val socketServer = get<SocketServer>()

        assertThat(orderRepository).isNotNull
        assertThat(tradeRepository).isNotNull
        assertThat(orderBookService).isNotNull
        assertThat(orderMatchingService).isNotNull
        assertThat(socketServer).isNotNull
    }

    @AfterAll
    fun tearDown() {
        stopKoin()
    }
}
