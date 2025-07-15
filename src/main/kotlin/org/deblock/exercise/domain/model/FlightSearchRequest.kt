package org.deblock.exercise.domain.model

import java.time.LocalDate

data class FlightSearchRequest(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val numberOfPassengers: Int
)
