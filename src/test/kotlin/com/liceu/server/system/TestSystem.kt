package com.liceu.server.system

import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@EnableMongoRepositories
class TestSystem(val endpoint: String) {

    @LocalServerPort
    var port: Int = 0
    var baseUrl: String = ""

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var testSetup: DataSetup

    @Value("\${client.api_key}")
    lateinit var apiKey: String



    val googleServerAuthCode = "4/nQF47iLi0DqRPU39i-TGqNgEwp9tHcTk1Nlym51k7E2zuHhroqQZY3c7JHUfFK_TPOHjwFCS1NqNqPcv4kAn128"

    @BeforeEach
    fun setup() {
        testSetup.setup()
        baseUrl = "http://localhost:$port$endpoint"
    }
}