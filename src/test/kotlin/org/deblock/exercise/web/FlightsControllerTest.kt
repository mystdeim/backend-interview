package org.deblock.exercise.web

import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.domain.service.FlightSearchService
import org.deblock.exercise.web.controller.FlightsController
import org.deblock.exercise.web.controller.GlobalExceptionHandler
import org.deblock.exercise.web.dto.FlightDto
import org.deblock.exercise.web.dto.SearchFlightsResponseDto
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.runBlocking

@WebFluxTest(controllers = [FlightsController::class])
@Import(GlobalExceptionHandler::class)
class FlightsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var flightSearchService: FlightSearchService

    @Test
    fun `should return flight list for valid request`() = runBlocking<Unit> {
        // given
        val sampleFlight = Flight(
            airline = "TestAir",
            supplier = "CrazyAir",
            fare = BigDecimal("199.99"),
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = LocalDate.of(2024, 10, 10),
            arrivalDate = LocalDate.of(2024, 10, 10)
        )
        whenever(flightSearchService.search(any())).thenReturn(listOf(sampleFlight))

        val expectedFlightDto = FlightDto(
            airline = "TestAir",
            supplier = "CrazyAir",
            fare = BigDecimal("199.99"),
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = LocalDate.of(2024, 10, 10),
            arrivalDate = LocalDate.of(2024, 10, 10)
        )
        val expectedResponse = SearchFlightsResponseDto(flights = listOf(expectedFlightDto))

        // when / then
        webTestClient.get()
            .uri("/flights?origin=LHR&destination=AMS&departureDate=2024-10-10&returnDate=2024-10-20&numberOfPassengers=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(SearchFlightsResponseDto::class.java)
            .isEqualTo(expectedResponse)
    }

    @Test
    fun `should return 400 for invalid origin IATA code`() {
        webTestClient.get()
            .uri("/flights?origin=LH&destination=AMS&departureDate=2024-10-10&returnDate=2024-10-20&numberOfPassengers=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Constraint Violation")
    }

    @Test
    fun `should return 400 when return date is before departure date`() {
        webTestClient.get()
            .uri("/flights?origin=LHR&destination=AMS&departureDate=2024-10-10&returnDate=2024-10-05&numberOfPassengers=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Bad Request")
            .jsonPath("$.message").isEqualTo("Return date must be after departure date")
    }
}
