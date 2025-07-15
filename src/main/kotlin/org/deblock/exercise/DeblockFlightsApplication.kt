package org.deblock.exercise

import org.deblock.exercise.config.SupplierProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SupplierProperties::class)
class DeblockFlightsApplication

fun main(args: Array<String>) {
    runApplication<DeblockFlightsApplication>(*args)
}
