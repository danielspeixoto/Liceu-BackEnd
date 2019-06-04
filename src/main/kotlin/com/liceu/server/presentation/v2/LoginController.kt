package com.liceu.server.presentation.v2

import com.liceu.server.presentation.util.JWTAuth
import com.liceu.server.presentation.v1.Response
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/v2/login")
class LoginController {

    @PostMapping
    fun login(
            @RequestBody body: HashMap<String, String>,
            request: HttpServletRequest,
            response: HttpServletResponse
    ): Response<Void> {
        val accessToken = body["accessToken"] ?: ""
        println(accessToken)
        // TODO Save user and send back id
        response.setHeader(JWTAuth.HEADER_STRING, JWTAuth.sign("id"))
        return Response()
    }
}