package org.deblock.exercise.suppliers.toughjet

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import kotlinx.coroutines.reactor.awaitSingle

class ToughJetClient(
    private val webClient: WebClient,
    private val toughJetBaseUrl: String
) {

    suspend fun search(request: Request): List<Response> {
        val url = UriComponentsBuilder.fromHttpUrl(toughJetBaseUrl)
            .queryParam("from", request.from)
            .queryParam("to", request.to)
            .queryParam("outboundDate", request.outboundDate)
            .queryParam("inboundDate", request.inboundDate)
            .queryParam("numberOfAdults", request.numberOfAdults)
            .toUriString()

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(Array<Response>::class.java)
            .awaitSingle()
            ?.toList() ?: emptyList()
    }

    data class Request(
        val from: String,
        val to: String,
        val outboundDate: String,
        val inboundDate: String,
        val numberOfAdults: Int,
    )

    data class Response(
        val carrier: String,
        val basePrice: Double,
        val tax: Double,
        val discount: Double,
        val departureAirportName: String,
        val arrivalAirportName: String,
        val outboundDateTime: String,
        val inboundDateTime: String,
    )
}
