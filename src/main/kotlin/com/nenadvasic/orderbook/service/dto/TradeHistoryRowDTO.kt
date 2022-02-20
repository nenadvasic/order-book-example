package com.nenadvasic.orderbook.service.dto

import com.nenadvasic.orderbook.helpers.BigDecimalSerializer
import com.nenadvasic.orderbook.model.Trade
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class TradeHistoryRowDTO(
    val tradeId: Int,
    val time: Instant,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val side: String,
    @Serializable(with = BigDecimalSerializer::class) val amount: BigDecimal,
) {
    companion object {
        fun makeFromModel(trade: Trade): TradeHistoryRowDTO {
            if (trade.id == null) {
                throw IllegalArgumentException("Trade id can not be null when creating TradeHistoryRowDTO")
            }
            return TradeHistoryRowDTO(
                tradeId = trade.id,
                time = trade.time,
                price = trade.price.stripTrailingZeros(),
                side = trade.side.toString(),
                amount = trade.amount.stripTrailingZeros(),
            )
        }
    }
}
