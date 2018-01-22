package com.comsysto.kotlinfullstack.backend

import com.comsysto.kotlinfullstack.backend.inbound.CurrencyDataRepository
import org.junit.Before
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import reactor.test.StepVerifier
import java.math.BigDecimal
import assertk.assert
import assertk.assertAll
import assertk.assertions.*

internal class CryptoStockServiceTest {

    val currencyKeys = listOf("MOCK")
    val valuesList = listOf(BigDecimal("0.99"), BigDecimal("0.42"))

    private lateinit var testee: CryptoStockService

    @Before
    fun setUp() {
        val repositoryMock = object : CurrencyDataRepository {
            override fun currencyKey(): String = currencyKeys[0]
            override fun dataStream(): Flux<BigDecimal> {
                return valuesList.toFlux()
            }
        }
        testee = CryptoStockService(listOf(repositoryMock))
    }

    @Test
    fun currentPriceStream() {
        StepVerifier.create(testee.currentPriceStream(currencyKeys.subList(0, 1)))
                .consumeNextWith({
                    assertAll {
                        assert(it.currency).isEqualTo(currencyKeys[0])
                        assert(it.price).isEqualTo(valuesList[0])
                    }
                })
                .consumeNextWith({
                    assertAll {
                        assert(it.currency).isEqualTo(currencyKeys[0])
                        assert(it.price).isEqualTo(valuesList[1])
                    }
                })
                .verifyComplete()
    }
}