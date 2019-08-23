package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.GoogleAPI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class TestGoogleAPIIntegration {

    val googleAPI = GoogleAPI(
            "851853196382-6dr3s1jb1ee3ejt7i4iselu4vej3rmbq.apps.googleusercontent.com",
            "sm2sA37l2SshglMRa7PTAYz1"
    )

//    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        val user = googleAPI.data("4/nQG18-KawrfR53XRO2YT6XCvPoMgv4bhtNafSiEidCcP0e5B83eqGU0AQbe7W90TisMOYNV-f7WLa_nxKD2dPu4")
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