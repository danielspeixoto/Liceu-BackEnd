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
        val user = googleAPI.data("eyJhbGciOiJSUzI1NiIsImtpZCI6IjBiMGJmMTg2NzQzNDcxYTFlZGNhYzMwNjBkMTI1NmY5ZTQwNTBiYTgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI4NTE4NTMxOTYzODItMmFoYTJ0OHBwMXIwcjl0MjRtZ2gzbjBkYW1pdG1hMmwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI4NTE4NTMxOTYzODItNmRyM3MxamIxZWUzZWp0N2k0aXNlbHU0dmVqM3JtYnEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTUwOTEwMzgwOTg2OTkzMDgyNjgiLCJlbWFpbCI6ImRhbmllbHNwZWl4b3RvQG91dGxvb2suY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJEYW5pZWwgUGVpeG90byIsInBpY3R1cmUiOiJodHRwczovL2xoNi5nb29nbGV1c2VyY29udGVudC5jb20vLTFidlBtX2dnRXFVL0FBQUFBQUFBQUFJL0FBQUFBQUFBQUFBL0FDSGkzcmN6UlFyZXk0elJGWEV3d1loWC1mMHVxS3RKQ1Evczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IkRhbmllbCIsImZhbWlseV9uYW1lIjoiUGVpeG90byIsImxvY2FsZSI6InB0LUJSIiwiaWF0IjoxNTY4OTM0MjMzLCJleHAiOjE1Njg5Mzc4MzN9.xq_Gxsp4cyD2Eo9_ecr--0TUSCAICbTgk74fVLPWq04CLPBcpZVwE-v-J1b9mXCJtzgHb49gZ_Fz6EbborZqDqxjNwrBmisV_mMubUZWNRa4dNvASTN5xz0ZOZFKPsdxTU-mpOoOMXJjy2z8kuE_eA1YyaV5S-VVzPmeqi1AYOGjGiIiJ-bsJtL_JkrcMH0Zcz4ZV1NpV8qqSAZ8e7swj-LIOXsv4Yy0I1i7c4C-vREGzwkD0grS2WctoMEq6JxCLx-esx2ztazbGbUlx-s893rdyJgA2sD_gUhE2kSgU-9qWVOVAq99l400zwLWb1wuLa39iIguJ5gSNx24ry3BvA")
        assertThat(user.email).isEqualTo("danielspeixoto@outlook.com")
//        TODO ADD other assertions
    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<Exception> {
            googleAPI.data("invalid")
        }
    }

}