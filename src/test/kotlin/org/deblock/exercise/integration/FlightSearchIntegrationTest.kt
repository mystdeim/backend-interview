package org.deblock.exercise.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.deblock.exercise.web.dto.SearchFlightsResponseDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.net.ServerSocket

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FlightSearchIntegrationTest {

    companion object {
        private var crazyAirWireMockPort: Int = 0
        private var toughJetWireMockPort: Int = 0

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            crazyAirWireMockPort = findAvailablePort()
            toughJetWireMockPort = findAvailablePort()
            registry.add("supplier.crazyair.enabled") { "true" }
            registry.add("supplier.crazyair.url") { "http://localhost:$crazyAirWireMockPort" }
            registry.add("supplier.toughjet.enabled") { "true" }
            registry.add("supplier.toughjet.url") { "http://localhost:$toughJetWireMockPort" }
        }

        private fun findAvailablePort(): Int {
            return ServerSocket(0).use { it.localPort }
        }
    }

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var crazyAirWireMockServer: WireMockServer
    private lateinit var toughJetWireMockServer: WireMockServer

    @BeforeEach
    fun setUp() {
        crazyAirWireMockServer = WireMockServer(crazyAirWireMockPort)
        toughJetWireMockServer = WireMockServer(toughJetWireMockPort)
        crazyAirWireMockServer.start()
        toughJetWireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        crazyAirWireMockServer.stop()
        toughJetWireMockServer.stop()
    }

    @Test
    fun `should return flights from both suppliers aggregated and sorted by fare`() {
        // given
        val crazyAirResponseJson = """
            [
              {
                "airline":"TestAir",
                "price":123.45,
                "cabinclass":"E",
                "departureAirportCode":"LHR",
                "destinationAirportCode":"AMS",
                "departureDate":"2023-09-01T10:00:00",
                "arrivalDate":"2023-09-01T12:00:00"
              }
            ]
        """.trimIndent()

        val toughJetResponseJson = """
            [
              {
                "carrier":"ToughJet Air",
                "basePrice":100.00,
                "tax":20.00,
                "discount":10.0,
                "departureAirportName":"LHR",
                "arrivalAirportName":"AMS",
                "outboundDateTime":"2023-09-01T10:00:00Z",
                "inboundDateTime":"2023-09-10T12:00:00Z"
              }
            ]
        """.trimIndent()

        // Mock the external CrazyAir service
        WireMock.configureFor("localhost", crazyAirWireMockPort)
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/"))
                .withQueryParam("origin", WireMock.equalTo("LHR"))
                .withQueryParam("destination", WireMock.equalTo("AMS"))
                .withQueryParam("departureDate", WireMock.equalTo("2023-09-01"))
                .withQueryParam("returnDate", WireMock.equalTo("2023-09-10"))
                .withQueryParam("passengerCount", WireMock.equalTo("1"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(crazyAirResponseJson)
                )
        )

        // Mock the external ToughJet service
        WireMock.configureFor("localhost", toughJetWireMockPort)
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/"))
                .withQueryParam("from", WireMock.equalTo("LHR"))
                .withQueryParam("to", WireMock.equalTo("AMS"))
                .withQueryParam("outboundDate", WireMock.equalTo("2023-09-01"))
                .withQueryParam("inboundDate", WireMock.equalTo("2023-09-10"))
                .withQueryParam("numberOfAdults", WireMock.equalTo("1"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(toughJetResponseJson)
                )
        )

        // when / then
        webTestClient.get()
            .uri("/flights?origin=LHR&destination=AMS&departureDate=2023-09-01&returnDate=2023-09-10&numberOfPassengers=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(SearchFlightsResponseDto::class.java)
            .returnResult()
            .responseBody?.let { response ->
                assert(response.flights.size == 2)
                
                // Flights should be sorted by fare (ascending)
                val sortedFlights = response.flights.sortedBy { it.fare }
                
                // First flight should be ToughJet (cheaper: 108.00)
                sortedFlights[0].let { flight ->
                    assert(flight.airline == "ToughJet Air")
                    assert(flight.supplier == "ToughJet")
                    // Expected price: (100 + 20) - (120 * 0.1) = 120 - 12 = 108.00
                    assert(flight.fare == BigDecimal("108.00"))
                }
                
                // Second flight should be CrazyAir (more expensive: 123.45)
                sortedFlights[1].let { flight ->
                    assert(flight.airline == "TestAir")
                    assert(flight.supplier == "CrazyAir")
                    assert(flight.fare == BigDecimal("123.45"))
                }
            }
    }
}
