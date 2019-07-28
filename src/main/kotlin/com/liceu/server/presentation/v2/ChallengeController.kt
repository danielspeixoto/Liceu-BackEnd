package com.liceu.server.presentation.v2

import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.challenge.ChallengeBoundary
import com.liceu.server.domain.global.*
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.util.HashMap
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException


@RestController
@RequestMapping("/v2/challenge")
class ChallengeController(
        @Autowired val challenge: ChallengeBoundary.IGetChallenge,
        @Autowired val updateChallenge: ChallengeBoundary.IUpdateAnswers
) {
    @Autowired
    lateinit var netUtils: NetworkUtils


    @GetMapping
    fun getChallenge(
            @RequestAttribute(value = "userId") userId: String,
            request: HttpServletRequest
    ): ResponseEntity<ChallengeResponse> {
        val eventName = "challenge_get"
        val eventTags = listOf(CONTROLLER, NETWORK, CHALLENGE , RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = challenge.run(userId)
            ResponseEntity(toChallengeResponse (result), HttpStatus.OK)

        }catch (e : Exception){
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }


    @PutMapping("/{challengeId}")
    fun updateAnswers(
            @PathVariable("challengeId") challengeId: String,
            @RequestAttribute("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
            ): ResponseEntity<Void>{
        val eventName = "update_challenge"
        val eventTags = listOf(CONTROLLER, NETWORK, CHALLENGE , RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val listAnswers = body["answers"] as List<String>? ?: throw ValidationException()

            updateChallenge.run(challengeId,userId,listAnswers)
            ResponseEntity(HttpStatus.OK)

        }catch (e: Exception) {
            when (e) {
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



    fun toChallengeResponse(challenge: Challenge): ChallengeResponse {
        return ChallengeResponse(
                challenge.id,
                challenge.challenger,
                challenge.challenged,
                challenge.answersChallenger,
                challenge.answersChallenged,
                challenge.scoreChallenger,
                challenge.scoreChallenged,
                challenge.triviaQuestionsUsed
        )
    }

    data class ChallengeResponse(
            val id: String,
            val challenger: String,
            val challenged: String?,
            val answersChallenger: List<String>,
            val answersChallenged: List<String>,
            val scoreChallenger: Int?,
            val scoreChallenged: Int?,
            val triviaQuestionsUsed: List<TriviaQuestion>
    )

}