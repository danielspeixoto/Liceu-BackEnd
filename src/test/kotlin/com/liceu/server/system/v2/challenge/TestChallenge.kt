package com.liceu.server.system.v2.challenge

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.data.ChallengeRepository
import com.liceu.server.system.TestSystem
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap


class TestChallenge: TestSystem ("/v2/challenge") {

    @Autowired
    lateinit var challengeRepo: ChallengeRepository


    @Autowired
    override lateinit var testSetup: DataSetup

    @Test
    fun getChallenge_exists_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo("09c54d325b75357a571d4cc1")
        Truth.assertThat(body["challenger"]).isEqualTo("37235b2a67c76abebce3f6e6")
        Truth.assertThat(body["challenged"]).isEqualTo("3a1449a4bdb40abd5ae1e431")
        val answersChallenger = (body["answersChallenger"] as List<String>)
        Truth.assertThat(answersChallenger[0]).isEqualTo("oi")
        Truth.assertThat(answersChallenger[1]).isEqualTo("abriu")
        Truth.assertThat(answersChallenger[2]).isEqualTo("testando")
        Truth.assertThat(answersChallenger[3]).isEqualTo("4")
        val answersChallenged = (body["answersChallenged"] as List<String>)
        answersChallenged.isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isEqualTo(10)
        Truth.assertThat(body["scoreChallenged"]).isEqualTo(9)
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)[0]
        Truth.assertThat(triviaQuestionsUsed["id"]).isEqualTo("0a1449a4bdb40abd5ae1e411")
        Truth.assertThat(triviaQuestionsUsed["userId"]).isEqualTo("37235b2a67c76abebce3f6e6")
        Truth.assertThat(triviaQuestionsUsed["question"]).isEqualTo("1+1?")
        Truth.assertThat(triviaQuestionsUsed["correctAnswer"]).isEqualTo("2")
        Truth.assertThat(triviaQuestionsUsed["wrongAnswer"]).isEqualTo("1")
        val tags = triviaQuestionsUsed["tags"] as List<String>
        Truth.assertThat(tags[0]).isEqualTo("matematica")
        Truth.assertThat(tags[1]).isEqualTo("algebra")
    }

    @Test
    fun getChallenge_notExists_returnChallenge(){
        testSetup.challengeRepo.deleteAll()

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["challenger"]).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(body["challenged"]).isEqualTo(null)
        val answersChallenger = (body["answersChallenger"] as List<String>)
        answersChallenger.isEmpty()
        val answersChallenged = (body["answersChallenged"] as List<String>)
        answersChallenged.isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isNull()
        Truth.assertThat(body["scoreChallenged"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)
        Truth.assertThat(triviaQuestionsUsed).hasSize(5)
    }

    @Test
    fun getChallenge_existsSameUser_returnChallenge(){
        testSetup.challengeRepo.deleteById("09c54d325b75357a571d4cc1")

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["challenger"]).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(body["challenged"]).isEqualTo(null)
        val answersChallenger = (body["answersChallenger"] as List<String>)
        answersChallenger.isEmpty()
        val answersChallenged = (body["answersChallenged"] as List<String>)
        answersChallenged.isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isNull()
        Truth.assertThat(body["scoreChallenged"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)
        Truth.assertThat(triviaQuestionsUsed).hasSize(5)
    }


    @Test
    fun updateChallenge_existsChallenge_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "answers" to listOf(
                                "3",
                                "4"
                        )
                )
                ,headers)

        val response = restTemplate
                .exchange<Void>(baseUrl+"/"+testSetup.CHALLENGE_TRIVIA_ID_2, HttpMethod.PUT,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)



        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_2).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId("09c54d325b75357a571d4cb2"))
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.answersChallenged[1]).isEqualTo("4")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(2)
    }




}