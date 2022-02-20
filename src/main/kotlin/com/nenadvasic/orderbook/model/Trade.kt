package com.nenadvasic.orderbook.model

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigDecimal

data class Trade(
    val id: Int? = null,
    val time: Instant = Clock.System.now(),
    val price: BigDecimal,
    val side: Side,
    val amount: BigDecimal,
) {
    constructor(
        id: Int? = null,
        time: Instant = Clock.System.now(),
        price: Double,
        side: Side,
        amount: Double,
    ) : this(
        id,
        time,
        toBigDecimal(price),
        side,
        toBigDecimal(amount)
    )
}
