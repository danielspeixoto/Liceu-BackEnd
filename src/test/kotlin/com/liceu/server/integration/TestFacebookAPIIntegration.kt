package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.FacebookAPI
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL
import javax.imageio.ImageIO
import javax.naming.AuthenticationException

class TestFacebookAPIIntegration {

    val facebook = FacebookAPI()

//    @Ignore
//    @Test
//    fun data_ValidAccessToken_ReturnsInfo() {
//        val user = facebook.data("EAAf4pgUyFpsBABAUrpBFqqG0ILaWb8iDdZCpxdhgZCIZBn5b3pBusuXYcxjSWbtNJq1dj6fRLMY8df3ZC6HBO3655RxjWWzUphZAGJhD8UuNzeo3HZA6lKrglJvQ7eF0pFIou1lOXGAqPAnTyctKv8DvD0k13wUTCk63tDui5r7J2yuOa3DioDgCCngrJKJvcQowZAMhTxtEwZDZD")
//        assertThat(user.name).isEqualTo("Jôãô Míqûíaês")
//        assertThat(user.email).isEqualTo("joao_yypgieh_miquiaes@tfbnw.net")
//        assertThat(ImageIO.read(URL(user.picture.url))).isNotNull()
//        assertThat(user.picture.width).isEqualTo(200)
//        assertThat(user.picture.height).isEqualTo(200)
//    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<AuthenticationException> {
            facebook.data("invalid")
        }
    }
}