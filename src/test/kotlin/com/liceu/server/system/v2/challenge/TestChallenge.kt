package com.liceu.server.system.v2.challenge

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.data.ChallengeRepository
import com.liceu.server.data.MongoActivityRepository
import com.liceu.server.system.TestSystem
import org.bson.types.ObjectId
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
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

    @Disabled
    @Test
    fun getChallenge_exists_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Thread.sleep(5000)
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
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_3,10,emptyList(),0)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(1)
        Truth.assertThat(activitiesChallenger[0].type).isEqualTo("challengeAccepted")
        Truth.assertThat(activitiesChallenger[0].params["challengedId"]).isEqualTo(testSetup.USER_ID_1)

    }

    @Disabled
    @Test
    fun getChallenge_notExists_returnChallenge(){
        testSetup.challengeRepo.deleteAll()

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Thread.sleep(5000)
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

    @Disabled
    @Test
    fun getChallenge_existsSameUser_returnChallenge(){
        testSetup.challengeRepo.deleteById(testSetup.CHALLENGE_TRIVIA_ID_3)

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Thread.sleep(5000)
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

    @Disabled
    @Test
    fun getChallenge_directChallenge_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_3_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
        Thread.sleep(5000)

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
    fun getDirectChallenge_challengeExists_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl+"/${testSetup.CHALLENGE_TRIVIA_ID_7}", HttpMethod.GET,entity)
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_7)
        Truth.assertThat(body["challenger"]).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(body["challenged"]).isEqualTo(testSetup.USER_ID_2)
        val answersChallenger = (body["answersChallenger"] as List<String>)
        Truth.assertThat(answersChallenger[0]).isEqualTo("1")
        val answersChallenged = (body["answersChallenged"] as List<String>)
        answersChallenged.isEmpty()
        Truth.assertThat(body["scoreChallenger"]).isEqualTo(0)
        Truth.assertThat(body["scoreChallenged"]).isNull()
        val triviaQuestionsUsed = (body["triviaQuestionsUsed"] as List<HashMap<String, Any>>)[0]
        Truth.assertThat(triviaQuestionsUsed["id"]).isEqualTo(testSetup.QUESTION_TRIVIA_ID_1)
        Truth.assertThat(triviaQuestionsUsed["userId"]).isEqualTo(testSetup.USER_ID_4)
        Truth.assertThat(triviaQuestionsUsed["question"]).isEqualTo("1+2?")
        Truth.assertThat(triviaQuestionsUsed["correctAnswer"]).isEqualTo("3")
        Truth.assertThat(triviaQuestionsUsed["wrongAnswer"]).isEqualTo("0")
        val tags = triviaQuestionsUsed["tags"] as List<String>
        Truth.assertThat(tags[0]).isEqualTo("graficos")
        Truth.assertThat(tags[1]).isEqualTo("algebra")
        val activitiesFromChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10,emptyList(),0)
        Truth.assertThat(activitiesFromChallenger.size).isEqualTo(4)
        Truth.assertThat(activitiesFromChallenger[0].type).isEqualTo("challengeAccepted")
        Truth.assertThat(activitiesFromChallenger[0].params["challengeId"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_7)
        Truth.assertThat(activitiesFromChallenger[0].params["challengedId"]).isEqualTo(testSetup.USER_ID_2)
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
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_2).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_2))
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.answersChallenged[1]).isEqualTo("4")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(2)
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10,emptyList(),0)
        val activitiesChallenged = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10,emptyList(),0)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(2)
        Truth.assertThat(activitiesChallenger[0].type).isEqualTo("challengeFinished")
        Truth.assertThat(activitiesChallenged.size).isEqualTo(4)
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
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_6).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_6))
        Truth.assertThat(resultRetrieved.answersChallenger[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.answersChallenger[1]).isEqualTo("4")
        Truth.assertThat(resultRetrieved.answersChallenger[2]).isEqualTo("5")
        Truth.assertThat(resultRetrieved.scoreChallenger).isEqualTo(3)
        Truth.assertThat(resultRetrieved.answersChallenged).isEmpty()
        Truth.assertThat(resultRetrieved.scoreChallenged).isNull()
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10,emptyList(),0)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(3)
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

        val response = restTemplate.exchange<Void>(baseUrl+"/${testSetup.CHALLENGE_TRIVIA_ID_7}", HttpMethod.PUT,entity)
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val resultRetrieved = challengeRepo.findById(testSetup.CHALLENGE_TRIVIA_ID_7).get()
        Truth.assertThat(resultRetrieved.id).isEqualTo(ObjectId(testSetup.CHALLENGE_TRIVIA_ID_7))
        Truth.assertThat(resultRetrieved.answersChallenged[0]).isEqualTo("3")
        Truth.assertThat(resultRetrieved.scoreChallenged).isEqualTo(1)
        val activitiesChallenger = activitiesData.getActivitiesFromUser(testSetup.USER_ID_1,10,emptyList(),0)
        val activitiesChallenged = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10,emptyList(),0)
        Truth.assertThat(activitiesChallenger.size).isEqualTo(4)
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
    fun getDirectChallenge_multipleCallsForSameChallenge_multipleReturns(){
        val headers = HttpHeaders()
        val responses = arrayListOf<Any>()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        for (i in 1..2) {
            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl + "/${testSetup.CHALLENGE_TRIVIA_ID_7}", HttpMethod.GET, entity)
            responses.add(response)
            Thread.sleep(5000)
        }
        val insertedResponse = responses[0] as ResponseEntity<*>
        Truth.assertThat(insertedResponse.statusCode).isEqualTo(HttpStatus.OK)
        val reinsertedResponse = responses[1] as ResponseEntity<*>
        Truth.assertThat(reinsertedResponse.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }


    @Test
    fun getChallenge_directChallenge_multipleReturns(){
        val responses = arrayListOf<Any>()
        val challengesId = arrayListOf<String>()
        var lastChallengeId: String = ""
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_3_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        for (i in 1..2) {
            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.GET,entity)
            val body = response.body!!
            responses.add(response)
            challengesId.add(body["id"].toString())
            Thread.sleep(5000)
            lastChallengeId = body["id"].toString()
        }
        val insertedResponse = responses[0] as ResponseEntity<*>
        Truth.assertThat(insertedResponse.statusCode).isEqualTo(HttpStatus.OK)
        val reinsertedResponse = responses[1] as ResponseEntity<*>
        Truth.assertThat(reinsertedResponse.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(challengesId.size).isEqualTo(2)
        Truth.assertThat(challengesId[0]).isNotEqualTo(lastChallengeId)
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


    @Test
    fun submitChallenge_equalChallengerAndChallenged_throwInternalServerError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "challengedId" to testSetup.USER_ID_1
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @Test
    fun getDirectChallenge_wrongUserChallenged_throwUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_3_ACCESS_TOKEN
        val entity = HttpEntity(null,headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl+"/${testSetup.CHALLENGE_TRIVIA_ID_7}", HttpMethod.GET,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

}