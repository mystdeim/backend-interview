package org.deblock.exercise.config

import org.deblock.exercise.domain.FlightSupplier
import org.deblock.exercise.suppliers.crazyair.CrazyAirClient
import org.deblock.exercise.suppliers.crazyair.CrazyAirSupplier
import org.deblock.exercise.suppliers.toughjet.ToughJetClient
import org.deblock.exercise.suppliers.toughjet.ToughJetSupplier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SupplierConfig {

    @Bean
    @ConditionalOnProperty(prefix = "supplier.crazyair", name = ["enabled"], havingValue = "true")
    fun crazyAirClient(
        webClient: WebClient,
        supplierProperties: SupplierProperties
    ): CrazyAirClient {
        return CrazyAirClient(webClient, supplierProperties.crazyair.url)
    }

    @Bean
    @ConditionalOnProperty(prefix = "supplier.crazyair", name = ["enabled"], havingValue = "true")
    fun crazyAirSupplier(crazyAirClient: CrazyAirClient): FlightSupplier {
        return CrazyAirSupplier(crazyAirClient)
    }

    @Bean
    @ConditionalOnProperty(prefix = "supplier.toughjet", name = ["enabled"], havingValue = "true")
    fun toughJetClient(
        webClient: WebClient,
        supplierProperties: SupplierProperties
    ): ToughJetClient {
        return ToughJetClient(webClient, supplierProperties.toughjet.url)
    }

    @Bean
    @ConditionalOnProperty(prefix = "supplier.toughjet", name = ["enabled"], havingValue = "true")
    fun toughJetSupplier(toughJetClient: ToughJetClient): FlightSupplier {
        return ToughJetSupplier(toughJetClient)
    }
} 