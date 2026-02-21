package com.calendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CalendarBackendApplication

fun main(args: Array<String>) {
    runApplication<CalendarBackendApplication>(*args)
}
