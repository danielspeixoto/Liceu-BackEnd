package com.liceu.server.end2end.v1

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.MongoDatabase
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.VideoRepository
import com.liceu.server.setup
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.repository.cdi.MongoRepositoryExtension
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.mongodb.repository.config.MongoRepositoryConfigurationExtension
import java.lang.System.exit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableMongoRepositories
class TestQuestionController {

    @LocalServerPort
    private var port: Int = 0
    private var baseUrl: String = ""

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/questions/"
        setup(questionRepo, videoRepo)
    }

    @Test
    @Throws(Exception::class)
    fun randomQuestion_whenNoTagsSpecified_ReturnsAnyRandomly() {
        assertThat(this.restTemplate.getForObject("$baseUrl?amount=1",
                String::class.java)).contains("Hello World")
    }
}