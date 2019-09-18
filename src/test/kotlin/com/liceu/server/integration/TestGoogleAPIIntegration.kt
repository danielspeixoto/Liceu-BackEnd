package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.GoogleAPI
import org.junit.Ignore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@DataMongoTest
@ActiveProfiles("test")
class TestGoogleAPIIntegration {

    @Autowired
    lateinit var testSetup: DataSetup
    lateinit var googleAPI: GoogleAPI

    @BeforeEach
    fun setup() {
        testSetup.setup()
        googleAPI = GoogleAPI(
                testSetup.googleClientId,
                testSetup.googleClientSecret
        )
    }

    @Disabled
    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        val user = googleAPI.data("4/rQG2bqzEQ0XCjlH-R7T5LpXguws8zSjDP5JCWqi5Ol86KVXR-6dEUBtwWh-NXD3UZwv86nCscSUG7hcdHUEL7x8")
        assertThat(user.email).isEqualTo("ingoalmeida@hotmail.com")
//        TODO ADD other assertions
    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<Exception> {
            googleAPI.data("invalid")
        }
    }

}