package com.nenadvasic.orderbook.repository

import com.nenadvasic.orderbook.model.Order
import java.math.BigDecimal

interface OrderRepository {
    fun saveOrder(order: Order): Order
    fun getAllActiveOrders(): List<Order>
    fun getOppositeBuyingOrders(sellingOrderPrice: BigDecimal): List<Order>
    fun getOppositeSellingOrders(buyingOrderPrice: BigDecimal): List<Order>
    fun decreaseAmount(orderId: Int, amountToDecrease: BigDecimal): Order
    fun setActiveToFalse(orderId: Int): Order
}
