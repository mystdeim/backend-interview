package org.deblock.exercise.domain

import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.domain.model.FlightSearchRequest
import org.deblock.exercise.domain.service.FlightSearchService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.runBlocking

class FlightSearchServiceTest {

    private val sampleRequest = FlightSearchRequest(
        origin = "LHR",
        destination = "AMS",
        departureDate = LocalDate.of(2024, 1, 1),
        returnDate = LocalDate.of(2024, 1, 10),
        numberOfPassengers = 1
    )

    @Test
    fun `search aggregates results from all suppliers and sorts by fare ascending`() = runBlocking<Unit> {
        // Arrange
        val supplier1 = mock<FlightSupplier>()
        val supplier2 = mock<FlightSupplier>()

        val flight1 = Flight(
            airline = "Airline A",
            supplier = supplier1.toString(),
            fare = BigDecimal("200.00"),
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = LocalDate.of(2024, 1, 1),
            arrivalDate = LocalDate.of(2024, 1, 1)
        )
        val flight2 = Flight(
            airline = "Airline B",
            supplier = supplier2.toString(),
            fare = BigDecimal("150.00"),
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = LocalDate.of(2024, 1, 1),
            arrivalDate = LocalDate.of(2024, 1, 1)
        )
        val flight3 = Flight(
            airline = "Airline C",
            supplier = supplier1.toString(),
            fare = BigDecimal("250.00"),
            departureAirportCode = "LHR",
            destinationAirportCode = "AMS",
            departureDate = LocalDate.of(2024, 1, 1),
            arrivalDate = LocalDate.of(2024, 1, 1)
        )

        whenever(supplier1.searchFlights(sampleRequest)).thenReturn(listOf(flight1, flight3))
        whenever(supplier2.searchFlights(sampleRequest)).thenReturn(listOf(flight2))

        val service = FlightSearchService(listOf(supplier1, supplier2))

        // Act
        val result = service.search(sampleRequest)

        // Assert
        assertEquals(listOf(flight2, flight1, flight3), result)
    }

    @Test
    fun `search returns an empty list when no supplier returns flights`() = runBlocking<Unit> {
        // Arrange
        val supplier1 = mock<FlightSupplier>()
        val supplier2 = mock<FlightSupplier>()

        whenever(supplier1.searchFlights(sampleRequest)).thenReturn(emptyList())
        whenever(supplier2.searchFlights(sampleRequest)).thenReturn(emptyList())

        val service = FlightSearchService(listOf(supplier1, supplier2))

        // Act
        val result = service.search(sampleRequest)

        // Assert
        assertTrue(result.isEmpty())
    }
}
