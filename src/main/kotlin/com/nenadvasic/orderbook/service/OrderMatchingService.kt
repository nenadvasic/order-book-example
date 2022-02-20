package com.nenadvasic.orderbook.service

import com.nenadvasic.orderbook.model.Order
import com.nenadvasic.orderbook.model.Trade
import com.nenadvasic.orderbook.model.enums.Side
import com.nenadvasic.orderbook.repository.OrderRepository
import com.nenadvasic.orderbook.repository.TradeRepository
import com.nenadvasic.orderbook.service.dto.PlaceOrderRequestDTO
import com.nenadvasic.orderbook.service.dto.PlaceOrderResponseDTO
import java.math.BigDecimal

internal data class MatchingResult(
    val ordersToDecreaseAmount: MutableMap<Int, BigDecimal>, // <order id, order amount>
    var filledAmount: BigDecimal,
)

class OrderMatchingService(
    private val orderRepository: OrderRepository,
    private val tradeRepository: TradeRepository,
) {
    fun processPlaceOrderRequest(placeOrderRequest: PlaceOrderRequestDTO): PlaceOrderResponseDTO {
        val placedOrder = orderRepository.saveOrder(placeOrderRequest.toModel())

        val oppositeOrders = if (placedOrder.side == Side.BUY) {
            orderRepository.getOppositeSellingOrders(placedOrder.price)
        } else {
            orderRepository.getOppositeBuyingOrders(placedOrder.price)
        }

        val matchingResult = matchPlacedOrderWithExistingOrders(placedOrder, oppositeOrders)

        saveMatchingResultToDB(placedOrder, matchingResult)

        return PlaceOrderResponseDTO(
            orderId = placedOrder.id!!,
            placedAmount = placedOrder.amount,
            filledAmount = matchingResult.filledAmount,
        )
    }

    internal fun matchPlacedOrderWithExistingOrders(placedOrder: Order, oppositeOrders: List<Order>): MatchingResult {
        val matchingResult = MatchingResult(mutableMapOf(), BigDecimal.ZERO)

        if (oppositeOrders.isEmpty()) {
            return matchingResult
        }

        var filledAmount = BigDecimal.ZERO
        var amountLeft = placedOrder.amount

        for (oppositeOrder in oppositeOrders) {
            if (oppositeOrder.amount <= amountLeft) {
                amountLeft -= oppositeOrder.amount
                filledAmount += oppositeOrder.amount
                matchingResult.ordersToDecreaseAmount[oppositeOrder.id!!] = oppositeOrder.amount
            } else {
                filledAmount += amountLeft
                matchingResult.ordersToDecreaseAmount[oppositeOrder.id!!] = amountLeft
                amountLeft = BigDecimal.ZERO
            }

            // Exit from the loop if the amount left to fill is zero
            if (amountLeft.compareTo(BigDecimal.ZERO) == 0) {
                break
            }
        }

        // Trade happens only if there is a filled order (fully or partially filled)
        // Several orders could be filled, and we create only one trade for all of them
        if (filledAmount > BigDecimal.ZERO) {
            matchingResult.ordersToDecreaseAmount[placedOrder.id!!] = filledAmount
            matchingResult.filledAmount = filledAmount
        }

        return matchingResult
    }

    private fun saveMatchingResultToDB(placedOrder: Order, matchingResult: MatchingResult) {
        if (matchingResult.ordersToDecreaseAmount.isNotEmpty()) {
            matchingResult.ordersToDecreaseAmount.forEach { (orderId, amount) ->
                orderRepository.decreaseAmount(orderId, amount)
            }
        }

        if (matchingResult.filledAmount > BigDecimal.ZERO) {
            val newTrade = Trade(
                price = placedOrder.price,
                side = placedOrder.side,
                amount = matchingResult.filledAmount,
            )
            tradeRepository.saveTrade(newTrade)
        }
    }
}
