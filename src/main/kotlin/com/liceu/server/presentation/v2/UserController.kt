package com.liceu.server.presentation.v2

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.*
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.User
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v2/user")
class UserController (
        @Autowired val user: UserBoundary.IUserById,
        @Autowired val challengesFromUser: UserBoundary.IChallengesFromUserById
) {

    data class UserResponse(
            val id: String,
            val name: String,
            val email: String,
            val picture: Picture
    )


    @Autowired
    lateinit var netUtils: NetworkUtils

    @GetMapping
    fun me(
        @RequestAttribute("userId") userId: String
    ): String {
        return userId
    }

    @GetMapping("/{userId}")
    fun getUserById(
            @PathVariable("userId") userId: String,
            request: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val eventName = "get_user_by_id"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, USER, ID)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = user.run(userId)
            val desiredUser = toUserResponse(result)
            ResponseEntity(desiredUser, HttpStatus.OK)
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


    @GetMapping("/{userId}/challenge")
    fun getChallengesFromUserById(
            @PathVariable("userId") userId: String,
            request: HttpServletRequest
    ): ResponseEntity<List<ChallengeResponse>> {
        val eventName = "get_challenges_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, CHALLENGE ,USER)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val challenges = challengesFromUser.run(userId)
            val challengesResponse = challenges.map { toChallengeResponse(it) }
            ResponseEntity(challengesResponse, HttpStatus.OK)
        }catch (e: Exception){
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

    fun toUserResponse(user: User): UserResponse {
        return UserResponse (
                user.id,
                user.name,
                user.email,
                user.picture
        )
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