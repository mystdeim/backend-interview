package org.deblock.exercise.suppliers.crazyair

import org.deblock.exercise.domain.FlightSupplier
import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.domain.model.FlightSearchRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CrazyAirSupplier(
    private val client: CrazyAirClient
) : FlightSupplier {

    override val name: String
        get() = "CrazyAir"

    override suspend fun searchFlights(request: FlightSearchRequest): List<Flight> {
        return client.getR(request.toRequest()).map { response ->
            response.toFlightResponse()
        }
    }

    private fun FlightSearchRequest.toRequest() = CrazyAirClient.Request(
        origin = origin,
        destination = destination,
        departureDate = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        returnDate = returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        passengerCount = numberOfPassengers
    )

    private fun CrazyAirClient.Response.toFlightResponse() = Flight(
        airline = airline,
        supplier = name,
        fare = BigDecimal(price).setScale(2, RoundingMode.HALF_EVEN),
        departureAirportCode = departureAirportCode,
        destinationAirportCode = destinationAirportCode,
        departureDate = LocalDate.parse(departureDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        arrivalDate = LocalDate.parse(arrivalDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}
