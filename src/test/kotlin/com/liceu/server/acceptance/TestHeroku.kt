package com.liceu.server.acceptance

import com.google.common.truth.Truth
import com.google.gson.GsonBuilder
import com.liceu.server.TestConfiguration
import com.liceu.server.data.UserRepository
import com.liceu.server.util.JWTAuth
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.io.*
import java.net.HttpURLConnection
import java.net.URL;


@ContextConfiguration(classes=[TestConfiguration::class])
class TestHeroku  {


    lateinit var auth: String
    @Autowired
    lateinit var userRepo: UserRepository
    @Autowired
    lateinit var jwtAuth: JWTAuth


    @BeforeEach
    fun login_ValidAccessToken_CreatesUserOrLogsIn() {
        val body = hashMapOf<String, Any>(
                "accessToken" to "EAAf4pgUyFpsBACERnEgTjs232323UY2ZAMfCWmcvAsryuVgz8MqIhFAn96jk3e5bVOllp3WLVcIaBnP6DhiuKgAMqpsM7IlKXdZCfP1apvoZCitFCEyR48avZB2qj2cYKWiwQbmhaL7DcnbnMMwO9FeKpDIeYviWNCDtBZBx6OimR9vEu1CIhy9Da1eGPcZBCvqCGPULYR2JbYM62FhA"
        )

        val url = URL("https://liceu-dev.herokuapp.com/v2/login")
        val con = url.openConnection() as HttpURLConnection
        con.doOutput = true
        con.run {
            requestMethod = "POST"
            setRequestProperty("API_KEY","2VsYHwfQKtjiAdLs8Z2fTLwuLpofSXWy")
            setRequestProperty("Content-Type","application/json")
        }
        val gson = GsonBuilder().create()
        val jsonString = gson.toJson(body)
        val wr = DataOutputStream(con.outputStream)
        wr.writeBytes(jsonString)
        wr.flush()
        wr.close()
        Truth.assertThat(con.responseCode).isEqualTo(200)
        auth = con.getHeaderField("Authorization")
        Truth.assertThat(auth.length).isGreaterThan(10)
    }


    //@Test
    fun getRanking_Exists_returnTopGames() {
        val url = URL("https://liceu-dev.herokuapp.com/v2/ranking/?year=2019&month=7&amount=5")
        val con = url.openConnection() as HttpURLConnection
        con.doOutput = true
        con.setRequestMethod("GET")
        con.setRequestProperty("API_KEY","2VsYHwfQKtjiAdLs8Z2fTLwuLpofSXWy")
        con.setRequestProperty("Authorization", auth)
        Truth.assertThat(con.responseCode).isEqualTo(200)

        val result = this.read(con.inputStream)
        val first = result[0].toMap()

        Truth.assertThat(result.size).isEqualTo(5)
        Truth.assertThat(first["id"].toString().isNotEmpty()).isTrue()
    }


    fun read(istr: InputStream): List<HashMap<String, Any>> {
        val gson = GsonBuilder().create();
        var to: BufferedReader? = null
        try {
            to = BufferedReader(InputStreamReader(istr))
            return gson.fromJson<List<HashMap<String, Any>>>(to, List::class.java)
        } catch (ioe: IOException) {
            throw ioe
        }
    }
}