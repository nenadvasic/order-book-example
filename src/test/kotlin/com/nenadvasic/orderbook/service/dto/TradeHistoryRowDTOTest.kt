package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.model.Trade
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class TradeHistoryRowDTOTest {
    @Test
    fun `should be creatable from Trade model`() {
        // Given
        val trade = Trade(
            id = 2,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 150.1,
            side = Side.SELL,
            amount = 5.5,
        )

        // When
        val tradeBookRowDTO = TradeHistoryRowDTO.makeFromModel(trade)

        // Then
        Assertions.assertThat(tradeBookRowDTO.tradeId).isEqualTo(2)
        Assertions.assertThat(tradeBookRowDTO.price).isEqualTo(150.1.toBigDecimal())
        Assertions.assertThat(tradeBookRowDTO.side).isEqualTo(Side.SELL.toString())
        Assertions.assertThat(tradeBookRowDTO.amount).isEqualTo(5.5.toBigDecimal())
    }

    @Test
    fun `should serialize to json`() {
        // Given
        val trade = Trade(
            id = 2,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 150.1,
            side = Side.SELL,
            amount = 5.5
        )
        val expectedJson = """
            {
                "tradeId": 2,
                "time": "2022-02-15T15:46:43.554Z",
                "price": "150.1",
                "side": "SELL",
                "amount": "5.5"
            }
            """.trimIndent()

        // When
        val actualJson = Json.encodeToString(TradeHistoryRowDTO.makeFromModel(trade))

        // Then
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT)
    }

    @Test
    fun `should throw an exception when trade id is null`() {
        // Given
        val trade = Trade(
            id = null,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 250.2,
            side = Side.SELL,
            amount = 51.3
        )

        // When/Then
         assertThatThrownBy {TradeHistoryRowDTO.makeFromModel(trade) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }
}
