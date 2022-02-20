package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class OrderBookRowDTOTest {
    @Test
    fun `should be creatable from Order model`() {
        // Given
        val order = Order(
            id = 2,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 150.1,
            side = Side.SELL,
            amount = 5.5
        )

        // When
        val orderBookRowDTO = OrderBookRowDTO.makeFromModel(order, 190.5.toBigDecimal())

        // Then
        Assertions.assertThat(orderBookRowDTO.orderId).isEqualTo(2)
        Assertions.assertThat(orderBookRowDTO.price).isEqualTo(150.1.toBigDecimal())
        Assertions.assertThat(orderBookRowDTO.amount).isEqualTo(5.5.toBigDecimal())
        Assertions.assertThat(orderBookRowDTO.total).isEqualTo(190.5.toBigDecimal())
    }

    @Test
    fun `should serialize to json`() {
        // Given
        val order = Order(
            id = 2,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 150.1,
            side = Side.SELL,
            amount = 5.5
        )
        val total = 190.5
        val expectedJson = """
            {
                "orderId": 2,
                "price": "150.1",
                "amount": "5.5",
                "total": "$total"
            }
            """.trimIndent()

        // When
        val actualJson = Json.encodeToString(OrderBookRowDTO.makeFromModel(order, total.toBigDecimal()))

        // Then
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT)
    }

    @Test
    fun `should throw an exception when order id is null`() {
        // Given
        val order = Order(
            id = null,
            time = Instant.parse("2022-02-15T15:46:43.554Z"),
            price = 250.2,
            side = Side.SELL,
            amount = 51.3
        )

        // When/Then
         assertThatThrownBy {OrderBookRowDTO.makeFromModel(order, 200.5.toBigDecimal()) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }
}
