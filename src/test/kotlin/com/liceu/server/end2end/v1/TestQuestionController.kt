package com.liceu.server.end2end.v1

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.MongoDatabase
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.VideoRepository
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



        questionRepo.deleteAll()
        videoRepo.deleteAll()
        val q1 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2017,
                3,
                "matemática",
                1,
                listOf("primeira", "segunda"),
                "12345",
                "referenceId",
                2,
                100,
                200
        )
        q1.id = "id1"
        questionRepo.insert(q1)
        val q2 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2016,
                5,
                "linguagens",
                1,
                listOf("segunda"),
                "54321",
                "referenceId2",
                2,
                100,
                200
        )
        q2.id = "id2"
        questionRepo.insert(q2)
        val q3 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AZUL",
                2015,
                15,
                "linguagens",
                1,
                listOf(),
                "54321",
                "referenceId3",
                1,
                100,
                200
        )
        q3.id = "id3"
        questionRepo.insert(q3)

        val item1 = MongoDatabase.MongoVideo(
                "primeira",
                "primeiro video",
                "videoId1",
                "id1",
                1.1f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                3
        )
        item1.id = "id1"
        videoRepo.insert(item1)
        val item2 = MongoDatabase.MongoVideo(
                "segundo",
                "segundo video",
                "videoId3",
                "id2",
                1.1f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                2
        )
        item2.id = "id2"
        videoRepo.insert(item2)
        val item3 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                "id1",
                1.3f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                1
        )
        item3.id = "id3"
        videoRepo.insert(item3)
    }

    @Test
    @Throws(Exception::class)
    fun randomQuestion_whenNoTagsSpecified_ReturnsAnyRandomly() {
        assertThat(this.restTemplate.getForObject("$baseUrl?amount=1",
                String::class.java)).contains("Hello World")
    }
}