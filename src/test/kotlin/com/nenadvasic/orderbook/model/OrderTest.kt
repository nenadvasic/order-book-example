package com.nenadvasic.orderbook.model

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OrderTest {
    @Test
    fun `should create an Order object with primary constructor`() {
        val order = Order(
            id = 19132,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 200.0.toBigDecimal(),
            side = Side.BUY,
            amount = 50.0.toBigDecimal(),
            filledAmount = 30.0.toBigDecimal(),
            active = true
        )

        assertThat(order.id).isEqualTo(19132)
        assertThat(order.time).isEqualTo(Instant.parse("2022-02-15T15:46:43.554Z"))
        assertThat(order.price).isEqualTo(200.0.toBigDecimal())
        assertThat(order.side).isEqualTo(Side.BUY)
        assertThat(order.amount).isEqualTo(50.0.toBigDecimal())
        assertThat(order.filledAmount).isEqualTo(30.0.toBigDecimal())
        assertThat(order.active).isEqualTo(true)
    }

    @Test
    fun `should create an Order object with secondary constructor`() {
        val order = Order(
            id = 19132,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 200.0,
            side = Side.BUY,
            amount = 50.0,
            filledAmount = 30.0,
            active = true
        )

        assertThat(order.id).isEqualTo(19132)
        assertThat(order.time).isEqualTo(Instant.parse("2022-02-15T15:46:43.554Z"))
        assertThat(order.price).isEqualTo(toBigDecimal(200.0))
        assertThat(order.side).isEqualTo(Side.BUY)
        assertThat(order.amount).isEqualTo(toBigDecimal(50.0))
        assertThat(order.filledAmount).isEqualTo(toBigDecimal(30.0))
        assertThat(order.active).isEqualTo(true)
    }
}
