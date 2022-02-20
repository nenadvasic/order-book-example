package com.nenadvasic.orderbook

import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.OrderRepositoryMysql
import com.nenadvasic.orderbook.repository.TradeRepository
import com.nenadvasic.orderbook.repository.TradeRepositoryMysql
import com.nenadvasic.orderbook.service.OrderBookService
import com.nenadvasic.orderbook.service.OrderMatchingService
import com.nenadvasic.orderbook.web.SocketServer
import org.koin.dsl.module

val koinModule = module {
    single { DatabaseFactory.database }
    factory<OrderRepository> { OrderRepositoryMysql(get()) }
    factory<TradeRepository> { TradeRepositoryMysql(get()) }
    factory { OrderBookService(get(), get()) }
    factory { OrderMatchingService(get(), get()) }
    single { SocketServer(get(), get()) }
}
