package com.nenadvasic.orderbook.service

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.enums.Side
import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.TradeRepository
import com.nenadvasic.orderbook.service.dto.PlaceOrderRequestDTO
import com.nenadvasic.orderbook.service.dto.PlaceOrderResponseDTO
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class OrderMatchingServiceTest {
    private val orderRepository = mockk<OrderRepository>()
    private val tradeRepository = mockk<TradeRepository>(relaxed = true)
    private val ordersMatchingService = OrderMatchingService(orderRepository, tradeRepository)

    @Test
    fun `should match a placed order with the existing opposite orders`() {
        // Given
        val placedOrder = Order(id = 5, price = 120.0, side = Side.SELL, amount = 313.0)

        val oppositeOrders = listOf(
            Order(id = 41, price = 115.0, side = Side.BUY, amount = 100.0),
            Order(id = 42, price = 120.0, side = Side.BUY, amount = 450.0),
            Order(id = 43, price = 300.0, side = Side.BUY, amount = 100.0),
        )

        // When
        val matchingResult = ordersMatchingService.matchPlacedOrderWithExistingOrders(placedOrder, oppositeOrders)

        // Then
        assertThat(matchingResult.ordersToDecreaseAmount.size).isEqualTo(3) // 1 placed order + 2 buying orders
        assertThat(matchingResult.filledAmount).isEqualByComparingTo(toBigDecimal(313.0))
    }

    @Test
    fun `should return an empty matching result when there is no opposite orders`() {
        // Given
        val placedOrder = Order(id = 5, price = 120.0, side = Side.SELL, amount = 313.0)

        val oppositeOrders = emptyList<Order>()

        // When
        val matchingResult = ordersMatchingService.matchPlacedOrderWithExistingOrders(placedOrder, oppositeOrders)

        // Then
        assertThat(matchingResult.ordersToDecreaseAmount.size).isEqualTo(0)
        assertThat(matchingResult.filledAmount).isEqualByComparingTo(BigDecimal.ZERO)

    }

    @Test
    fun `should process a selling place order`() {
        // Given
        val placeOrderRequest = PlaceOrderRequestDTO(
            price = toBigDecimal(120.0),
            side = Side.SELL,
            amount = toBigDecimal(313.0),
        )

        val placedOrder = Order(id = 5, price = 120.0, side = Side.SELL, amount = 313.0)

        val buyingOrders = listOf(
            Order(id = 43, price = 300.0, side = Side.BUY, amount = 92.0),
            Order(id = 42, price = 120.0, side = Side.BUY, amount = 51.3),
        )

        val expectedResponse = PlaceOrderResponseDTO(
            orderId = 5,
            placedAmount = toBigDecimal(313.0),
            filledAmount = toBigDecimal(143.3),
        )

        every { orderRepository.saveOrder(any()) } returns placedOrder
        every { orderRepository.getOppositeBuyingOrders(placedOrder.price) } returns buyingOrders
        every { orderRepository.decreaseAmount(any(), any()) } returns Order(price = 0.0, amount = 0.0, side = Side.BUY)

        // When
        val actualResponse = ordersMatchingService.processPlaceOrderRequest(placeOrderRequest)

        // Then
        assertThat(actualResponse.placedAmount).isEqualByComparingTo(expectedResponse.placedAmount)
        assertThat(actualResponse.filledAmount).isEqualByComparingTo(expectedResponse.filledAmount)
    }

    @Test
    fun `should process a buying place order`() {
        // Given
        val placeOrderRequest = PlaceOrderRequestDTO(
            price = toBigDecimal(253.0),
            side = Side.BUY,
            amount = toBigDecimal(183.0),
        )

        val placedOrder = Order(id = 5, price = 253.0, side = Side.BUY, amount = 183.0)

        val sellingOrders = listOf(
            Order(id = 62, price = 132.0, side = Side.SELL, amount = 90.0),
            Order(id = 61, price = 250.0, side = Side.SELL, amount = 18.2),
        )

        val expectedResponse = PlaceOrderResponseDTO(
            orderId = 5,
            placedAmount = toBigDecimal(183.0),
            filledAmount = toBigDecimal(108.2),
        )

        every { orderRepository.saveOrder(any()) } returns placedOrder
        every { orderRepository.getOppositeSellingOrders(placedOrder.price) } returns sellingOrders
        every { orderRepository.decreaseAmount(any(), any()) } returns Order(price = 0.0, amount = 0.0, side = Side.BUY)

        // When
        val actualResponse = ordersMatchingService.processPlaceOrderRequest(placeOrderRequest)

        // Then
        assertThat(actualResponse.placedAmount).isEqualByComparingTo(expectedResponse.placedAmount)
        assertThat(actualResponse.filledAmount).isEqualByComparingTo(expectedResponse.filledAmount)
    }
}
