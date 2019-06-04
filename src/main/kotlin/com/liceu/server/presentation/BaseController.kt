package com.liceu.server.presentation

import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class BaseController {

    @RequestMapping("/")
    fun get(): String {
        return "Hello World"
    }

}