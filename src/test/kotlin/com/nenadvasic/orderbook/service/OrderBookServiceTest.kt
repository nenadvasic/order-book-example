package com.nenadvasic.orderbook.service

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.Trade
import com.nenadvasic.orderbook.model.enums.Side
import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.TradeRepository
import com.nenadvasic.orderbook.service.dto.OrderBookDTO
import com.nenadvasic.orderbook.service.dto.OrderBookRowDTO
import com.nenadvasic.orderbook.service.dto.TradeHistoryRowDTO
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class OrderBookServiceTest {
    private val orderRepository = mockk<OrderRepository>()
    private val tradeRepository = mockk<TradeRepository>()
    private val orderBookService = OrderBookService(orderRepository, tradeRepository)

    @Test
    fun `should return the order book`() {
        // Given
        val orders = listOf(
            Order(id = 25, price = 1050.0, side = Side.BUY, amount = 20.0),
            Order(id = 21, price = 5000.0, side = Side.SELL, amount = 5.0),
            Order(id = 27, price = 900.0, side = Side.BUY, amount = 45.0),
            Order(id = 24, price = 2000.0, side = Side.SELL, amount = 105.0),
            Order(id = 28, price = 800.0, side = Side.BUY, amount = 100.0),
            Order(id = 22, price = 3040.0, side = Side.SELL, amount = 25.0),
            Order(id = 26, price = 950.0, side = Side.BUY, amount = 30.0),
            Order(id = 23, price = 2550.0, side = Side.SELL, amount = 35.0),
            Order(id = 29, price = 100.0, side = Side.BUY, amount = 1.0),
        )

        every { orderRepository.getAllActiveOrders() } returns orders
        every { tradeRepository.getLastTrade() } returns Trade(
            id = 15,
            price = toBigDecimal(1455.5),
            side = Side.SELL,
            amount = toBigDecimal(50.0)
        )

        val expectedOrderBookDTO = OrderBookDTO(
            sellOrders = listOf(
                OrderBookRowDTO(
                    orderId = 21,
                    price = toBigDecimal(5000.0),
                    amount = toBigDecimal(5.0),
                    total = toBigDecimal(170.0)
                ),
                OrderBookRowDTO(
                    orderId = 22,
                    price = toBigDecimal(3040.0),
                    amount = toBigDecimal(25.0),
                    total = toBigDecimal(165.0)
                ),
                OrderBookRowDTO(
                    orderId = 23,
                    price = toBigDecimal(2550.0),
                    amount = toBigDecimal(35.0),
                    total = toBigDecimal(140.0)
                ),
                OrderBookRowDTO(
                    orderId = 24,
                    price = toBigDecimal(2000.0),
                    amount = toBigDecimal(105.0),
                    total = toBigDecimal(105.0)
                ),
            ),
            buyOrders = listOf(
                OrderBookRowDTO(
                    orderId = 25,
                    price = toBigDecimal(1050.0),
                    amount = toBigDecimal(20.0),
                    total = toBigDecimal(20.0)
                ),
                OrderBookRowDTO(
                    orderId = 26,
                    price = toBigDecimal(950.0),
                    amount = toBigDecimal(30.0),
                    total = toBigDecimal(50.0)
                ),
                OrderBookRowDTO(
                    orderId = 27,
                    price = toBigDecimal(900.0),
                    amount = toBigDecimal(45.0),
                    total = toBigDecimal(95.0)
                ),
                OrderBookRowDTO(
                    orderId = 28,
                    price = toBigDecimal(800.0),
                    amount = toBigDecimal(100.0),
                    total = toBigDecimal(195.0)
                ),
                OrderBookRowDTO(
                    orderId = 29,
                    price = toBigDecimal(100.0),
                    amount = toBigDecimal(1.0),
                    total = toBigDecimal(196.0)
                ),
            ),
            lastPrice = toBigDecimal(1455.5),
            lastSide = Side.SELL.toString(),
        )

        // When
        val actualOrderBookDTO = orderBookService.getOrderBook()

        // Then
        assertThat(actualOrderBookDTO).isEqualTo(expectedOrderBookDTO)
    }

    @Test
    fun `should return trade history`() {
        // Given
        val trades = listOf(
            Trade(
                id = 32,
                time = Instant.parse("2022-02-15T15:55:43.554Z"),
                price = 5000.0,
                side = Side.SELL,
                amount = 5.0
            ),
            Trade(
                id = 31,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = 1050.0,
                side = Side.BUY,
                amount = 20.0
            ),
        )

        every { tradeRepository.getAllTrades() } returns trades

        val expectedTradeHistory = listOf(
            TradeHistoryRowDTO(
                tradeId = 32,
                time = Instant.parse("2022-02-15T15:55:43.554Z"),
                price = toBigDecimal(5000.0),
                side = Side.SELL.toString(),
                amount = toBigDecimal(5.0)
            ),
            TradeHistoryRowDTO(
                tradeId = 31,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = toBigDecimal(1050.0),
                side = Side.BUY.toString(),
                amount = toBigDecimal(20.0)
            ),
        )

        // When
        val actualTradeHistory = orderBookService.getTradeHistory()

        // Then
        assertThat(actualTradeHistory).isEqualTo(expectedTradeHistory)
    }
}
