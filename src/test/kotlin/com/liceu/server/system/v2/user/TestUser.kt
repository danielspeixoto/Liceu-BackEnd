package com.liceu.server.system.v2.user

import com.google.common.truth.Truth
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.UserRepository
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun userID_exists_returnUser(){
        
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl + "/39c54d325b75357a571d4cc2", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["id"]).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(body["name"]).isEqualTo("user2")
        Truth.assertThat(body["email"]).isEqualTo("user2@g.com")
        val userRetrieved = (body["picture"] as HashMap<String, Any>)
        Truth.assertThat(userRetrieved["url"]).isEqualTo("https://picture2.jpg")
        Truth.assertThat(userRetrieved["width"]).isEqualTo(200)
        Truth.assertThat(userRetrieved["height"]).isEqualTo(200)


    }

    @Test
    fun userID_notExits_returnError(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl + "/88235b2a67c76abebce3f6e3", HttpMethod.GET,entity)


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
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val producer =  data.getUserById("39c54d325b75357a571d4cc2")
        val user = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(producer.followers?.size).isEqualTo(1)
        Truth.assertThat(producer.followers?.get(0)).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(user.following?.size).isEqualTo(3)
        Truth.assertThat(user.following).contains("39c54d325b75357a571d4cc2")
    }

    @Test
    fun updateProducerToBeUnfollowed_usersExists_verifyUserAndProducer(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        var producer =  data.getUserById("39c54d325b75357a571d4cc2")
        var user = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(producer.followers?.size).isEqualTo(1)
        Truth.assertThat(producer.followers?.get(0)).isEqualTo("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(user.following?.size).isEqualTo(3)
        Truth.assertThat(user.following).contains("39c54d325b75357a571d4cc2")

        val responseDelete = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(responseDelete.statusCode).isEqualTo(HttpStatus.OK)
        producer = data.getUserById("39c54d325b75357a571d4cc2")
        user = data.getUserById("3a1449a4bdb40abd5ae1e431")
        Truth.assertThat(producer.followers?.size).isEqualTo(0)
        Truth.assertThat(user.following?.size).isEqualTo(2)
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
                .exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/instagram", HttpMethod.PUT, entity)
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
    fun updateProducerToBeFollowed_userIdAsInt_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to 1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_producerIdAsInt_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/1123123/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateProducerToBeFollowed_noUserId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_userIdToEmptyString_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                "userId" to ""
                , headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_userIdToNull_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                "userId" to null
                , headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_wrongProducerId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b753a571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateProducerToBeUnfollowed_userIdAsInt_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to 1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeUnfollowed_producerIdAsInt_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/1123123/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @Test
    fun updateProducerToBeUnfollowed_noUserId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeUnfollowed_userIdToEmptyString_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                "userId" to ""
                , headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeUnfollowed_userIdToNull_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                "userId" to null
                , headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b75357a571d4cc2/followers", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeUnfollowed_wrongProducerId_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "userId" to testSetup.USER_ID_1
                ), headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b7571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}