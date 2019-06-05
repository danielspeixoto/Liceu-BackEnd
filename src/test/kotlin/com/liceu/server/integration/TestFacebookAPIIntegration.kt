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
        val user = facebook.data("EAAf4pgUyFpsBAHp5FhI36XE50EMOYgYjCNKk0A4YFCfzvmF975NZA0Vdcr7cXJ2jJDOMsf6UQcCd0g1GUsSS9r859HA3E2S9Lzu2DmH9VskmNxKZBZAuW0pb4cgOuid5ZBJnZAZCOuIQUcsPuKM6vQTOSYWmDBkgwVKSSaBzyCJZAhylpEpPVLfPEaRH1MAhXPVFPFCIlmTgem6ScPdmSCn")
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