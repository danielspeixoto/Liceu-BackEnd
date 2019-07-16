package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.video.Video
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v2/question")
class QuestionControllerV2(
        @Autowired val random: QuestionBoundary.IRandom,
        @Autowired val videos: QuestionBoundary.IVideos,
        @Autowired val question: QuestionBoundary.IQuestionById

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

    ): ResponseEntity<List<QuestionResponse>> {
        val eventName = "question_get"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = random.run(tagNames, amount)
            ResponseEntity(result.map { toQuestionResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{questionId}/videos")
    fun videos(
            @PathVariable("questionId") questionId: String,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<Map<String, Any>>> {
        val eventName = "question_videos_get"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, RETRIEVAL, VIDEO)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = videos.run(questionId, start, amount)
            result.map { toVideoResponse(it) }
            ResponseEntity(result.map { toVideoResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags + listOf(TAG),
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{questionId}")
    fun getUserById(
            @PathVariable("questionId") questionId: String,
            request: HttpServletRequest
    ): ResponseEntity<QuestionResponse> {
        val eventName = "get_question_by_id"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, QUESTION, ID)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = question.run(questionId)
            val desiredQuestion = toQuestionResponse(result)
            ResponseEntity(desiredQuestion, HttpStatus.OK)
        } catch (e: Exception) {
            when(e) {
                is ItemNotFoundException -> {
                    Logging.error(
                            eventName,
                            eventTags,
                            e, data = networkData
                    )
                    ResponseEntity(HttpStatus.NOT_FOUND)
                }
                else -> {
                    Logging.error(
                            eventName,
                            eventTags,
                            e, data = networkData
                    )
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
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