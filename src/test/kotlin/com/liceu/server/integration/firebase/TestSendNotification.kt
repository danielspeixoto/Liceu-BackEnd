package com.liceu.server.integration.firebase

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.GoogleAPI
import com.liceu.server.data.firebase.FirebaseNotifications
import com.liceu.server.domain.global.EmptyException
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
@DataMongoTest
@ActiveProfiles("test")
class TestSendNotification {

    @Autowired
    lateinit var testSetup: DataSetup
    @Autowired
    lateinit var firebaseNotifications: FirebaseNotifications

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun send_ValidFCMToken_Success() {
        val result = firebaseNotifications.send(testSetup.USER_1_FCM_TOKEN, "title", "body")
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun send_EmptyFCMToken_ThrowsError() {
        assertThrows<EmptyException> {
            val result = firebaseNotifications.send("", "title", "body")
            Truth.assertThat(result).isTrue()
        }
    }

    @Test
    fun send_InvalidFCMToken_False() {
        val result = firebaseNotifications.send("123", "title", "body")
        Truth.assertThat(result).isFalse()
    }
}