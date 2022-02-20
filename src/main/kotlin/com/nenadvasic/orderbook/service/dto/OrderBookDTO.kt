package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.helpers.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class OrderBookDTO(
    val sellOrders:  List<OrderBookRowDTO>,
    val buyOrders:  List<OrderBookRowDTO>,
    @Serializable(with = BigDecimalSerializer::class) val lastPrice: BigDecimal?,
    val lastSide: String?,
)
