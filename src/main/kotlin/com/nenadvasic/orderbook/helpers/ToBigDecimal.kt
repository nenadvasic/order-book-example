package com.nenadvasic.orderbook.helpers

import java.math.BigDecimal

fun toBigDecimal(value: Double): BigDecimal {
    return value.toBigDecimal().stripTrailingZeros()
}
