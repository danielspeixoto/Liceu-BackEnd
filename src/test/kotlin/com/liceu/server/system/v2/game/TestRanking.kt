package com.liceu.server.system.v2.game

import com.google.common.truth.Truth
import com.liceu.server.data.GameRepository
import com.liceu.server.system.TestSystem
import junit.extensions.TestSetup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpEntity
import java.util.*

class TestRanking: TestSystem("/v2/ranking") {

    @Autowired
    lateinit var gameRepo: GameRepository


    @Test
    fun getRanking_Exists_returnTopGames() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<List<HashMap<String,Any>>>(baseUrl + "?year=2019&month=10&amount=5", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body.size).isEqualTo(4)
        Truth.assertThat(body[0]["id"]).isEqualTo(testSetup.GAME_ID_5)
        Truth.assertThat(body[0]["userId"]).isEqualTo(testSetup.USER_ID_4)
//        Truth.assertThat(body[0]["submissionDate"]).isEqualTo("Mon Oct 14 08:20:20 BRT 2019")
        Truth.assertThat(body[0]["timeSpent"]).isEqualTo(1)
        val answerRetrieved = (body[0]["answers"] as List<HashMap<String, Any>>)[0]
        Truth.assertThat(answerRetrieved["questionId"]).isEqualTo(testSetup.QUESTION_ID_1)
        Truth.assertThat(answerRetrieved["correctAnswer"]).isEqualTo(2)
        Truth.assertThat(answerRetrieved["selectedAnswer"]).isEqualTo(2)

        Truth.assertThat(body[1]["id"]).isEqualTo(testSetup.GAME_ID_3)
        Truth.assertThat(body[1]["userId"]).isEqualTo(testSetup.USER_ID_3)
//        Truth.assertThat(body[1]["submissionDate"]).isEqualTo("Sat Oct 12 08:20:20 BRT 2019")
        Truth.assertThat(body[1]["timeSpent"]).isEqualTo(3)
    }


}