package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.helpers.BigDecimalSerializer
import kotlinx.serialization.*
import java.math.BigDecimal

@Serializable
data class PlaceOrderResponseDTO(
    val orderId: Int,
    @Serializable(with = BigDecimalSerializer::class) val placedAmount: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class) val filledAmount: BigDecimal,
)
