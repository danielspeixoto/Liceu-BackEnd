package com.liceu.server.data

import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

//@RunWith(SpringJUnit4ClassRunner::class)
///@SpringBootTest(classes = arrayOf(AppConfig::class))
@ComponentScan
@ExtendWith(SpringExtension::class)
@DataMongoTest
class TestQuestionRepository {

    @Autowired
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var repo: QuestionRepository

    @BeforeEach
    fun dataSetup() {
        val question = MongoQuestionRepository.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2017,
                3,
                "matemática",
                1,
                arrayListOf("primeira", "segunda"),
                "12345",
                "referenceId",
                2,
                100,
                200
                )
        question.id = "id1"
        repo.insert(question)
    }

    @AfterEach
    fun destroy() {
        repo.deleteAll()
    }

    @Test
    fun addTag() {
        data.addTag("id1", "terceira")

        assertThat(repo.findById("id1").get().tags)
                .containsExactly("primeira", "segunda", "terceira")
    }
}