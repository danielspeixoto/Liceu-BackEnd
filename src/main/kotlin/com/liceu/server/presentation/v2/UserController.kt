package com.liceu.server.presentation.v2

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.User
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException

@RestController
@RequestMapping("/v2/user")
class UserController (
        @Autowired val user: UserBoundary.IUserById
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


    fun toUserResponse(user: User): UserResponse {
        return UserResponse (
                user.id,
                user.name,
                user.email,
                user.picture
        )
    }
}