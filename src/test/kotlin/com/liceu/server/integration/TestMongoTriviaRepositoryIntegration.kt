package com.liceu.server.integration

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoTriviaRepository
import com.liceu.server.data.TriviaRepository
import com.liceu.server.domain.trivia.TriviaQuestionToInsert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoTriviaRepositoryIntegration {

    @Autowired
    lateinit var data: MongoTriviaRepository
    @Autowired
    lateinit var reportRepo: TriviaRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insert_Valid_CanBeRetrieved() {

        val id = data.insert(TriviaQuestionToInsert(
                testSetup.USER_ID_1,
                "essa e uma questao de teste sobre matematica: Seno de 0?",
                "0",
                "1",
                listOf(
                        "matematica",
                        "angulos",
                        "trigonometria"
                )
        ))

        val report = data.toTriviaQuestion(reportRepo.findById(id).get())
        Truth.assertThat(report.userId).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(report.question).isEqualTo("essa e uma questao de teste sobre matematica: Seno de 0?")
        Truth.assertThat(report.correctAnswer).isEqualTo("0")
        Truth.assertThat(report.wrongAnswer).isEqualTo("1")
    }


//    @Test
    fun randomQuestions_requestOnlyOne_returnsExistent() {
        // Test will fail temporarily to avoid mongo sample aggregation error
        val questions = data.randomQuestions(listOf("historia"), 5)
        val ids = questions.map { it.id }
        Truth.assertThat(ids).containsExactly(testSetup.QUESTION_TRIVIA_ID_5)
    }

//    @Test
    fun randomQuestions_requestMajoraty_returnsExistent() {
        // Test will fail temporarily to avoid mongo sample aggregation error
        val questions = data.randomQuestions(listOf("matematica"), 5)
        val ids = questions.map { it.id }
        Truth.assertThat(ids).containsExactly(testSetup.QUESTION_TRIVIA_ID_1, testSetup.QUESTION_TRIVIA_ID_2, testSetup.QUESTION_TRIVIA_ID_3,
                testSetup.QUESTION_TRIVIA_ID_4)
    }

    @Test
    fun randomQuestions_emptyTags_returnsAll() {
        val questions = data.randomQuestions(listOf(), 5)
        val ids = questions.map { it.id }
        Truth.assertThat(ids).containsExactly(testSetup.QUESTION_TRIVIA_ID_1, testSetup.QUESTION_TRIVIA_ID_2, testSetup.QUESTION_TRIVIA_ID_3,
                testSetup.QUESTION_TRIVIA_ID_4,testSetup.QUESTION_TRIVIA_ID_5)
    }

    @Test
    fun updateCommentsTrivia_commentValid_verifyTrivia(){
        val result1 = data.updateListOfComments(testSetup.QUESTION_TRIVIA_ID_1,testSetup.USER_ID_2,"user2","questao interessante 1")
        val result2 = data.updateListOfComments(testSetup.QUESTION_TRIVIA_ID_1,testSetup.USER_ID_2,"user2","questao interessante 2")
        val result3= data.updateListOfComments(testSetup.QUESTION_TRIVIA_ID_1,testSetup.USER_ID_2,"user2","questao interessante 3")
        Truth.assertThat(result1).isEqualTo(1)
        Truth.assertThat(result2).isEqualTo(1)
        Truth.assertThat(result3).isEqualTo(1)
        val triviaChanged = data.getTriviaById(testSetup.QUESTION_TRIVIA_ID_1)
        Truth.assertThat(triviaChanged.comments?.size).isEqualTo(3)
        Truth.assertThat(triviaChanged.comments?.get(0)?.comment).isEqualTo("questao interessante 1")
        Truth.assertThat(triviaChanged.comments?.get(1)?.comment).isEqualTo("questao interessante 2")
    }



}