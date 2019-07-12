package com.liceu.server.presentation.v2

import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game
import com.liceu.server.domain.game.GameBoundary
import com.liceu.server.domain.game.GameSubmission
import com.liceu.server.domain.global.AUTH
import com.liceu.server.domain.global.CONTROLLER
import com.liceu.server.domain.global.GAME
import com.liceu.server.domain.global.NETWORK
import com.liceu.server.presentation.Response
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException

@RestController
@RequestMapping("/v2/game")
class GameController(
        @Autowired val submit: GameBoundary.ISubmit
) {

    @Autowired
    lateinit var netUtils: NetworkUtils


    @PostMapping
    fun submit(
            @RequestAttribute("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<HashMap<String, Any>> {
        val eventName = "game_post"
        val eventTags = listOf(CONTROLLER, NETWORK, GAME)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))

        return try {
            val answersReq = body["answers"] ?: throw ValidationException()
            answersReq as List<HashMap<String, Any>>

            val answers = answersReq.map {
                Answer(
                        it["questionId"] as String? ?: throw ValidationException(),
                        it["correctAnswer"] as Int? ?: throw ValidationException(),
                        it["selectedAnswer"] as Int? ?: throw ValidationException()
                )
            }

            val id = submit.run(GameSubmission(
                    userId,
                    answers,
                    body["timeSpent"] as Int? ?: throw ValidationException()
            ))
            ResponseEntity(hashMapOf<String,Any>(
                    "id" to id
            ), HttpStatus.OK)
        } catch (e: Exception) {
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