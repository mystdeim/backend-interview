package org.deblock.exercise.suppliers.crazyair

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import kotlinx.coroutines.reactor.awaitSingle

class CrazyAirClient(
    private val webClient: WebClient,
    private val crazyAirBaseUrl: String
) {

    suspend fun getR(request: Request): List<Response> {
        val url = UriComponentsBuilder.fromHttpUrl(crazyAirBaseUrl)
            .queryParam("origin", request.origin)
            .queryParam("destination", request.destination)
            .queryParam("departureDate", request.departureDate)
            .queryParam("returnDate", request.returnDate)
            .queryParam("passengerCount", request.passengerCount)
            .toUriString()

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(Array<Response>::class.java)
            .awaitSingle()
            ?.toList() ?: emptyList()
    }

    data class Request(
        val origin: String,
        val destination: String,
        val departureDate: String,
        val returnDate: String,
        val passengerCount: Int,
    )

    data class Response(
        val airline: String,
        val price: Double,
        val cabinclass: String,
        val departureAirportCode: String,
        val destinationAirportCode: String,
        val departureDate: String,
        val arrivalDate: String,
    )
}
