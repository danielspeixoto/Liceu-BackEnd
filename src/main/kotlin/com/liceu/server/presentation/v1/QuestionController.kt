package com.liceu.server.presentation.v1

import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.video.Video
import com.liceu.server.presentation.util.networkData
import com.liceu.server.presentation.v1.Response.Companion.ALREADY_EXISTS_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.NOT_FOUND_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.STATUS_ERROR
import com.liceu.server.presentation.v1.Response.Companion.UNKNOWN_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.VALIDATION_ERROR_CODE
import com.liceu.server.util.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/question")
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

    data class VideoResponse(
            val id: String,
            val title: String,
            val description: String,
            val videoId: String,
            val questionId: String,
            val aspectRatio: Float,
            val thumbnail: String,
            val channelTitle: String
    )

    @GetMapping
    fun questions(
            @RequestParam(value = "tags[]", defaultValue = "") tagNames: List<String>,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest

    ): Response<List<QuestionResponse>> {
        val eventName = "question_get"
        val eventTags = listOf(NETWORK, QUESTION, RETRIEVAL)
        val networkData = networkData(request)

        Logging.info(eventName, eventTags, data = networkData)
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

    @GetMapping("/{questionId}/relatedVideos")
    fun videos(
            @PathVariable("questionId") questionId: String,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest
    ): Response<List<Map<String, Any>>> {
        val eventName = "question_videos_get"
        val eventTags = listOf(NETWORK, QUESTION, RETRIEVAL, VIDEO)
        val networkData = networkData(request)

        Logging.info(eventName, eventTags, data = networkData)
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

    @PostMapping("/{questionId}/tags")
    fun tags(
            @PathVariable("questionId") questionId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): Response<HashMap<String,Any>> {
        val eventName = "question_tags_post"
        val eventTags = listOf(NETWORK, QUESTION, INSERTION, TAG)
        val networkData = networkData(request)

        Logging.info(eventName, eventTags, data = networkData)

        return try {
            if (!body.containsKey("name") || body["name"] !is String) {
                throw InputValidationException()
            }
            addTag.run(questionId, body["name"] as String)
            Response()
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            val errorCode = when (e) {
                is ItemNotFoundException -> NOT_FOUND_ERROR_CODE
                is TagAlreadyExistsException -> ALREADY_EXISTS_ERROR_CODE
                is InputValidationException -> VALIDATION_ERROR_CODE
                else -> UNKNOWN_ERROR_CODE
            }
            Response(hashMapOf(), status = STATUS_ERROR, errorCode = errorCode)
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