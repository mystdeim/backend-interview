package org.deblock.exercise.web.dto

import java.math.BigDecimal
import java.time.LocalDate

data class FlightDto(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDate,
    val arrivalDate: LocalDate
)
