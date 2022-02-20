package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.helpers.BigDecimalSerializer
import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Clock
import kotlinx.serialization.*
import java.math.BigDecimal

@Serializable
data class PlaceOrderRequestDTO(
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val side: Side,
    @Serializable(with = BigDecimalSerializer::class) val amount: BigDecimal,
) {
    fun toModel(): Order {
        return Order(
            time = Clock.System.now(),
            price = price,
            side = side,
            amount = amount,
        )
    }
}

