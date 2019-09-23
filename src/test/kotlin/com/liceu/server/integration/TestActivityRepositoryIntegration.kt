package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.ActivityRepository
import com.liceu.server.data.MongoActivityRepository
import com.liceu.server.domain.activities.ActivityToInsert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestActivityRepositoryIntegration {

    @Autowired
    lateinit var data: MongoActivityRepository

    @Autowired
    lateinit var repo: ActivityRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insertActivity_Valid_CanBeRetrieved(){
        val id = data.insertActivity(ActivityToInsert(
                testSetup.USER_ID_1,
                "triviaFinished",
                hashMapOf(
                        "challenger" to testSetup.USER_ID_2,
                        "triviaId" to testSetup.CHALLENGE_TRIVIA_ID_1
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        ))
        val activity = repo.findById(id).get()
        assertThat(activity.id.toHexString()).isEqualTo(id)
        assertThat(activity.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        assertThat(activity.type).isEqualTo("triviaFinished")
        assertThat(activity.params.size).isEqualTo(2)
        assertThat(activity.params["challenger"]).isEqualTo(testSetup.USER_ID_2)
    }

    @Test
    fun insertActivity_validVariation_CanBeRetrieved(){
        val id = data.insertActivity(ActivityToInsert(
                testSetup.USER_ID_1,
                "followedSomeone",
                hashMapOf(
                        "followed" to testSetup.USER_ID_2
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        ))
        val activity = repo.findById(id).get()
        assertThat(activity.id.toHexString()).isEqualTo(id)
        assertThat(activity.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        assertThat(activity.type).isEqualTo("followedSomeone")
        assertThat(activity.params.size).isEqualTo(1)
        assertThat(activity.params["followed"]).isEqualTo(testSetup.USER_ID_2)
    }

    @Test
    fun getActivitiesFromUser_validUser_returnListOfActivities() {
        val activitiesRetrieved = data.getActivitiesFromUser(testSetup.USER_ID_1,10, emptyList())
        assertThat(activitiesRetrieved.size).isEqualTo(3)
        assertThat(activitiesRetrieved[0].type).isEqualTo("challengeAccepted")
        assertThat(activitiesRetrieved[1].type).isEqualTo("challengeFinished")
        assertThat(activitiesRetrieved[2].type).isEqualTo("followedUser")
    }

    @Test
    fun getActivitiesFromUser_validUser_returnOnlyOneActivitiy() {
        val activitiesRetrieved = data.getActivitiesFromUser(testSetup.USER_ID_2,10,emptyList())
        assertThat(activitiesRetrieved.size).isEqualTo(1)
        assertThat(activitiesRetrieved[0].type).isEqualTo("followedUser")
    }

    @Test
    fun getActivitiesFromUser_usingTags_returnOnlyOneActivity(){
        val activitiesRetrieved = data.getActivitiesFromUser(testSetup.USER_ID_1,10, listOf("challengeFinished","challengeAccepted"))
        assertThat(activitiesRetrieved.size).isEqualTo(2)
        assertThat(activitiesRetrieved[0].id).isEqualTo(testSetup.ACITIVITY_ID_4)
        assertThat(activitiesRetrieved[1].id).isEqualTo(testSetup.ACITIVITY_ID_2)
    }


}