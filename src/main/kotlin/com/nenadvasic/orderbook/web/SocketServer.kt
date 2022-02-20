package com.nenadvasic.orderbook.web

import com.nenadvasic.orderbook.service.OrderBookService
import com.nenadvasic.orderbook.service.OrderMatchingService
import com.nenadvasic.orderbook.service.dto.PlaceOrderRequestDTO
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.util.*
import kotlin.collections.LinkedHashSet

// Please note that connection timeout is not working properly due to the bug in Ktor
// https://youtrack.jetbrains.com/issue/KTOR-3504
class SocketServer(
    private val orderBookService: OrderBookService,
    private val ordersMatchingService: OrderMatchingService,
) {
    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    fun addClientConnection(session: DefaultWebSocketServerSession): Connection {
        val c = Connection(session)
        println("${c.name}: Connected") // TODO Logging
        connections.add(c)
        return c
    }

    fun removeClientConnection(c: Connection) {
        println("${c.name}: Disconnected") // TODO Logging
        connections.remove(c)
    }

    suspend fun sendInitialContentToClient(c: Connection) {
        sendOrderBookToClient(c)
        sendTradeHistoryToClient(c)
    }

    suspend fun sendMessageToAllClients(msg: String) {
        connections.forEach {
            it.send(msg)
        }
    }

    suspend fun readIncomingMessages(c: Connection, incoming: ReceiveChannel<Frame>) {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue

            val text = frame.readText()

            val message: ClientMessage = try {
                Json.decodeFromString(text)
            } catch (e: SerializationException) {
                println(e.toString()) // TODO Logging
                continue
            }

            if (message.type == MessageType.PlaceOrder) {
                val placeOrderRequestDTO: PlaceOrderRequestDTO = try {
                    Json.decodeFromString(message.data)
                } catch (e: SerializationException) {
                    println(e.toString()) // TODO Logging
                    continue
                }
                processPlaceOrderRequest(c, placeOrderRequestDTO)
            }
        }
    }

    private suspend fun processPlaceOrderRequest(c: Connection, placeOrderRequest: PlaceOrderRequestDTO) {
        println("${c.name}: $placeOrderRequest") // TODO Logging

        val placeOrderResponse = ordersMatchingService.processPlaceOrderRequest(placeOrderRequest)

        println("${c.name} $placeOrderResponse") // TODO Logging

        val msg = ServerMessage(MessageType.PlaceOrderResponse, placeOrderResponse)
        c.send(Json.encodeToString(msg))

        updateOrderBookOnAllClients()

        if (placeOrderResponse.filledAmount > BigDecimal.ZERO) {
            updateTradeHistoryOnAllClients()
        }
    }

    private suspend fun sendOrderBookToClient(c: Connection) {
        val orderBook = orderBookService.getOrderBook()
        val msg = ServerMessage(MessageType.OrderBook, orderBook)
        c.send(Json.encodeToString(msg))
    }

    private suspend fun sendTradeHistoryToClient(c: Connection) {
        val tradeHistory = orderBookService.getTradeHistory()
        val msg = ServerMessage(MessageType.TradeHistory, tradeHistory)
        c.send(Json.encodeToString(msg))
    }

    private suspend fun updateOrderBookOnAllClients() {
        val orderBook = orderBookService.getOrderBook()
        val msg = ServerMessage(MessageType.OrderBook, orderBook)
        sendMessageToAllClients(Json.encodeToString(msg))
    }

    private suspend fun updateTradeHistoryOnAllClients() {
        val tradeHistory = orderBookService.getTradeHistory()
        val msg = ServerMessage(MessageType.TradeHistory, tradeHistory)
        sendMessageToAllClients(Json.encodeToString(msg))
    }
}
