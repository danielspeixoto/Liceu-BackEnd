package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.FacebookAPI
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URL
import javax.imageio.ImageIO
import javax.naming.AuthenticationException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@DataMongoTest
@ActiveProfiles("test")
class TestFacebookAPIIntegration {

    val facebook = FacebookAPI()
    @Autowired
    lateinit var testSetup: DataSetup

    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        val user = facebook.data(testSetup.facebookAccessToken)
        assertThat(user.name).isEqualTo("Jôãô Míqûíaês")
        assertThat(user.email).isEqualTo("joao_yypgieh_miquiaes@tfbnw.net")
        assertThat(ImageIO.read(URL(user.picture.url))).isNotNull()
        assertThat(user.picture.width).isEqualTo(200)
        assertThat(user.picture.height).isEqualTo(200)
    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<AuthenticationException> {
            facebook.data("invalid")
        }
    }
}