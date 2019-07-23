package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.CONTROLLER
import com.liceu.server.domain.global.NETWORK
import com.liceu.server.domain.global.REPORT
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.trivia.TriviaQuestionSubmission
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
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
        @Autowired val submit: TriviaBoundary.ISubmit
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
        }catch(e: Exception){
            when(e) {
                is ValidationException, is ClassCastException -> {
                    Logging.error(
                            eventName,
                            eventTags,
                            e, data = networkData
                    )
                    ResponseEntity(HttpStatus.BAD_REQUEST)
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




}