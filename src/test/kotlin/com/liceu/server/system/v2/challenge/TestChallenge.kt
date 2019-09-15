package com.liceu.server.system.v2.challenge

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.data.ChallengeRepository
import com.liceu.server.data.MongoActivityRepository
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
    lateinit var activitiesData: MongoActivityRepository

    @Autowired
    override lateinit var testSetup: DataSetup


    @Test
    fun submitChallenge_validChallenge_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "challengedId" to testSetup.USER_ID_2
        ),headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(body["challenged"]).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(body["answersChallenger"] as List<String>).isEmpty()
        Truth.assertThat(body["answersChallenged"] as List<String>).isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isNull()
        Truth.assertThat(body["scoreChallenger"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)[0]
        Truth.assertThat(triviaQuestionsUsed.size).isEqualTo(9)
    }


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
        Truth.assertThat(body["id"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_3)
        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_3)
        Truth.assertThat(body["challenged"]).isEqualTo(testSetup.USER_ID_1)
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
        Truth.assertThat(triviaQuestionsUsed["id"]).isEqualTo(testSetup.QUESTION_TRIVIA_ID_2)
        Truth.assertThat(triviaQuestionsUsed["userId"]).isEqualTo(testSetup.USER_ID_3)
        Truth.assertThat(triviaQuestionsUsed["question"]).isEqualTo("1+1?")
        Truth.assertThat(triviaQuestionsUsed["correctAnswer"]).isEqualTo("2")
        Truth.assertThat(triviaQuestionsUsed["wrongAnswer"]).isEqualTo("1")
        val tags = triviaQuestionsUsed["tags"] as List<String>
        Truth.assertThat(tags[0]).isEqualTo("matematica")
        Truth.assertThat(tags[1]).isEqualTo("algebra")
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_3,10)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(1)
        Truth.assertThat(activitiesChallenger[0].type).isEqualTo("challengeAccepted")
        Truth.assertThat(activitiesChallenger[0].params["challengedId"]).isEqualTo(testSetup.USER_ID_1)

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

        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_1)
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
        testSetup.challengeRepo.deleteById(testSetup.CHALLENGE_TRIVIA_ID_3)

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(body["challenged"]).isEqualTo(null)
        val answersChallenger = (body["answersChallenger"] as List<String>)
        Truth.assertThat(answersChallenger).isEmpty()
        val answersChallenged = (body["answersChallenged"] as List<String>)
        Truth.assertThat(answersChallenged).isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isNull()
        Truth.assertThat(body["scoreChallenged"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)
        Truth.assertThat(triviaQuestionsUsed).hasSize(5)
    }

    @Test
    fun getChallenge_directChallenge_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_3_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_8)
        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(body["challenged"]).isEqualTo(testSetup.USER_ID_3)
        val answersChallenger = (body["answersChallenger"] as List<String>)
        Truth.assertThat(answersChallenger.size).isEqualTo(1)
        val answersChallenged = (body["answersChallenged"] as List<String>)
        Truth.assertThat(answersChallenged).isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isEqualTo(0)
        Truth.assertThat(body["scoreChallenged"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)
        Truth.assertThat(triviaQuestionsUsed).hasSize(1)
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
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_2))
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.answersChallenged[1]).isEqualTo("4")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(2)
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10)
        val activitiesChallenged = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(2)
        Truth.assertThat(activitiesChallenger[0].type).isEqualTo("challengeFinished")
        Truth.assertThat(activitiesChallenged.size).isEqualTo(3)
        Truth.assertThat(activitiesChallenged[0].type).isEqualTo("challengeFinished")
    }

    @Test
    fun updateChallenge_challengerQuestions_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "answers" to listOf(
                                "3",
                                "4",
                                "5"
                        )
                )
                ,headers)

        val response = restTemplate.exchange<Void>(baseUrl+"/"+testSetup.CHALLENGE_TRIVIA_ID_6, HttpMethod.PUT,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_6).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_6))
        Truth.assertThat(resultRetrieved.answersChallenger[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.answersChallenger[1]).isEqualTo("4")
        Truth.assertThat(resultRetrieved.answersChallenger[2]).isEqualTo("5")
        Truth.assertThat(resultRetrieved.scoreChallenger).isEqualTo(3)
        Truth.assertThat(resultRetrieved.answersChallenged).isEmpty()
        Truth.assertThat(resultRetrieved.scoreChallenged).isNull()
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(2)
    }

    @Test
    fun updateChallenge_ChallengedQuestions_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "answers" to listOf(
                                "3"
                        )
                )
                ,headers)

        val response = restTemplate.exchange<Void>(baseUrl+"/"+testSetup.CHALLENGE_TRIVIA_ID_7, HttpMethod.PUT,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_7).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_7))
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(1)
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10)
        val activitiesChallenged = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(3)
        Truth.assertThat(activitiesChallenger[0].type).isEqualTo("challengeFinished")
        var paramsFromActivity = activitiesChallenger[0].params
        Truth.assertThat(paramsFromActivity["challengeId"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_7)
        Truth.assertThat(paramsFromActivity["challengedId"]).isEqualTo(testSetup.USER_ID_2)
        paramsFromActivity = activitiesChallenged[0].params
        Truth.assertThat(activitiesChallenged.size).isEqualTo(2)
        Truth.assertThat(activitiesChallenged[0].type).isEqualTo("challengeFinished")
        Truth.assertThat(paramsFromActivity["challengeId"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_7)
        Truth.assertThat(paramsFromActivity["challengerId"]).isEqualTo(testSetup.USER_ID_1)
    }

    @Test
    fun submitChallenge_challengedIdToNull_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }



}