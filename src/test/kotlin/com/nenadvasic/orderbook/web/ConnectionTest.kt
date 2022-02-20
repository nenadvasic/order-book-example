package com.nenadvasic.orderbook.web

import io.ktor.websocket.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ConnectionTest {
    @Test
    fun `should initialize Connection class`() {
        // Given
        val session1 = mockk<DefaultWebSocketServerSession>()
        val session2 = mockk<DefaultWebSocketServerSession>()
        val expectedConnectionName1 = "client" + Connection.lastId.toInt()
        val expectedConnectionName2 = "client" + (Connection.lastId.toInt() + 1)

        // When
        val connection1 = Connection(session1)
        val connection2 = Connection(session2)

        // Then
        assertThat(connection1.name).isEqualTo(expectedConnectionName1)
        assertThat(connection2.name).isEqualTo(expectedConnectionName2)
    }

    @Test
    fun `should send a message to the client`() {
        // Given
        val session = mockk<DefaultWebSocketServerSession>()
        val connection = Connection(session)

        coEvery { session.send(any()) } returns Unit

        // When
        runTest {
            connection.send("Ahoy!")
        }

        // Then
        coVerify(exactly = 1) { session.send(any()) }
    }
}
