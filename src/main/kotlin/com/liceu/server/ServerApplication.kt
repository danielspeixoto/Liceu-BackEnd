package com.liceu.server

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import javax.annotation.PostConstruct

@EnableCircuitBreaker
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
class ServerApplication {

	@PostConstruct
	fun setProperties() {
		System.setProperty("com.google.common.truth.disable_stack_trace_cleaning", "true")
	}
}

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
