package com.nenadvasic.orderbook.repository

import com.nenadvasic.orderbook.TestContainer
import com.nenadvasic.orderbook.model.Trade
import com.nenadvasic.orderbook.model.enums.Side
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.*

// TODO Move integration tests to a separate directory
@TestInstance(Lifecycle.PER_CLASS)
internal class TradeRepositoryMysqlTest {
    companion object {
        val database = TestContainer().initDatabase()
        val tradeRepository: TradeRepository = TradeRepositoryMysql(database)
    }

    @BeforeEach
    fun beforeEach() {
        transaction(database) {
            object : Table("trades") {}.deleteAll()
        }
    }

    @DisplayName("Create a trade")
    @Nested
    inner class CreateTrade {
        @Test
        fun `should create a new buying trade`() {
            // Given
            val newTrade = Trade(
                id = 19132,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = 200.0,
                side = Side.BUY,
                amount = 50.0,
            )

            // When
            val createdTrade = tradeRepository.saveTrade(newTrade)

            // Then
            assertThat(newTrade).isEqualTo(createdTrade)
        }

        @Test
        fun `should create a new selling trade`() {
            // Given
            val newTrade = Trade(
                id = 21133,
                time = Instant.parse("2022-02-15T15:46:43.554Z"),
                price = 123.0,
                side = Side.SELL,
                amount = 13.0,
            )

            // When
            val createdTrade = tradeRepository.saveTrade(newTrade)

            // Then
            assertThat(newTrade).isEqualTo(createdTrade)
        }
    }

    // TODO Get single trade

    @DisplayName("Get trades")
    @Nested
    inner class GetTrades {
        @Test
        fun `should return a list of all trades`() {
            // Given
            val listOfTrades = listOf(
                Trade(price = 200.0, side = Side.BUY, amount = 50.0),
                Trade(price = 100.0, side = Side.SELL, amount = 30.0),
                Trade(price = 80.0, side = Side.BUY, amount = 1.0),
                Trade(price = 30.0, side = Side.SELL, amount = 5.0),
            )

            // When
            listOfTrades.forEach {
                tradeRepository.saveTrade(it)
            }

            val trades = tradeRepository.getAllTrades()

            // Then
            assertThat(trades.size).isEqualTo(4)
        }

        @Test
        fun `should return the last trade`() {
            // Given
            val listOfTrades = listOf(
                Trade(id = 29, price = 200.0, side = Side.BUY, amount = 50.0),
                Trade(id = 30, price = 100.0, side = Side.SELL, amount = 30.0),
                Trade(id = 31, price = 80.0, side = Side.BUY, amount = 1.0),
                Trade(
                    id = 32,
                    time = Instant.parse("2022-02-15T15:46:43Z"),
                    price = 30.0,
                    side = Side.SELL,
                    amount = 5.0
                ),
            )

            // When
            listOfTrades.forEach {
                tradeRepository.saveTrade(it)
            }

            val lastTrade = tradeRepository.getLastTrade()

            // Then
            assertThat(lastTrade).isEqualTo(listOfTrades[3])
        }
    }
}
