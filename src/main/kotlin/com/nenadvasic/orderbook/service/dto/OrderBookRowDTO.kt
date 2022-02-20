package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.helpers.BigDecimalSerializer
import com.nenadvasic.orderbook.model.Order
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class OrderBookRowDTO(
    val orderId: Int,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val amount: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val total: BigDecimal,
) {
    companion object {
        fun makeFromModel(order: Order, total: BigDecimal): OrderBookRowDTO {
            if (order.id == null) {
                throw IllegalArgumentException("Order id can not be null when creating OrderBookRowDTO")
            }
            return OrderBookRowDTO(
                orderId = order.id,
                price = order.price.stripTrailingZeros(),
                amount = order.amount.stripTrailingZeros(),
                total = total.stripTrailingZeros(),
            )
        }
    }
}
