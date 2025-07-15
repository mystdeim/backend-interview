package org.deblock.exercise.domain.service

import org.deblock.exercise.domain.FlightSupplier
import org.deblock.exercise.domain.model.FlightSearchRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory

open class FlightSearchService(private val suppliers: List<FlightSupplier>) {
    
    private val logger = LoggerFactory.getLogger(FlightSearchService::class.java)

    open suspend fun search(request: FlightSearchRequest) = coroutineScope {
        val supplierResults = suppliers.map { supplier ->
            async {
                try {
                    supplier.searchFlights(request)
                } catch (exception: Exception) {
                    logger.error("Error searching flights with supplier ${supplier.name}: ${exception.message}", exception)
                    emptyList()
                }
            }
        }

        supplierResults.awaitAll()
            .flatten()
            .sortedBy { flight -> flight.fare }
    }
}
