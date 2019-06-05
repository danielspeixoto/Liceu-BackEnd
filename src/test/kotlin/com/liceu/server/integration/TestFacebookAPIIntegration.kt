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
        val user = facebook.data("EAAf4pgUyFpsBACivxMMHar1zH1sfNxYOEO9VYLZCMPvmvJ7ZAyYhw8BfZCl5MR1QA0JhBhoeYB4f455CP1VHtnz2OwXlYYq8W9eQOXtQzsbFDUxwmFE7RlsfqabsXi5cMJ4k6iXdhMDEfXSJEF6Y7KoZBmZCHLw1mrZA0dZAgAm7O1GetwtZBim2IkVHfM2cAqlGwqybeQRZAagZDZD")
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