package com.nenadvasic.orderbook.repository

import com.nenadvasic.orderbook.TestContainer
import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.*
import java.math.BigDecimal

// TODO Move integration tests to a separate directory
@TestInstance(Lifecycle.PER_CLASS)
internal class OrderRepositoryMysqlTest {
    companion object {
        val database = TestContainer().initDatabase()
        val orderRepository: OrderRepository = OrderRepositoryMysql(database)
    }

    @BeforeEach
    fun beforeEach() {
        transaction(database) {
            object : Table("orders") {}.deleteAll()
        }
    }

    @DisplayName("Create an order")
    @Nested
    inner class CreateOrder {
        @Test
        fun `should create a new buying order`() {
            // Given
            val newOrder = Order(
                id = 19132,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = 200.0,
                side = Side.BUY,
                amount = 50.0,
                filledAmount = 30.0,
                active = true
            )

            // When
            val createdOrder = orderRepository.saveOrder(newOrder)

            // Then
            assertThat(newOrder).isEqualTo(createdOrder)
        }

        @Test
        fun `should create a new selling order`() {
            // Given
            val newOrder = Order(
                id = 21133,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = 123.0,
                side = Side.SELL,
                amount = 13.0,
                filledAmount = 3.0,
                active = true
            )

            // When
            val createdOrder = orderRepository.saveOrder(newOrder)

            // Then
            assertThat(newOrder).isEqualTo(createdOrder)
        }
    }

    @DisplayName("Get orders")
    @Nested
    inner class GetOrders {
        @Test
        fun `should return a list of all active orders`() {
            // Given
            val listOfOrders = listOf(
                Order(price = 200.0, side = Side.BUY, amount = 50.0, active = true),
                Order(price = 100.0, side = Side.SELL, amount = 30.0, active = true),
                Order(price = 80.0, side = Side.BUY, amount = 1.0, active = false),
                Order(price = 30.0, side = Side.SELL, amount = 5.0, active = true),
            )

            // When
            listOfOrders.forEach {
                orderRepository.saveOrder(it)
            }

            val orders = orderRepository.getAllActiveOrders()

            // Then
            assertThat(orders.size).isEqualTo(3)
        }

        @Test
        fun `should return a list of opposite buying orders for given selling order price`() {
            // Given
            val listOfOrders = listOf(
                Order(price = 200.0, side = Side.BUY, amount = 50.0),
                Order(price = 100.0, side = Side.SELL, amount = 30.0),
                Order(price = 80.0, side = Side.BUY, amount = 1.0),
                Order(price = 40.0, side = Side.BUY, amount = 1.0),
                Order(price = 30.0, side = Side.SELL, amount = 5.0),
            )
            val sellingOrderPrice = 70.0

            // When
            listOfOrders.forEach {
                orderRepository.saveOrder(it)
            }

            val orders = orderRepository.getOppositeBuyingOrders(sellingOrderPrice.toBigDecimal())

            // Then
            assertThat(orders.size).isEqualTo(2)
        }

        @Test
        fun `should return a list of opposite selling orders for given buying order price`() {
            // Given
            val listOfOrders = listOf(
                Order(price = 200.0, side = Side.BUY, amount = 50.0),
                Order(price = 100.0, side = Side.SELL, amount = 30.0),
                Order(price = 80.0, side = Side.BUY, amount = 1.0),
                Order(price = 30.0, side = Side.SELL, amount = 5.0),
            )
            val buyingOrderPrice = 70.0

            // When
            listOfOrders.forEach {
                orderRepository.saveOrder(it)
            }
            val orders = orderRepository.getOppositeSellingOrders(buyingOrderPrice.toBigDecimal())

            // Then
            assertThat(orders.size).isEqualTo(1)
        }
    }

    @DisplayName("Update an order")
    @Nested
    inner class UpdateOrder {
        @Test
        fun `should set the active field to false`() {
            // Given
            val order = Order(id = 391, price = 123.0, side = Side.SELL, amount = 13.0, active = true)

            // When
            orderRepository.saveOrder(order)
            val updatedOrder = orderRepository.setActiveToFalse(order.id!!)

            // Then
            assertThat(updatedOrder.active).isEqualTo(false)
        }

        @Test
        fun `should throw an exception if the order doesn't exist when trying to update the active field`() {
            assertThatThrownBy { orderRepository.setActiveToFalse(1292) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should decrease the order amount`() {
            // Given
            val order = Order(id = 892, price = 123.0, side = Side.SELL, amount = 1239.0, active = true)

            // When
            orderRepository.saveOrder(order)
            val updatedOrder = orderRepository.decreaseAmount(order.id!!, 543.0.toBigDecimal())

            // Then
            assertThat(updatedOrder.amount).isEqualByComparingTo(BigDecimal.valueOf(1239.0 - 543.0))
            assertThat(updatedOrder.filledAmount).isEqualByComparingTo(BigDecimal.valueOf(543.0))
            assertThat(updatedOrder.active).isEqualTo(true)
        }

        @Test
        fun `should decrease the order amount and set active to false if the remaining amount is zero`() {
            // Given
            val order = Order(id = 892, price = 123.0, side = Side.SELL, amount = 1239.0, active = true)

            // When
            orderRepository.saveOrder(order)
            val updatedOrder = orderRepository.decreaseAmount(order.id!!, 1239.0.toBigDecimal())

            // Then
            assertThat(updatedOrder.amount).isEqualByComparingTo(BigDecimal.valueOf(0))
            assertThat(updatedOrder.filledAmount).isEqualByComparingTo(BigDecimal.valueOf(1239.0))
            assertThat(updatedOrder.active).isEqualTo(false)
        }

        @Test
        fun `should throw an exception when the decrease amount is greater than the existing amount`() {
            // Given
            val order = Order(id = 723, price = 123.0, side = Side.SELL, amount = 1239.0, active = true)

            // When/Then
            orderRepository.saveOrder(order)

            assertThatThrownBy { orderRepository.decreaseAmount(order.id!!, 2934.0.toBigDecimal()) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should throw an exception when we try to decrease the order amount and the order doesn't exist`() {
            assertThatThrownBy { orderRepository.decreaseAmount(1992, 2.0.toBigDecimal()) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
