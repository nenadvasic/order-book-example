package com.nenadvasic.orderbook.web

import com.nenadvasic.orderbook.helpers.toBigDecimal
import com.nenadvasic.orderbook.model.enums.Side
import com.nenadvasic.orderbook.service.OrderBookService
import com.nenadvasic.orderbook.service.OrderMatchingService
import com.nenadvasic.orderbook.service.dto.OrderBookDTO
import com.nenadvasic.orderbook.service.dto.OrderBookRowDTO
import com.nenadvasic.orderbook.service.dto.PlaceOrderResponseDTO
import com.nenadvasic.orderbook.service.dto.TradeHistoryRowDTO
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SocketServerTest {
    private val orderBookService = mockk<OrderBookService>(relaxed = true)
    private val ordersMatchingService = mockk<OrderMatchingService>(relaxed = true)
    private val socketServer = SocketServer(orderBookService, ordersMatchingService)

    @Test
    fun `should add a new client connection`() {
        // Given
        val sessions = List(15) { mockk<DefaultWebSocketServerSession>() }

        // When
        val connections = sessions.map {
            socketServer.addClientConnection(it)
        }

        // Then
        assertThat(connections[0]).isInstanceOf(Connection::class.java)
        assertThat(socketServer.connections.size).isEqualTo(15)
    }

    @Test
    fun `should remove a client connection`() {
        // Given
        val sessions = List(10) { mockk<DefaultWebSocketServerSession>() }

        // When
        val connections = sessions.map {
            socketServer.addClientConnection(it)
        }

        connections.forEach {
            socketServer.removeClientConnection(it)
        }

        // Then
        assertThat(socketServer.connections.size).isEqualTo(0)
    }

    @Test
    fun `should send initial content to a new client`() {
        // Given
        val session = mockk<DefaultWebSocketServerSession>()

        every { orderBookService.getOrderBook() } returns getOrderBookExample()
        every { orderBookService.getTradeHistory() } returns getTradeHistoryExample()
        coEvery { session.send(any()) } returns Unit

        // When
        val connection = socketServer.addClientConnection(session)

        runTest {
            socketServer.sendInitialContentToClient(connection)
        }

        // Then
        verify { orderBookService.getOrderBook() }
        verify { orderBookService.getTradeHistory() }
        coVerify(exactly = 2) { session.send(any()) }
    }

    @Test
    fun `should send a message to all clients`() {
        // Given
        val sessions = List(5) { mockk<DefaultWebSocketServerSession>() }

        // When
        sessions.forEach {
            socketServer.addClientConnection(it)
            coEvery { it.send(any()) } returns Unit
        }

        runTest {
            socketServer.sendMessageToAllClients("Ahoy!")
        }

        // Then
        sessions.forEach {
            coVerify(exactly = 1) { it.send(any()) }
        }
    }

    @Test
    fun `should read any incoming message without failure`() {
        // Given
        val session = mockk<DefaultWebSocketServerSession>()

        // When
        val connection = socketServer.addClientConnection(session)

        runTest {
            val incoming: ReceiveChannel<Frame> = produce { send(Frame.Text("Ahoy unstructured message!")) }
            socketServer.readIncomingMessages(connection, incoming)
        }

        // Then all good
    }

    @Test
    fun `should process a place order request from an incoming message`() {
        // Given
        val placingOrderSession = mockk<DefaultWebSocketServerSession>()
        val otherSessions = List(3) { mockk<DefaultWebSocketServerSession>() }

        every { ordersMatchingService.processPlaceOrderRequest(any()) } returns getPlaceOrderResponseDTOExample()
        every { orderBookService.getOrderBook() } returns getOrderBookExample()
        every { orderBookService.getTradeHistory() } returns getTradeHistoryExample()
        coEvery { placingOrderSession.send(any()) } returns Unit

        val incomingMessageText = """
            {"type":"PlaceOrder","data":"{\"price\":\"6000\",\"side\":\"SELL\",\"amount\":\"9999999\"}"}
        """.trimIndent()

        // When
        val placingOrderConnection = socketServer.addClientConnection(placingOrderSession)

        otherSessions.forEach {
            socketServer.addClientConnection(it)
            coEvery { it.send(any()) } returns Unit
        }

        runTest {
            val incoming: ReceiveChannel<Frame> = produce { send(Frame.Text(incomingMessageText)) }
            socketServer.readIncomingMessages(placingOrderConnection, incoming)
        }

        // Then
        coVerify(exactly = 3) { placingOrderSession.send(any()) }

        otherSessions.forEach {
            coVerify(exactly = 2) { it.send(any()) }
        }
    }

    @Test
    fun `should process a malformed place order request without failure`() {
        // Given
        val placingOrderSession = mockk<DefaultWebSocketServerSession>()

        val incomingMessageText = """
            {"type":"PlaceOrder","data":"{\"price_ERROR_field\":\"6000\",\"side\":\"SELL\",\"amount\":\"9999999\"}"}
        """.trimIndent()

        // When
        val placingOrderConnection = socketServer.addClientConnection(placingOrderSession)

        runTest {
            val incoming: ReceiveChannel<Frame> = produce { send(Frame.Text(incomingMessageText)) }
            socketServer.readIncomingMessages(placingOrderConnection, incoming)
        }

        // Then all good
    }

    private fun getOrderBookExample(): OrderBookDTO {
        return OrderBookDTO(
            sellOrders = listOf(
                OrderBookRowDTO(
                    orderId = 21,
                    price = toBigDecimal(5000.0),
                    amount = toBigDecimal(5.0),
                    total = toBigDecimal(170.0)
                )
            ),
            buyOrders = listOf(
                OrderBookRowDTO(
                    orderId = 25,
                    price = toBigDecimal(1050.0),
                    amount = toBigDecimal(20.0),
                    total = toBigDecimal(20.0)
                )
            ),
            lastPrice = toBigDecimal(1455.5),
            lastSide = Side.SELL.toString(),
        )
    }

    private fun getTradeHistoryExample(): List<TradeHistoryRowDTO> {
        return listOf(
            TradeHistoryRowDTO(
                tradeId = 32,
                time = Instant.parse("2022-02-15T15:55:43.554Z"),
                price = toBigDecimal(5000.0),
                side = Side.SELL.toString(),
                amount = toBigDecimal(5.0)
            ),
        )
    }

    private fun getPlaceOrderResponseDTOExample(): PlaceOrderResponseDTO {
        return PlaceOrderResponseDTO(
            orderId = 123456,
            placedAmount = toBigDecimal(313.0),
            filledAmount = toBigDecimal(143.3),
        )
    }
}
