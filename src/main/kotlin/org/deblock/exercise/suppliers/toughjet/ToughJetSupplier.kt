package org.deblock.exercise.suppliers.toughjet

import org.deblock.exercise.domain.FlightSupplier
import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.domain.model.FlightSearchRequest
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ToughJetSupplier(
    private val client: ToughJetClient
) : FlightSupplier {

    override val name: String
        get() = "ToughJet"

    override suspend fun searchFlights(request: FlightSearchRequest): List<Flight> {
        return client.search(request.toRequest()).map { response ->
            response.toFlightResponse()
        }
    }

    private fun FlightSearchRequest.toRequest() = ToughJetClient.Request(
        from = origin,
        to = destination,
        outboundDate = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        inboundDate = returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        numberOfAdults = numberOfPassengers
    )

    private fun ToughJetClient.Response.toFlightResponse(): Flight {
        // Calculate final price: basePrice + tax - discount
        val basePriceWithTax = BigDecimal(basePrice + tax)
        val discountAmount = basePriceWithTax.multiply(BigDecimal(discount / 100.0))
        val finalPrice = basePriceWithTax.subtract(discountAmount)
        
        // Parse ISO_INSTANT format to LocalDate
        val outboundDate = Instant.parse(outboundDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val inboundDate = Instant.parse(inboundDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return Flight(
            airline = carrier,
            supplier = name,
            fare = finalPrice.setScale(2, RoundingMode.HALF_EVEN),
            departureAirportCode = departureAirportName,
            destinationAirportCode = arrivalAirportName,
            departureDate = outboundDate,
            arrivalDate = inboundDate
        )
    }
}
