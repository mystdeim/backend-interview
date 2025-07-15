package org.deblock.exercise.web.dto

data class SearchFlightsResponseDto(
    val flights: List<FlightDto> = emptyList()
)
