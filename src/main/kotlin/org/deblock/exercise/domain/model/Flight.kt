package org.deblock.exercise.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDate,
    val arrivalDate: LocalDate
)
