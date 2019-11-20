package com.liceu.server

import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthContributorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import javax.annotation.PostConstruct
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration




@EnableCircuitBreaker
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class, ElasticsearchAutoConfiguration::class, ElasticsearchDataAutoConfiguration::class, RestClientAutoConfiguration::class])
class ServerApplication {

	@PostConstruct
	fun setProperties() {
		System.setProperty("com.google.common.truth.disable_stack_trace_cleaning", "true")
	}
}

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
