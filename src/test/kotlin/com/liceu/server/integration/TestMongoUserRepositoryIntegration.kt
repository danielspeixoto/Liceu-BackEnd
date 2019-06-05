package com.liceu.server.integration

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.DataSetup.Companion.USER_ID_1
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.UserRepository
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.user.UserForm
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
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
                "id"
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
                "oldId"
        ))
        val user = userRepository.findByEmail("user1@g.com")
        assertThat(id).isEqualTo(USER_ID_1)
        assertThat(userRepository.count()).isEqualTo(countBefore)
        assertThat(user).isNotNull()
        assertThat(user.name).isEqualTo("updatedName")
        assertThat(user.facebookId).isEqualTo("oldId")
    }
}