package org.deblock.exercise.web.controller

import org.deblock.exercise.domain.model.FlightSearchRequest
import org.deblock.exercise.domain.service.FlightSearchService
import org.deblock.exercise.web.converter.FlightConverter
import org.deblock.exercise.web.dto.SearchFlightsResponseDto
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

@RestController
@RequestMapping("/flights")
@Validated
class FlightsController(private val flightSearchService: FlightSearchService) {

    @GetMapping
    suspend fun searchFlights(
        @RequestParam("origin") @Pattern(
            regexp = IATA_CODE_PATTERN,
            message = "Origin $IATA_CODE_MESSAGE"
        ) origin: String,
        @RequestParam("destination") @Pattern(
            regexp = IATA_CODE_PATTERN,
            message = "Destination $IATA_CODE_MESSAGE"
        ) destination: String,
        @RequestParam("departureDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) departureDate: LocalDate,
        @RequestParam("returnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) returnDate: LocalDate,
        @RequestParam("numberOfPassengers") @Min(value = 1, message = "Number of passengers must be at least 1") @Max(
            value = 4,
            message = "Number of passengers cannot exceed 4"
        ) numberOfPassengers: Int,
    ): SearchFlightsResponseDto {
        if (returnDate.isBefore(departureDate)) {
            throw IllegalArgumentException("Return date must be after departure date")
        }
        if (origin == destination) {
            throw IllegalArgumentException("Origin and destination cannot be the same")
        }

        val request = FlightSearchRequest(
            origin.uppercase(),
            destination.uppercase(),
            departureDate,
            returnDate,
            numberOfPassengers
        )
        val flights = flightSearchService.search(request).map { FlightConverter.toDto(it) }
        return SearchFlightsResponseDto(flights)
    }

    companion object {
        private const val IATA_CODE_PATTERN = "^[A-Z]{3}$"
        private const val IATA_CODE_MESSAGE = "Must be a 3-letter IATA code"
    }
}
