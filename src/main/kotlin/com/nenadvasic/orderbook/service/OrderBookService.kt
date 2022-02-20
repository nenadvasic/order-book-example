package com.nenadvasic.orderbook.service

import com.nenadvasic.orderbook.service.dto.OrderBookDTO
import com.nenadvasic.orderbook.service.dto.OrderBookRowDTO
import com.nenadvasic.orderbook.service.dto.TradeHistoryRowDTO
import com.nenadvasic.orderbook.model.enums.Side
import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.TradeRepository
import java.math.BigDecimal

class OrderBookService(
    private val orderRepository: OrderRepository,
    private val tradeRepository: TradeRepository,
) {
    fun getOrderBook(): OrderBookDTO {
        val orders = orderRepository.getAllActiveOrders()
        val sellOrders = mutableListOf<OrderBookRowDTO>()
        val buyOrders = mutableListOf<OrderBookRowDTO>()

        // Total amount is sum of all order amounts at the given price level
        var totalAmount = BigDecimal.ZERO

        orders
            .filter { it.side == Side.SELL }
            .sortedBy { it.price }
            .forEach {
                totalAmount += it.amount
                sellOrders.add(OrderBookRowDTO.makeFromModel(it, totalAmount))
            }

        totalAmount = BigDecimal.ZERO

        orders
            .filter { it.side == Side.BUY }
            .sortedByDescending { it.price }
            .forEach {
                totalAmount += it.amount
                buyOrders.add(OrderBookRowDTO.makeFromModel(it, totalAmount))
            }

        val lastTrade = tradeRepository.getLastTrade()

        return OrderBookDTO(
            sellOrders = sellOrders.sortedByDescending { it.price },
            buyOrders = buyOrders,
            lastPrice = lastTrade?.price?.stripTrailingZeros(),
            lastSide = lastTrade?.side?.toString(),
        )
    }

    fun getTradeHistory(): List<TradeHistoryRowDTO> {
        val trades = tradeRepository.getAllTrades()
        return trades.map { TradeHistoryRowDTO.makeFromModel(it) }
    }
}
