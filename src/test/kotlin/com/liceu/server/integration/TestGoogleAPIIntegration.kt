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
        val user = googleAPI.data("ya29.GluMB-2DhqOa0C134ww0fVKx3fQaCvomr3yvC7YDHH1m28C6GpJk-jI6zQoSzONJ0MpfXoEyV0DXXuXOLa1Pu_QuDNlAFcyBO2UWyusw09ulIhN1nfd3QdXnbPBR")
        assertThat(user.email).isEqualTo("danielspeixoto@outlook.com")
//        TODO ADD other assertions
    }

    @Disabled
    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<Exception> {
            googleAPI.data("invalid")
        }
    }

}