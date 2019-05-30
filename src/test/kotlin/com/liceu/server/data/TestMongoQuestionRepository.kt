package com.liceu.server.data

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.domain.exception.AlreadyExistsException
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.question.Question
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception

@ComponentScan
@ExtendWith(SpringExtension::class)
@DataMongoTest
class TestMongoQuestionRepository {

    @Autowired
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var repo: QuestionRepository

    @BeforeEach
    fun dataSetup() {
        val q1 = MongoQuestionRepository.MongoQuestion(
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
        q1.id = "id1"
        repo.insert(q1)
        val q2 = MongoQuestionRepository.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2016,
                5,
                "linguagens",
                1,
                arrayListOf("segunda"),
                "54321",
                "referenceId2",
                2,
                100,
                200
        )
        q2.id = "id2"
        repo.insert(q2)
        val q3 = MongoQuestionRepository.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AZUL",
                2015,
                15,
                "linguagens",
                1,
                arrayListOf(),
                "54321",
                "referenceId3",
                1,
                100,
                200
        )
        q3.id = "id3"
        repo.insert(q3)
    }

    @AfterEach
    fun destroy() {
        repo.deleteAll()
    }

    @Test
    fun addTag_hasDifferentTags_adds() {
        data.addTag("id1", "terceira")

        assertThat(repo.findById("id1").get().tags)
                .containsExactly("primeira", "segunda", "terceira")
                .inOrder()
    }

    @Test
    fun addTag_hasNoTags_adds() {
        data.addTag("id3", "terceira")

        assertThat(repo.findById("id3").get().tags)
                .containsExactly("terceira")
                .inOrder()
    }

    @Test
    fun addTag_alreadyHasTag_throwsError() {
        try {
            data.addTag("id1", "primeira")
            fail("should throw exception")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(AlreadyExistsException::class.java)
        }

        assertThat(repo.findById("id1").get().tags)
                .containsExactly("primeira", "segunda")
                .inOrder()
    }

    @Test
    fun addTag_nonExistentQuestion_throwsError() {
        try {
            data.addTag("id0", "primeira")
            fail("should throw exception")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(ItemNotFoundException::class.java)
        }
    }

    @Test
    fun randomByTags_requestMoreThanExists_returnsExistent() {
        val questions = data.randomByTags(listOf("segunda"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly("id1", "id2")
    }

    @Test
    fun randomByTags_requestLessThanExists_returnsExistentRandomly() {
        val ids = arrayListOf<String>()
        var i = 0
        while (i < 10) {
            val questions = data.randomByTags(listOf("segunda"), 1)
            assertThat(questions.size).isEqualTo(1)
            assertThat(questions[0].id).matches("id[12]")
            ids.add(questions[0].id)
            i++
        }
        assertThat(ids).containsAtLeast("id1", "id2")
    }

    @Test
    fun randomByTags_notEveryoneHasTag_filters() {
        val questions = data.randomByTags(listOf("primeira"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly("id1")
    }

    @Test
    fun randomByTags_noOneHasTag_empty() {
        val questions = data.randomByTags(listOf("ultima"), 10)
        assertThat(questions).isEmpty()
    }

    @Test
    fun randomByTags_correctRequest_dataIsValid() {
        val result = data.randomByTags(listOf("primeira"), 10)[0]

        val question = Question(
                "id1",
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

        EqualsTester()
                .addEqualityGroup(question, result)
                .testEquals()
    }
}