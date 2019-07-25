package com.liceu.server.system.v2.challenge

import com.google.common.truth.Truth
import com.liceu.server.data.ChallengeRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap


class TestChallenge: TestSystem ("v2/challenge") {

    @Autowired
    lateinit var challengeRepo: ChallengeRepository

    @Test
    fun getChallenge_exists_returnChallenge(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>(baseUrl, HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body[0]["id"]).isEqualTo("09c54d325b75357a571d4ca2")
        Truth.assertThat(body[0]["challenger"]).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(body[0]["challenged"]).isEqualTo("37235b2a67c76abebce3f6e3")
        //Truth.assertThat(body[0]["answersChallenger"]).isEqualTo("")

    }


}