package org.deblock.exercise.domain

import org.deblock.exercise.domain.model.Flight
import org.deblock.exercise.domain.model.FlightSearchRequest

interface FlightSupplier {
    val name: String
    suspend fun searchFlights(request: FlightSearchRequest): List<Flight>
} 