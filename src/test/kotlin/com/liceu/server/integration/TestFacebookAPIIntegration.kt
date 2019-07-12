package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.FacebookAPI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL
import javax.imageio.ImageIO
import javax.naming.AuthenticationException

class TestFacebookAPIIntegration {

    val facebook = FacebookAPI()

    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        val user = facebook.data("EAAf4pgUyFpsBAPWr0WehvKnwgAA1LpZB1o727vaNxRVxq3PIhZC27zFJ5vdpi7fhnMOiGnRnYVyHSmlTwfDm3vGW1rBjddFHM1IBziyVsaS79zIE9WVVmimTNcaUlUjknJAM6TKZB4wrGYwZATadp8vk4r2tHNp2XSZCuNuT7H9dJvt8ZBayOhc3P7IvhYfZCuwg30j3xBVQwZDZD")
        assertThat(user.name).isEqualTo("Open Graph Test User")
        assertThat(user.email).isEqualTo("open_imoeavk_user@tfbnw.net")
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