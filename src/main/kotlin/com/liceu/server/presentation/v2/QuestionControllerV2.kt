package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.video.Video
import com.liceu.server.presentation.Response
import com.liceu.server.presentation.Response.Companion.ALREADY_EXISTS_ERROR_CODE
import com.liceu.server.presentation.Response.Companion.NOT_FOUND_ERROR_CODE
import com.liceu.server.presentation.Response.Companion.STATUS_ERROR
import com.liceu.server.presentation.Response.Companion.UNKNOWN_ERROR_CODE
import com.liceu.server.presentation.Response.Companion.VALIDATION_ERROR_CODE
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v2/question")
class QuestionControllerV2(
        @Autowired val random: QuestionBoundary.IRandom,
        @Autowired val videos: QuestionBoundary.IVideos
) {

    @Autowired
    lateinit var netUtils: NetworkUtils

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

    @GetMapping
    fun questions(
            @RequestParam(value = "tags", defaultValue = "") tagNames: List<String>,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest

    ): Response<List<QuestionResponse>> {
        val eventName = "question_get"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data =networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = random.run(tagNames, amount)
            Response(result.map { toQuestionResponse(it) })
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            Response(listOf(), status = STATUS_ERROR, errorCode = UNKNOWN_ERROR_CODE)
        }
    }

    @GetMapping("/{questionId}/videos")
    fun videos(
            @PathVariable("questionId") questionId: String,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest
    ): Response<List<Map<String, Any>>> {
        val eventName = "question_videos_get"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, RETRIEVAL, VIDEO)
        val networkData =  netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = videos.run(questionId, start, amount)
            Response(result.map { toVideoResponse(it) })
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags + listOf(TAG),
                    e, data = networkData
            )
            Response(listOf(), status = STATUS_ERROR, errorCode = UNKNOWN_ERROR_CODE)
        }
    }

    fun toQuestionResponse(question: Question): QuestionResponse {
        return QuestionResponse(
                question.id,
                question.imageURL,
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

    fun toVideoResponse(video: Video): Map<String, Any> {
        return hashMapOf<String, Any>(
                "id" to video.id,
                "title" to video.title,
                "description" to video.description,
                "videoId" to video.videoId,
                "questionId" to video.questionId,
                "aspectRatio" to video.aspectRatio,
                "thumbnails" to hashMapOf(
                        "default" to video.thumbnail
                ),
                "channel" to hashMapOf(
                        "title" to video.channelTitle
                )
        )
    }

}