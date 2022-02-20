package com.nenadvasic.orderbook.model

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TradeTest {
    @Test
    fun `should create an Trade object with primary constructor`() {
        val trade = Trade(
            id = 19132,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 200.0.toBigDecimal(),
            side = Side.BUY,
            amount = 50.0.toBigDecimal(),
        )

        assertThat(trade.id).isEqualTo(19132)
        assertThat(trade.time).isEqualTo(Instant.parse("2022-02-15T15:46:43.554Z"))
        assertThat(trade.price).isEqualTo(200.0.toBigDecimal())
        assertThat(trade.side).isEqualTo(Side.BUY)
        assertThat(trade.amount).isEqualTo(50.0.toBigDecimal())
    }

    @Test
    fun `should create an Trade object with secondary constructor`() {
        val trade = Trade(
            id = 19132,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 200.0,
            side = Side.BUY,
            amount = 50.0,
        )

        assertThat(trade.id).isEqualTo(19132)
        assertThat(trade.time).isEqualTo(Instant.parse("2022-02-15T15:46:43.554Z"))
        assertThat(trade.price).isEqualTo(toBigDecimal(200.0))
        assertThat(trade.side).isEqualTo(Side.BUY)
        assertThat(trade.amount).isEqualTo(toBigDecimal(50.0))
    }
}
