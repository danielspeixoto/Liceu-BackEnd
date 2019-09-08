package com.liceu.server.system.v2.user

import com.google.common.truth.Truth
import com.liceu.server.data.MongoActivityRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.UserRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestUser: TestSystem("/v2/user") {

    @Autowired
    lateinit var userRepo : UserRepository

    @Autowired
    lateinit var data: MongoUserRepository

    @Autowired
    lateinit var activitiesData: MongoActivityRepository

    @Test
    fun userID_exists_returnUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>("$baseUrl/${testSetup.USER_ID_3}", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["id"]).isEqualTo(testSetup.USER_ID_3)
        Truth.assertThat(body["name"]).isEqualTo("manitos1")
        Truth.assertThat(body["email"]).isEqualTo("user3@g.com")

        val picture = (body["picture"] as HashMap<String, Any>)
        Truth.assertThat(picture["url"]).isEqualTo("https://picture3.jpg")
        Truth.assertThat(picture["width"]).isEqualTo(200)
        Truth.assertThat(picture["height"]).isEqualTo(200)

        Truth.assertThat(body["state"]).isEqualTo("BA")
        Truth.assertThat(body["school"]).isEqualTo("MARISTA")
        Truth.assertThat(body["age"]).isEqualTo(18)
        Truth.assertThat(body["youtubeChannel"]).isEqualTo("Jorginho")
        Truth.assertThat(body["instagramProfile"]).isEqualTo("jorge")
        Truth.assertThat(body["description"]).isEqualTo("alguma descrição maneira")
        Truth.assertThat(body["website"]).isEqualTo("www.umsite.com.br")
        Truth.assertThat(body["amountOfFollowers"]).isEqualTo(1)
        Truth.assertThat(body["amountOfFollowing"]).isEqualTo(2)
        Truth.assertThat(body["following"]).isEqualTo(true)

        // Only update this after doing a assertion of a body property
        Truth.assertThat(body.size).isEqualTo(14)
    }

    @Test
    fun userID_notFollowingUser_followingIsFalse(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>("$baseUrl/${testSetup.USER_ID_4}", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        Truth.assertThat(body["following"]).isEqualTo(false)
    }

    @Test
    fun challengeFromUser_Exists_returnChallenges(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>(baseUrl + "/39c54d325b75357a571d4cc2/challenge", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        Truth.assertThat(body[0]["id"]).isEqualTo("09c54d325b75357a571d4ca2")
        Truth.assertThat(body[1]["id"]).isEqualTo("09c54d325b75357a571d4cb2")
    }

    @Test
    fun challengeFromUser_userDontExists_returnError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)

        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>(baseUrl + "/88235b2a67c76abebce3f6e3/challenge", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!

        Truth.assertThat(body.size).isEqualTo(0)
    }

    @Test
    fun updateSchool_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to " CURSO MARístá PatãmarEs "
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.school).isEqualTo("MARISTA")
    }

    @Test
    fun updateAge_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "day" to 1,
                        "month" to 4,
                        "year" to 1998
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/age", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.age).isEqualTo(21)
    }

    @Test
    fun updateYoutubeChannel_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to "www.youtube.com/liceu"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.youtubeChannel).isEqualTo("www.youtube.com/liceu")
    }

    @Test
    fun updateInstagramProfile_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "instagramProfile" to "liceu.co"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/instagram", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.instagramProfile).isEqualTo("liceu.co")
    }

    @Test
    fun updateDescription_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "description" to "O liceu e muito legal po"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/description", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.description).isEqualTo("O liceu e muito legal po")
    }

    @Test
    fun updateWebsite_userExists_noReturn(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "website" to "www.liceu.co.com.br"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/website", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(userUpdated.website).isEqualTo("www.liceu.co.com.br")
    }

    @Test
    fun updateProducerToBeFollowed_usersExists_verifyUserAndProducer(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val producer =  data.getUserById("39c54d325b75357a571d4cc2")
        val user = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(producer.followers?.size).isEqualTo(1)
        Truth.assertThat(producer.followers?.get(0)).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(user.following?.size).isEqualTo(3)
        Truth.assertThat(user.following).contains("39c54d325b75357a571d4cc2")
        val activitiesProducer = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10)
        Truth.assertThat(activitiesProducer.size).isEqualTo(2)
        Truth.assertThat(activitiesProducer[0].type).isEqualTo("followedUser")
    }

    @Test
    fun updateProducerToBeFollowed_producerIdInvalid_throwsError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/undefined/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateProducerToBeFollowed_producerDoesNotExists_throwsError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/aaaa49a4bdb40abd5ae1e431/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }


    @Test
    fun updateProducerToBeUnfollowed_usersExists_verifyUserAndProducer(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val responseDelete = restTemplate
                .exchange<Void>("$baseUrl/${testSetup.USER_ID_3}/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(responseDelete.statusCode).isEqualTo(HttpStatus.OK)
        val producer = data.getUserById(testSetup.USER_ID_3)
        val user = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(producer.followers?.size).isEqualTo(0)
        Truth.assertThat(user.following?.size).isEqualTo(1)
    }

    @Test
    fun updateSchool_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to 1
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateAge_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "age" to "1"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/age", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateYoutubeChannel_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to 123123
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateInstagramProfile_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "instagramProfile" to 123123
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/instagram", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateDescription_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "description" to 123123
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/description", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateWebsite_mismatchVariable_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "website" to 123123.2332
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/website", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateYoutubeChannel_overFlowChannelName_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_wrongProducerId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b753a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateProducerToBeUnfollowed_wrongProducerId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b7571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}