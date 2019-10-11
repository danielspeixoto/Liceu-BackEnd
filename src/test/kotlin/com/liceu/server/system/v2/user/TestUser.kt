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
import java.time.Instant
import java.util.*

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
        Truth.assertThat(body["desiredCourse"]).isEqualTo("Cientista")
        Truth.assertThat(body["telephoneNumber"]).isEqualTo("71923232323")

        // Only update this after doing a assertion of a body property
        Truth.assertThat(body.size).isEqualTo(16)
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
        Truth.assertThat(body.size).isEqualTo(2)
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
    fun updateProfileImage_jpegImage_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(
            hashMapOf(
                "imageData" to "data:image/jpeg;base64,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"
            ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/profileImage", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val user = data.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(user.picture.url).isEqualTo("https://storage.googleapis.com/liceu-general-tests/user1${testSetup.USER_ID_1}.jpeg")
    }

    @Test
    fun updateProfileImage_pngImage_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                    "imageData" to "data:image/png;base64,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"
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/profileImage", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(user.picture.url).isEqualTo("https://storage.googleapis.com/liceu-general-tests/user2${testSetup.USER_ID_2}.png")
    }

    @Test
    fun updateFcmToken_userExists_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                    "fcmToken" to "1239010293n1[092smi[10923n1029b3[019f3n1/////////asdasdasdsa/asd/a/da/s///ca/c/m"
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/cloudMessaging", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(user.fcmToken).isEqualTo("1239010293n1[092smi[10923n1029b3[019f3n1/////////asdasdasdsa/asd/a/da/s///ca/c/m")
    }

    @Test
    fun updateLastAccess_userExists_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/check", HttpMethod.PUT, entity)
        Thread.sleep(5000)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(user.lastAccess).isNotNull()
        val activitiesFromUser = activitiesData.getActivitiesFromUser(testSetup.USER_ID_2,1, listOf("lastAccessRegister"))
        Truth.assertThat(activitiesFromUser.size).isEqualTo(1)
        Truth.assertThat(activitiesFromUser[0].userId).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(activitiesFromUser[0].type).isEqualTo("lastAccessRegister")
    }

    @Test
    fun updateDesiredCourse_userExists_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "desiredCourse" to "fisica"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/course", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(user.desiredCourse).isEqualTo("fisica")
    }

    @Test
    fun updateTelephoneNumber_userExists_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "71988553321"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(user.telephoneNumber).isEqualTo("71988553321")
    }

    @Test
    fun updateTelephoneNumber_userExistsDifferentNumber_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "719-8855-3321"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(user.telephoneNumber).isEqualTo("71988553321")
    }

    @Test
    fun updateTelephoneNumber_differentNumber_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "(71)9-8855-3321"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(user.telephoneNumber).isEqualTo("71988553321")
    }

    @Test
    fun updateTelephoneNumber_differentNumberWithoutDash_returnVoidAndVerifyUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "(71)98855-3321"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(user.telephoneNumber).isEqualTo("71988553321")
    }

    @Test
    fun updateTelephoneNumber_wrongUserProfileOwner_returnUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "fisica"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateTelephoneNumber_wrongNumberFormat_returnInternalServerError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "(71988223344"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateTelephoneNumber_wrongTelephoneNumberFormat_returnInternalServerError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to "71--988223344"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun updateTelephoneNumber_telephoneNumberToEmpty_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateTelephoneNumber_telephoneNumberToNull_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "telephoneNumber" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/telephone", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateDesiredCourse_wrongUserProfileOwner_returnUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "desiredCourse" to "fisica"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/course", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateDesiredCourse_desiredCourseToEmpty_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "desiredCourse" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/course", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateDesiredCourse_desiredCourseToNull_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity( hashMapOf(
                "desiredCourse" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/course", HttpMethod.PUT, entity)
        val user = data.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }


    @Test
    fun updateLastAccess_wrongUserProfileOwner_returnUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/check", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun updateFcmToken_fcmTokenToNull_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                        "fcmToken" to null
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/cloudMessaging", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateFcmToken_emptyFcmToken_returnBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                        "fcmToken" to ""
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/cloudMessaging", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateFcmToken_wrongUserProfileOwner_returnUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                        "fcmToken" to null
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/cloudMessaging", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }



    @Test
    fun updateProfileImage_imageToNull_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                    "imageData" to null
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/profileImage", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProfileImage_emptyImage_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                        "imageData" to ""
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/profileImage", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateProfileImage_wrongUserProfileOwner_throwsUnauthorized(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(
                hashMapOf(
                        "imageData" to ""
                ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/profileImage", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
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