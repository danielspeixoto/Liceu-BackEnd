package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.data.FacebookAPI
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL
import javax.imageio.ImageIO
import javax.naming.AuthenticationException

class TestFacebookAPIIntegration {

    val facebook = FacebookAPI()
    @Autowired
    lateinit var testSetup: DataSetup


    @Ignore
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