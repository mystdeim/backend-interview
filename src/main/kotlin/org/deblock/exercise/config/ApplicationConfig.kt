package org.deblock.exercise.config

import org.deblock.exercise.domain.FlightSupplier
import org.deblock.exercise.domain.service.FlightSearchService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ApplicationConfig {

    @Bean
    fun webClient(): WebClient = WebClient.builder().build()

    @Bean
    fun flightSearchService(suppliers: List<FlightSupplier>): FlightSearchService {
        return FlightSearchService(suppliers)
    }
}
