package com.liceu.server.integration

import com.liceu.server.data.GoogleAPI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.google.common.truth.Truth.assertThat
import javax.naming.AuthenticationException


class TestGoogleAPIIntegration {

    val googleAPI = GoogleAPI()

    @Test
    fun data_ValidAccessToken_ReturnsInfo() {
        val user = googleAPI.data("eyJhbGciOiJSUzI1NiIsImtpZCI6IjM0OTRiMWU3ODZjZGFkMDkyZTQyMzc2NmJiZTM3ZjU0ZWQ4N2IyMmQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxNTY4NzM2MDY0NzgtbzZkN2FwbXU5MGNyZWs4MG9tdjlkNDRob3A0ajM4Z24uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxNTY4NzM2MDY0NzgtbzZkN2FwbXU5MGNyZWs4MG9tdjlkNDRob3A0ajM4Z24uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTM1MTAzMzc1ODcwNjA1MzQ1ODIiLCJlbWFpbCI6ImluZ29hbG1laWRhQGhvdG1haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJYNnJGQmdNdDVOSUFHdy1PMUhXakRBIiwibmFtZSI6IlRoZVhaMTMwIiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8taTBFV2VFT1J4bk0vQUFBQUFBQUFBQUkvQUFBQUFBQUFBQ3MvYUNNV2lYeXc0TVkvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IlRoZVhaMTMwIiwiZmFtaWx5X25hbWUiOiIuIiwibG9jYWxlIjoicHQtQlIiLCJpYXQiOjE1NjUwNTMzNzUsImV4cCI6MTU2NTA1Njk3NX0.knpPaeKQrOL0Y1mspWYX6sF1B3PP28dDga8svGIuQlF1T8s69Iw_hJmb41NE4OmsoZJvLOlxEDMNmFDQRuX-Rv8uRz2PYn1hoWiGJevRU8NBAnL42FhixNJICpmrgjRsEDxir1MMm4QFHfxTeSo5i6ClPabHGbFu0E0id4Jr4-zsJEEeRqCrxPKLojCgScoC5Iy4GCvgiOZcueQKcROd5rl3GDDuGBoTbGdlLjIunY74IUjDupqwmXFz5l78aAFF0LIsRf0umVCGokAEPBX4iEKryTWr0a4vr5jcZXUw9uzykIeY7cOkiU3WHrjZEUDJ35F_jfufLvMYA85qut0lsw")
        assertThat(user.email).isEqualTo("ingoalmeida@hotmail.com")
    }

    @Test
    fun data_InvalidAccessToken_ThrowsError() {
        assertThrows<Exception> {
            googleAPI.data("invalid")
        }
    }

}