package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.UserRepository
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.user.UserForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoUserRepositoryIntegration {

    @Autowired
    lateinit var data: MongoUserRepository
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun save_UserDoesNotExists_CreatesNew() {
        data.save(UserForm(
                "newuser",
                "newuser@gmail.com",
                Picture(
                        "https://newuser.pic",
                        200, 200
                ),
                "id",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ))
        val user = userRepository.findByEmail("newuser@gmail.com")
        println(user)
        assertThat(user).isNotNull()
        assertThat(user.name).isEqualTo("newuser")
    }

    @Test
    fun save_UserExists_Updates() {
        val countBefore = userRepository.count()
        val id = data.save(UserForm(
                "updatedName",
                "user1@g.com",
                Picture(
                        "https://newuser.pic",
                        200, 200
                ),
                "oldId",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ))
        val user = userRepository.findByEmail("user1@g.com")
        assertThat(id).isEqualTo(testSetup.USER_ID_1)
        assertThat(userRepository.count()).isEqualTo(countBefore)
        assertThat(user).isNotNull()
        assertThat(user.name).isEqualTo("updatedName")
        assertThat(user.facebookId).isEqualTo("oldId")
    }

    @Test
    fun userId_UserExists_returnUser(){
        val result = data.getUserById(testSetup.USER_ID_1)
        assertThat(result.name).isEqualTo("user1")
        assertThat(result.email).isEqualTo("user1@g.com")
        assertThat(result.picture.url).isEqualTo("https://picture1.jpg")
        assertThat(result.picture.width).isEqualTo(200)
        assertThat(result.picture.height).isEqualTo(200)

        val result4 = data.getUserById(testSetup.USER_ID_4)
        assertThat(result4.name).isEqualTo("user4")
        assertThat(result4.email).isEqualTo("user4@g.com")
        assertThat(result4.picture.url).isEqualTo("https://picture4.jpg")
        assertThat(result4.picture.width).isEqualTo(200)
        assertThat(result4.picture.height).isEqualTo(200)
    }

    @Test
    fun userId_UserDoesNotExists_returnError(){
        assertThrows<ItemNotFoundException> {
            data.getUserById("88235b2a67c76abebce3f6e3")
        }
    }


    @Test
    fun challengesFromUser_challengeExists_returnChallenges(){
        val result = data.getChallengesFromUserById(testSetup.USER_ID_1)
        val ids = result.map { it.id }
        assertThat(ids).containsExactly(testSetup.CHALLENGE_TRIVIA_ID_4, testSetup.CHALLENGE_TRIVIA_ID_2).inOrder()
        assertThat(result[0].challenger).isEqualTo(testSetup.USER_ID_1)
        assertThat(result[1].challenged).isEqualTo(testSetup.USER_ID_1)
    }

    @Test
    fun challengesFromUser_challengeDontExists_returnError(){
        val result = data.getChallengesFromUserById(testSetup.USER_ID_4)
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun challengesFromUser_challengerWithNoAnswers_returnNull(){
        val result = data.getChallengesFromUserById(testSetup.USER_ID_5)
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun updateLocation_userExist_returnUserWithLocation(){
        val result = data.updateLocationFromUser(testSetup.USER_ID_1,71.233,67.898,"RJ")
        assertThat(result).isEqualTo(1)
        val getUser2 = data.getUserById(testSetup.USER_ID_1)
        assertThat(getUser2.location).isNotNull()
        assertThat(getUser2.location?.x).isEqualTo(71.233)
        assertThat(getUser2.location?.y).isEqualTo(67.898)
        assertThat(getUser2.state).isEqualTo("RJ")
    }

    @Test
    fun updateSchool_userExist_returnUserWithSchool(){
        val result = data.updateSchoolFromUser(testSetup.USER_ID_1,"Imt")
        assertThat(result).isEqualTo(1)
        val getUser = data.getUserById(testSetup.USER_ID_1)
        assertThat(getUser.school).isEqualTo("Imt")
    }

    @Test
    fun updateAge_userExist_returnUserWithAge(){
        val result = data.updateAgeFromUser(testSetup.USER_ID_1,30)
        assertThat(result).isEqualTo(1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.age).isEqualTo(30)
    }

    @Test
    fun updateYoutubeChannel_userExist_returnUserWithYoutubeChannel(){
        val result = data.updateYoutubeChannelFromUser(testSetup.USER_ID_1,"www.youtube.com/liceu.co")
        assertThat(result).isEqualTo(1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.youtubeChannel).isEqualTo("www.youtube.com/liceu.co")
    }

    @Test
    fun updateInstagramProfile_userExist_returnUserWithInstagramProfile(){
        val result = data.updateInstagramProfileFromUser(testSetup.USER_ID_1,"@liceu.co")
        assertThat(result).isEqualTo(1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.instagramProfile).isEqualTo("@liceu.co")
    }

    @Test
    fun updateDescription_userExist_returnUserWithDescription(){
        val result = data.updateDescriptionFromUser(testSetup.USER_ID_1,"Eu sou o liceu, prazer")
        assertThat(result).isEqualTo(1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.description).isEqualTo("Eu sou o liceu, prazer")
    }

    @Test
    fun updateWebsite_userExist_returnUserWithWebsite(){
        val result = data.updateDescriptionFromUser(testSetup.USER_ID_1,"Eu sou o liceu, prazer")
        assertThat(result).isEqualTo(1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.description).isEqualTo("Eu sou o liceu, prazer")
    }

    @Test
    fun getUsersByNameWithoutLocation_usersExist_returnListOfUsers(){
        val result = data.getUsersByNameUsingLocation("user",null,null,15)
        assertThat(result.size).isEqualTo(3)
        val ids = result.map { it.id }
        assertThat(ids).containsExactly(testSetup.USER_ID_1,testSetup.USER_ID_2,testSetup.USER_ID_4)
    }

    @Test
    fun getUsersByNameUsingLocation_usersExist_returnListOfUsers(){
        val result = data.getUsersByNameUsingLocation("user",-90.83,-42.86,15)
        assertThat(result.size).isEqualTo(3)
        val ids = result.map { it.id }
        assertThat(ids).containsExactly(testSetup.USER_ID_1,testSetup.USER_ID_2,testSetup.USER_ID_4)
    }

    @Test
    fun getUsersByNameUsingLocation_userExist_returnListOfUsers(){
        val result = data.getUsersByNameUsingLocation("man i",-90.83,-42.86,15)
        assertThat(result.size).isEqualTo(1)
        val ids = result.map { it.id }
        assertThat(ids).containsExactly(testSetup.USER_ID_3)
    }

    @Test
    fun updateProducerToBeFollowed_userExist_verifyUserAndProducer(){
        val result1 = data.updateAddUserToProducerFollowerList(testSetup.USER_ID_1,testSetup.USER_ID_2)
        assertThat(result1).isEqualTo(1)
        val result2 = data.updateAddProducerToFollowingList(testSetup.USER_ID_1,testSetup.USER_ID_2)
        assertThat(result2).isEqualTo(1)
        val producer = data.getUserById(testSetup.USER_ID_2)
        assertThat(producer.followers?.size).isEqualTo(1)
        assertThat(producer.followers?.get(0)).isEqualTo(testSetup.USER_ID_1)
        val user = data.getUserById(testSetup.USER_ID_1)
        assertThat(user.following?.size).isEqualTo(3)
        assertThat(user.following).containsExactly(testSetup.USER_ID_3,testSetup.USER_ID_4,testSetup.USER_ID_2)
    }

    @Test
    fun updateProducerToBeUnfollowed_userExist_verifyUserAndProducer(){
        val resultDel = data.updateRemoveUserToProducerFollowerList(testSetup.USER_ID_3,testSetup.USER_ID_4)
        assertThat(resultDel).isEqualTo(1)
        val resultDel2 = data.updateRemoveProducerToFollowingList(testSetup.USER_ID_3,testSetup.USER_ID_4)
        assertThat(resultDel2).isEqualTo(1)
        val producerAfter = data.getUserById(testSetup.USER_ID_4)
        assertThat(producerAfter.followers?.size).isEqualTo(0)
        val userAfter = data.getUserById(testSetup.USER_ID_3)
        assertThat(userAfter.following?.size).isEqualTo(1)
    }


    @Test
    fun updateProducerToBeUnfollowed_producerWithNoFollowers_verifyUserAndProducer(){
        val resultDel = data.updateRemoveUserToProducerFollowerList(testSetup.USER_ID_1,testSetup.USER_ID_2)
        assertThat(resultDel).isEqualTo(0)
        val producerBefore = data.getUserById(testSetup.USER_ID_2)
        assertThat(producerBefore.followers?.size).isNull()
        val resultDel2 = data.updateRemoveProducerToFollowingList(testSetup.USER_ID_1,testSetup.USER_ID_2)
        assertThat(resultDel2).isEqualTo(0)
        val userAfter = data.getUserById(testSetup.USER_ID_1)
        assertThat(userAfter.following?.size).isEqualTo(2)
    }

}