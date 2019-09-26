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
                .exchange<List<HashMap<String, Any>>>(baseUrl + "/${testSetup.USER_ID_2}/challenge", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        Truth.assertThat(body[0]["id"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_1)
        Truth.assertThat(body[1]["id"]).isEqualTo(testSetup.CHALLENGE_TRIVIA_ID_2)
    }

    @Test
    fun challengeFromUser_userDontExists_returnEmptyBody() {
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
    fun updateSchool_userExists_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to " CURSO MARístá PatãmarEs "
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.school).isEqualTo("MARISTA")
    }

    @Test
    fun updateSchool_userExistsAndSchoolNameWithoutPrefix_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to " ÚFBÃ "
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.school).isEqualTo("UFBA")
    }

    @Test
    fun updateSchool_userExistsAndSchoolNameWithPrefix_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to " COLÉgiO Antôníó Viẽira "
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.school).isEqualTo("VIEIRA")
    }

    @Test
    fun updateAge_userExists_returnVoid(){
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
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/age", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.age).isEqualTo(21)
    }

    @Test
    fun updateYoutubeChannel_userExists_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to "www.youtube.com/liceu"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.youtubeChannel).isEqualTo("www.youtube.com/liceu")
    }

    @Test
    fun updateInstagramProfile_userExists_returnVoid(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "instagramProfile" to "lIceu.CO"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/instagram", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.instagramProfile).isEqualTo("liceu.co")
    }

    @Test
    fun updateInstagramProfile_specialInstagramProfileWithAt_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "instagramProfile" to "  @liCEu.co  "
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/instagram", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.instagramProfile).isEqualTo("liceu.co")
    }

    @Test
    fun updateDescription_userExists_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "description" to "O liceu e muito legal po"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/description", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.description).isEqualTo("O liceu e muito legal po")
    }

    @Test
    fun updateWebsite_userExists_returnVoid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "website" to "www.liceu.co.com.br"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/website", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val userUpdated = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userUpdated.website).isEqualTo("www.liceu.co.com.br")
    }

    @Test
    fun updateProducerToBeFollowed_usersExists_returnVoidAndVerifyUserAndProducer(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/followers", HttpMethod.PUT, entity)
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val producer =  data.getUserById(testSetup.USER_ID_2)
        val user = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(producer.followers?.size).isEqualTo(1)
        Truth.assertThat(producer.followers?.get(0)).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(user.following?.size).isEqualTo(3)
        Truth.assertThat(user.following).contains(testSetup.USER_ID_2)
        val activitiesProducer = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,10, emptyList())
        Truth.assertThat(activitiesProducer.size).isEqualTo(2)
        Truth.assertThat(activitiesProducer[0].type).isEqualTo("followedUser")
    }

    @Test
    fun updateProducerToBeUnfollowed_usersExists_returnVoidAndVerifyUserAndProducer(){
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
    fun updateProducerToBeFollowed_producerIdInvalid_throwsInternalServerError(){
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
    fun updateProducerToBeFollowed_producerDoesNotExists_throwsNotFound(){
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
    fun updateSchool_mismatchVariable_throwsBadRequest(){
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
    fun updateAge_ageToString_throwsBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "age" to "1"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/age", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }


    @Test
    fun updateYoutubeChannel_youtubeChannelToIng_throwsBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to 123123
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateInstagramProfile_instagramProfileToInt_throwsBadRequest(){
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
    fun updateDescription_descriptionToInt_throwsBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "description" to 123123
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/description", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateWebsite_websiteToInt_throwsBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "website" to 123123.2332
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/website", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateYoutubeChannel_overFlowYoutubeChannel_throwsBadRequest(){
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
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_1}/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProducerToBeFollowed_wrongProducerId_throwsInternalServerError(){
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
    fun updateProducerToBeUnfollowed_wrongProducerId_throwsInternalServerError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl/39c54d325b7571d4cc2/followers", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateYoutubeChannel_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "youtubeChannel" to "www.youtube.com/testeLiceu"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/youtube", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateInstagramProfile_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "instagramProfile" to "www.youtube.com/testeLiceu"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/instagram", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateAge_wrongUserProfileOwner_throwsUnauthorized(){
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
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/age", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateSchool_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "school" to "MArte Nassa"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/school", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateLocation_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "longitude" to -10.23123,
                        "latitute" to 123.12313
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/locale", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateDescription_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "description" to "sou legal"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/description", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateWebsite_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "website" to "www.sou legal.com"
                ), headers)
        val response = restTemplate
                .exchange<Void>(baseUrl + "/${testSetup.USER_ID_2}/website", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }



}