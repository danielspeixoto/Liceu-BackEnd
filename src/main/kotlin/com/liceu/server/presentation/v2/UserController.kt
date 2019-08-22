package com.liceu.server.presentation.v2

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.*
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.*
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import com.mongodb.client.model.geojson.GeoJsonObjectType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.util.HashMap
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException

@RestController
@RequestMapping("/v2/user")
class UserController (
        @Autowired val user: UserBoundary.IUserById,
        @Autowired val challengesFromUser: UserBoundary.IChallengesFromUserById,
        @Autowired val updateLocation: UserBoundary.IUpdateLocation,
        @Autowired val updateSchool: UserBoundary.IUpdateSchool,
        @Autowired val updateAge: UserBoundary.IUpdateAge,
        @Autowired val updateYoutubeChannel: UserBoundary.IUpdateYoutubeChannel,
        @Autowired val updateInstagramProfile: UserBoundary.IUpdateInstagramProfile,
        @Autowired val updateDescription: UserBoundary.IUpdateDescription,
        @Autowired val updateWebsite: UserBoundary.IUpdateWebsite,
        @Autowired val getUsersByNameUsingLocation: UserBoundary.IGetUsersByNameUsingLocation
) {


    data class UserResponse(
            val id: String,
            val name: String,
            val email: String,
            val picture: Picture,
            val state: String?,
            val school: String?,
            val age: Int?,
            val youtubeChannel: String?,
            val instagramProfile: String?,
            val description: String?,
            val website: String?

    )



    @Autowired
    lateinit var netUtils: NetworkUtils

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

    @GetMapping
    fun getUsersByNameUsingLocation(
            @RequestParam(value = "nameRequired", defaultValue = "") nameRequired: String,
            @RequestParam(value = "longitude", defaultValue = "") longitude: Double,
            @RequestParam(value = "latitude", defaultValue = "") latitude: Double,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<UserResponse>> {
        val eventName = "get_users_by_name_near_location"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, USER, LOCATION)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = getUsersByNameUsingLocation.run(nameRequired,longitude,latitude,amount)
            val desiredUser = result.map {toUserResponse(it)}
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

    @PutMapping("/{userId}/locale")
    fun updateLocation(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_location"
        val eventTags = listOf(CONTROLLER, NETWORK, LOCATION , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val longitude = body["longitude"] as Double? ?: throw ValidationException()
            val latitude = body["latitude"] as Double? ?: throw ValidationException()
            updateLocation.run(userId,longitude,latitude)
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

    @PutMapping("/{userId}/school")
    fun updateSchool(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_school"
        val eventTags = listOf(CONTROLLER, NETWORK, SCHOOL , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val school = body["school"] as String? ?: throw ValidationException()
            updateSchool.run(userId,school)
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

    @PutMapping("/{userId}/age")
    fun updateAge(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_age"
        val eventTags = listOf(CONTROLLER, NETWORK, AGE , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val day = body["day"] as Int? ?: throw ValidationException()
            val month = body["month"] as Int? ?: throw ValidationException()
            val year = body["year"] as Int? ?: throw ValidationException()
            updateAge.run(userId,day,month,year)
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

    @PutMapping("/{userId}/youtube")
    fun updateYoutubeChannel(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_youtube_channel"
        val eventTags = listOf(CONTROLLER, NETWORK, YOUTUBE , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val youtubeChannel = body["youtubeChannel"] as String? ?: throw ValidationException()
            updateYoutubeChannel.run(userId,youtubeChannel)
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

    @PutMapping("/{userId}/instagram")
    fun updateInstagramProfile(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_instagram_profile"
        val eventTags = listOf(CONTROLLER, NETWORK, INSTAGRAM , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val instagramProfile = body["instagramProfile"] as String? ?: throw ValidationException()
            updateInstagramProfile.run(userId,instagramProfile)
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

    @PutMapping("/{userId}/description")
    fun updateDescription(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_description"
        val eventTags = listOf(CONTROLLER, NETWORK, DESCRIPTION , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val description = body["description"] as String? ?: throw ValidationException()
            updateDescription.run(userId,description)
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

    @PutMapping("/{userId}/website")
    fun updateWebsite(
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "update_website"
        val eventTags = listOf(CONTROLLER, NETWORK, WEBSITE , UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{

            val website = body["website"] as String? ?: throw ValidationException()
            updateWebsite.run(userId,website)
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

    fun toUserResponse(user: User): UserResponse {
        return UserResponse (
                user.id,
                user.name,
                user.email,
                user.picture,
                user.state,
                user.school,
                user.age,
                user.youtubeChannel,
                user.instagramProfile,
                user.description,
                user.website
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