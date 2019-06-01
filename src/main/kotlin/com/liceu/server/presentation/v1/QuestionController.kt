package com.liceu.server.presentation.v1

import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.presentation.util.networkData
import com.liceu.server.util.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/questions")
class QuestionController(
        @Autowired val random: QuestionBoundary.IRandom,
        @Autowired val addTag: QuestionBoundary.IAddTag,
        @Autowired val videos: QuestionBoundary.IVideos
) {

    data class QuestionResponse(
            val id: String,
            val view: String,
            val source: String,
            val variant: String,
            val edition: Int,
            val number: Int,
            val domain: String,
            val answer: Int,
            val tags: List<String>,
            val stage: Int,
            val width: Int,
            val height: Int
    )

    @RequestMapping("/")
    fun get(
            @RequestParam(value = "tags[]", defaultValue = "") tagNames: List<String>,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request : HttpServletRequest

    ): Response<List<QuestionResponse>> {
        val eventName = "question_get"
        val eventTags = listOf(NETWORK, QUESTION, RETRIEVAL)
        val networkData = networkData(request)

        Logging.info(eventName, eventTags, data=networkData)
        return try {
            val result = random.run(tagNames, amount)
            Response(result.map { toQuestionResponse(it) })
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags + listOf(TAG),
                    e, data=networkData
            )
            Response(listOf(), status = STATUS_ERROR, errorCode = UNKNOWN_ERROR)
        }
    }

    fun toQuestionResponse(question: Question): QuestionResponse {
        return QuestionResponse(
                question.id,
                question.view,
                question.source,
                question.variant,
                question.edition,
                question.number,
                question.domain,
                question.answer,
                question.tags,
                question.stage,
                question.width,
                question.height
        )
    }

}