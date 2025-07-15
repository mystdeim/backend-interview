package org.deblock.exercise.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "supplier")
data class SupplierProperties(
    val crazyair: CrazyAirProperties = CrazyAirProperties(),
    val toughjet: ToughJetProperties = ToughJetProperties()
)

data class CrazyAirProperties(
    val enabled: Boolean = true,
    val url: String = ""
)

data class ToughJetProperties(
    val enabled: Boolean = false,
    val url: String = ""
)
