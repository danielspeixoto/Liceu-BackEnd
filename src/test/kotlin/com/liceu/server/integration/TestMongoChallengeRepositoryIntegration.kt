package com.liceu.server.integration

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.*
import com.liceu.server.domain.challenge.ChallengeToInsert
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.trivia.TriviaQuestion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoChallengeRepositoryIntegration {

    @Autowired
    lateinit var data: MongoChallengeRepository
    @Autowired
    lateinit var repo: ChallengeRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insert_Valid_CanBeRetrieved(){

        val report = data.createChallenge(ChallengeToInsert(
                testSetup.USER_ID_1,
                testSetup.USER_ID_2,
                listOf(
                        "oaai",
                        "aaabriu",
                        "taaestando",
                        "4aa"
                ),
                listOf(
                        "oaai2",
                        "abaariu2",
                        "teaastando2",
                        "2aa"
                ),
                6,
                3,
                listOf(
                        TriviaQuestion(
                                testSetup.QUESTION_TRIVIA_ID_1,
                                testSetup.USER_ID_4,
                                "1+2?",
                                "3",
                                "0",
                                listOf(
                                        "graficos",
                                        "algebra"
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                true
        ))


        Truth.assertThat(report.challenger).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(report.challenged).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(report.answersChallenger[0]).isEqualTo("oaai")
        Truth.assertThat(report.answersChallenger[1]).isEqualTo("aaabriu")
        Truth.assertThat(report.answersChallenger[2]).isEqualTo("taaestando")
        Truth.assertThat(report.answersChallenger[3]).isEqualTo("4aa")
        Truth.assertThat(report.answersChallenged[0]).isEqualTo("oaai2")
        Truth.assertThat(report.answersChallenged[1]).isEqualTo("abaariu2")
        Truth.assertThat(report.answersChallenged[2]).isEqualTo("teaastando2")
        Truth.assertThat(report.answersChallenged[3]).isEqualTo("2aa")
        Truth.assertThat(report.scoreChallenger).isEqualTo(6)
        Truth.assertThat(report.scoreChallenged).isEqualTo(3)
        Truth.assertThat(report.triviaQuestionsUsed[0].id).isEqualTo(testSetup.QUESTION_TRIVIA_ID_1)
        Truth.assertThat(report.triviaQuestionsUsed[0].userId).isEqualTo(testSetup.USER_ID_4)
        Truth.assertThat(report.triviaQuestionsUsed[0].question).isEqualTo("1+2?")
        Truth.assertThat(report.triviaQuestionsUsed[0].correctAnswer).isEqualTo("3")
        Truth.assertThat(report.triviaQuestionsUsed[0].wrongAnswer).isEqualTo("0")
        Truth.assertThat(report.triviaQuestionsUsed[0].tags[0]).isEqualTo("graficos")
        Truth.assertThat(report.triviaQuestionsUsed[0].tags[1]).isEqualTo("algebra")
    }


    @Test
    fun insert_nullChallenged_CanBeRetrieved(){
        val report = data.createChallenge(ChallengeToInsert(
                testSetup.USER_ID_3,
                null,
                listOf(
                        "oaai",
                        "aaabriu",
                        "taaestando",
                        "4aa"
                ),
                listOf(
                        "oaai2",
                        "abaariu2",
                        "teaastando2",
                        "2aa"
                ),
                6,
                3,
                listOf(
                        TriviaQuestion(
                                testSetup.QUESTION_TRIVIA_ID_1,
                                testSetup.USER_ID_4,
                                "1+2?",
                                "3",
                                "0",
                                listOf(
                                        "graficos",
                                        "algebra"
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                true
        ))


        Truth.assertThat(report.challenger).isEqualTo(testSetup.USER_ID_3)
        Truth.assertThat(report.challenged).isEqualTo(null)
        Truth.assertThat(report.answersChallenger[0]).isEqualTo("oaai")
        Truth.assertThat(report.answersChallenger[1]).isEqualTo("aaabriu")
        Truth.assertThat(report.answersChallenger[2]).isEqualTo("taaestando")
        Truth.assertThat(report.answersChallenger[3]).isEqualTo("4aa")
        Truth.assertThat(report.answersChallenged[0]).isEqualTo("oaai2")
        Truth.assertThat(report.answersChallenged[1]).isEqualTo("abaariu2")
        Truth.assertThat(report.answersChallenged[2]).isEqualTo("teaastando2")
        Truth.assertThat(report.answersChallenged[3]).isEqualTo("2aa")
        Truth.assertThat(report.scoreChallenger).isEqualTo(6)
        Truth.assertThat(report.scoreChallenged).isEqualTo(3)
        Truth.assertThat(report.triviaQuestionsUsed[0].id).isEqualTo(testSetup.QUESTION_TRIVIA_ID_1)
        Truth.assertThat(report.triviaQuestionsUsed[0].userId).isEqualTo(testSetup.USER_ID_4)
        Truth.assertThat(report.triviaQuestionsUsed[0].question).isEqualTo("1+2?")
        Truth.assertThat(report.triviaQuestionsUsed[0].correctAnswer).isEqualTo("3")
        Truth.assertThat(report.triviaQuestionsUsed[0].wrongAnswer).isEqualTo("0")
        Truth.assertThat(report.triviaQuestionsUsed[0].tags[0]).isEqualTo("graficos")
        Truth.assertThat(report.triviaQuestionsUsed[0].tags[1]).isEqualTo("algebra")
    }

    @Test
    fun retrieve_validChallenge_returnChallenge(){
        val resultRetrieved = data.matchMaking("39c54d325b75357a571d4cc2")
        Truth.assertThat(resultRetrieved?.id).isEqualTo("09c54d325b75357a571d4cc1")
        Truth.assertThat(resultRetrieved?.challenger).isEqualTo("37235b2a67c76abebce3f6e6")
        Truth.assertThat(resultRetrieved?.challenged).isEqualTo("39c54d325b75357a571d4cc2")
        Truth.assertThat(resultRetrieved?.answersChallenger?.get(0)).isEqualTo("oi")
        Truth.assertThat(resultRetrieved?.answersChallenger?.get(1)).isEqualTo("abriu")
        Truth.assertThat(resultRetrieved?.answersChallenger?.get(2)).isEqualTo("testando")
        Truth.assertThat(resultRetrieved?.answersChallenger?.get(3)).isEqualTo("4")
        Truth.assertThat(resultRetrieved?.scoreChallenger).isEqualTo(10)
        Truth.assertThat(resultRetrieved?.scoreChallenged).isEqualTo(9)
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.id).isEqualTo("0a1449a4bdb40abd5ae1e411")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.userId).isEqualTo(testSetup.USER_ID_3)
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.question).isEqualTo("1+1?")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.correctAnswer).isEqualTo("2")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.wrongAnswer).isEqualTo("1")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.tags?.get(0)).isEqualTo("matematica")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.tags?.get(1)).isEqualTo("algebra")
        Truth.assertThat(resultRetrieved?.triviaQuestionsUsed?.get(0)?.comments?.get(0)?.comment).isEqualTo("essa questao e boa")
    }

    @Test
    fun retrieve_invalidChallenge_returnChallenge(){
        testSetup.challengeRepo.deleteById("09c54d325b75357a571d4cc1")
        testSetup.challengeRepo.deleteById("09c54d325b75357a571d4cd2")

        val resultRetrieved = data.matchMaking("0a1449a4bdb40abd5ae1e333")
        Truth.assertThat(resultRetrieved).isNull()
    }

    @Test
    fun findById_validChallenge_returnChallenge(){
        val resultRetrieved = data.findById("09c54d325b75357a571d4cb2")
        Truth.assertThat(resultRetrieved.id).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_2)
    }

    @Test
    fun findById_invalidChallenge_returnError(){
        assertThrows<ItemNotFoundException> {
            data.findById("09c54d325b75357a531d4cb2")
        }
    }

    @Test
    fun updateChallenge_challengerChange_return(){
        val answers = listOf(
                "oi-trocado","alo-trocado","aahah-trocado","its me-trocado"
        )
        data.updateAnswers("09c54d325b75357a571d4cb2",true,answers,8)
        val resultRetrieved = data.findById("09c54d325b75357a571d4cb2")
        Truth.assertThat(resultRetrieved.answersChallenger[0]).isEqualTo("oi-trocado")
        Truth.assertThat(resultRetrieved.answersChallenger[1]).isEqualTo("alo-trocado")
        Truth.assertThat(resultRetrieved.answersChallenger[2]).isEqualTo("aahah-trocado")
        Truth.assertThat(resultRetrieved.answersChallenger[3]).isEqualTo("its me-trocado")
        Truth.assertThat(resultRetrieved.scoreChallenger).isEqualTo(8)
    }

    @Test
    fun updateChallenge_challengedChange_return(){
        val answers = listOf(
                "oi-trocadod","alo-trocadod","aahah-trocadod","its me-trocadod"
        )
        data.updateAnswers("09c54d325b75357a571d4cb2",false,answers,0)
        val resultRetrieved = data.findById("09c54d325b75357a571d4cb2")
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("oi-trocadod")
        Truth.assertThat(resultRetrieved.answersChallenged[1]).isEqualTo("alo-trocadod")
        Truth.assertThat(resultRetrieved.answersChallenged[2]).isEqualTo("aahah-trocadod")
        Truth.assertThat(resultRetrieved.answersChallenged[3]).isEqualTo("its me-trocadod")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(0)
    }

    @Test
    fun directChallenge_validChallenge_returnChallenge(){
        val result = data.verifyDirectChallenges(testSetup.USER_ID_2)
        Truth.assertThat(result?.id).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_7)
        Truth.assertThat(result?.challenger).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(result?.answersChallenger?.size).isEqualTo(1)
        Truth.assertThat(result?.scoreChallenger).isEqualTo(0)
        Truth.assertThat(result?.challenged).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(result?.answersChallenged).isEmpty()
        Truth.assertThat(result?.downloadChallenger).isTrue()
        Truth.assertThat(result?.downloadChallenged).isTrue()
    }

    @Test
    fun findDirectChallengesById_validChallenge_returnChallenge(){
        val result = data.findDirectChallengesById(testSetup.CHALLENGE_TRIVIA_ID_2)
        Truth.assertThat(result?.challenger).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(result?.challenged).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(result?.downloadChallenger).isTrue()
        Truth.assertThat(result?.downloadChallenged).isTrue()
    }


}