package org.deblock.exercise.web.converter

import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.web.dto.FlightDto

object FlightConverter {

    fun toDto(flight: Flight): FlightDto =
        FlightDto(
            airline = flight.airline,
            supplier = flight.supplier,
            fare = flight.fare,
            departureAirportCode = flight.departureAirportCode,
            destinationAirportCode = flight.destinationAirportCode,
            departureDate = flight.departureDate,
            arrivalDate = flight.arrivalDate
        )
}
