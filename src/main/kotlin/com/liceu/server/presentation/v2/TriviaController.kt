package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.trivia.TriviaQuestionSubmission
import com.liceu.server.domain.trivia.UpdateCommentsTrivia
import com.liceu.server.presentation.util.converters.TriviaQuestionResponse
import com.liceu.server.presentation.util.converters.toTriviaQuestionResponse
import com.liceu.server.presentation.util.handleException
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException


@RestController
@RequestMapping("/v2/trivia")
class TriviaController(
        @Autowired val submit: TriviaBoundary.ISubmit,
        @Autowired val randomQuestions: TriviaBoundary.IRandomQuestions,
        @Autowired val updateCommentsTrivia: TriviaBoundary.IUpdateListOfComments,
        @Autowired val updateRatingTrivia: TriviaBoundary.IUpdateRating

) {
    @Autowired
    lateinit var netUtils: NetworkUtils

    @PostMapping
    fun submit(
        @RequestAttribute("userId") userId: String,
        @RequestBody body: HashMap<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<HashMap<String,Any>>{
        val eventName = "trivia_question_post"
        val eventTags = listOf(CONTROLLER, NETWORK, REPORT)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val messageReq = body["question"] as String? ?: throw ValidationException()
            val answerReq = body["correctAnswer"] as String? ?: throw ValidationException()
            val wrongReq = body["wrongAnswer"] as String? ?: throw ValidationException()
            val tags = body["tags"] as List<String>? ?: throw ValidationException()


            val id = submit.run(TriviaQuestionSubmission(
                    userId,
                    messageReq,
                    answerReq,
                    wrongReq,
                    tags
            ))
            ResponseEntity(hashMapOf<String,Any>(
                    "id" to id
            ), HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("userId" to userId) +
                    ("question" to body["question"]) +
                    ("correctAnswer" to body["correctAnswer"]) +
                    ("wrongAnswer" to body["wrongAnswer"]) +
                    ("tags" to  body["tags"])
            )
        }
    }

    @GetMapping
    fun triviaQuestions(
            @RequestParam(value = "tags", defaultValue = "") tags: List<String>,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<TriviaQuestionResponse>> {
        val eventName = "trivia_question_get"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, TRIVIA ,RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = randomQuestions.run(tags, amount)
            ResponseEntity(result.map { toTriviaQuestionResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("tags" to tags) +
                    ("amount" to amount)
            )
        }
    }

    @PutMapping ("/{questionId}/comment")
    fun updateComments(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("questionId") questionId: String,
            @RequestBody body: java.util.HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "put_comment_question"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, COMMENT, UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val comment = body["comment"] as String? ?: throw ValidationException()
            updateCommentsTrivia.run(questionId, authenticatedUserId, comment)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("userId" to authenticatedUserId) +
                    ("questionId" to questionId) +
                    ("comment" to body["comment"])
            )
        }
    }

    @PutMapping ("/{questionId}/rating")
    fun updateRating(
            @PathVariable("questionId") questionId: String,
            @RequestBody body: java.util.HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "put_comment_rating"
        val eventTags = listOf(CONTROLLER, NETWORK, QUESTION, RATING, UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val rating = body["rating"] as Int? ?:throw ValidationException()
            updateRatingTrivia.run(questionId,rating)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("questionId" to questionId) +
                    ("rating" to body["rating"])
            )
        }
    }


}