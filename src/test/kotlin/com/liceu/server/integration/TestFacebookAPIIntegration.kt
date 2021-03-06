package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.FacebookAPI
import com.liceu.server.util.FacebookTestUsers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URL
import javax.imageio.ImageIO
import javax.naming.AuthenticationException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@DataMongoTest
@ActiveProfiles("test")
class TestFacebookAPIIntegration {

    @Autowired
    lateinit var facebookTestUsers: FacebookTestUsers

    val facebook = FacebookAPI()
    @Autowired
    lateinit var testSetup: DataSetup

    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        if (facebookTestUsers.hasAccess) {
            val user = facebook.data(facebookTestUsers.userAccessToken)
            assertThat(user.name).isEqualTo("Sophia Aldajeidicfbi Okelolasky")
            assertThat(user.email).isEqualTo("sulwxcmqrp_1567856463@tfbnw.net")
            assertThat(ImageIO.read(URL(user.picture.url))).isNotNull()
            assertThat(user.picture.width).isEqualTo(200)
            assertThat(user.picture.height).isEqualTo(200)
        }
    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        if (facebookTestUsers.hasAccess) {
            assertThrows<AuthenticationException> {
                facebook.data("invalid")
            }
        }
    }
}