package com.nenadvasic.orderbook.web

import kotlinx.serialization.Serializable

enum class MessageType {
    OrderBook,
    TradeHistory,
    PlaceOrder,
    PlaceOrderResponse,
}

@Serializable
data class ClientMessage(
    val type: MessageType,
    val data: String,
)

@Serializable
data class ServerMessage<T>(
    val type: MessageType,
    val data: T,
)
