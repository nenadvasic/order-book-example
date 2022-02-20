package com.nenadvasic.orderbook.model

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigDecimal

data class Order(
    val id: Int? = null,
    val time: Instant = Clock.System.now(),
    val price: BigDecimal,
    val side: Side,
    val amount: BigDecimal,
    val filledAmount: BigDecimal = BigDecimal.ZERO,
    val active: Boolean = true,
) {
    // Secondary constructor for initializing with double values instead of big decimals
    constructor(
        id: Int? = null,
        time: Instant = Clock.System.now(),
        price: Double,
        side: Side,
        amount: Double,
        filledAmount: Double = 0.0,
        active: Boolean = true,
    ) : this(
        id,
        time,
        toBigDecimal(price),
        side,
        toBigDecimal(amount),
        toBigDecimal(filledAmount),
        active,
    )
}
