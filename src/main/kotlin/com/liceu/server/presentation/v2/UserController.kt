package com.liceu.server.presentation.v2

import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController {

    @GetMapping
    fun me(principal: Principal): String {
        return "deu certo"
    }
}