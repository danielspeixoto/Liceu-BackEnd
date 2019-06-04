package com.liceu.server.presentation.v2

import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/v2/user")
class UserController {

    @GetMapping
    fun me(
        @RequestAttribute("userId") userId: String
    ): String {
        return userId
    }
}