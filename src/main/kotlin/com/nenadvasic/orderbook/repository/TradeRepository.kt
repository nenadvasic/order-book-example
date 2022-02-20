package com.nenadvasic.orderbook.repository

import com.nenadvasic.orderbook.model.Trade

interface TradeRepository {
    fun saveTrade(trade: Trade): Trade
    fun getAllTrades(): List<Trade>
    fun getLastTrade(): Trade?
}
