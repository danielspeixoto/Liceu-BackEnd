package com.liceu.server.presentation.v1

import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.util.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.Exception

@RestController
@RequestMapping("/questions")
class QuestionController(
        @Autowired val random: QuestionBoundary.IRandom,
        @Autowired val addTag: QuestionBoundary.IAddTag,
        @Autowired val videos: QuestionBoundary.IVideos
) {

    @RequestMapping("/")
    fun get(
            @RequestParam(value="tags[]", defaultValue="") tagNames: List<String>,
            @RequestParam(value="amount", defaultValue="0") amount: Int
    ): Response<List<Question>> {
        val eventName = "question_get"
        val eventTags = listOf(NETWORK, QUESTION, RETRIEVAL)
        Logging.info(eventName, eventTags)
        return try {
            val result = random.run(tagNames, amount)
            Response(result)
        } catch (e: Exception) {
            Logging.error(eventName, eventTags + listOf(TAG), e)
            Response(listOf(), status=STATUS_ERROR)
        }
    }

}