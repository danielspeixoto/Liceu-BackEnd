package com.liceu.server.presentation.v2

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.*
import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.*
import com.liceu.server.presentation.util.converters.*
import com.liceu.server.presentation.util.handleException
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
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
        @Autowired val updateProducerToBeFollowed: UserBoundary.IUpdateProducerToBeFollowed,
        @Autowired val updateProducerToBeUnfollowed: UserBoundary.IUpdateProducerToBeUnfollowed,
        @Autowired val updateProfileImage: UserBoundary.IUpdateProfileImage,
        @Autowired val updateFcmToken: UserBoundary.IUpdateFcmToken,
        @Autowired val updateLastAccess: UserBoundary.IUpdateLastAccess,
        @Autowired val updateDesiredCourse: UserBoundary.IUpdateCourse,
        @Autowired val updateTelephoneNumber: UserBoundary.IUpdateTelephoneNumber,
        @Autowired val updatePostToBeSaved: UserBoundary.IUpdatePostToBeSaved,
        @Autowired val updatePostSavedToBeRemoved: UserBoundary.IUpdateSavedPostToBeRemoved,
        @Autowired val getUsersByNameUsingLocation: UserBoundary.IGetUsersByNameUsingLocation,
        @Autowired val getPostsFromUser: PostBoundary.IGetPostsFromUser,
        @Autowired val getActivityFromUser: ActivityBoundary.IGetActivitiesFromUser,
        @Autowired val getPostsSaved: UserBoundary.IGetSavedPosts

) {

    @Autowired
    lateinit var netUtils: NetworkUtils

    @GetMapping("/{userId}", produces=["application/json;charset=UTF-8"])
    fun getUserById(
            @RequestAttribute("userId") authenticatedUserId: String,
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
            val desiredUser = toUserResponse(result, authenticatedUserId)
            ResponseEntity(desiredUser, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @GetMapping(produces=["application/json;charset=UTF-8"])
    fun getUsersByNameUsingLocation(
            @RequestAttribute("userId") authenticatedUserId: String,
            @RequestParam(value = "name", defaultValue = "") name: String,
            @RequestParam(value = "longitude", defaultValue = "") longitude: Double,
            @RequestParam(value = "latitude", defaultValue = "") latitude: Double,
            @RequestParam(value = "amount", defaultValue = "10") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<UserResponse>> {
        val eventName = "get_users_by_name_near_location"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, USER, LOCATION)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = getUsersByNameUsingLocation.run(name, longitude, latitude, amount)
            val desiredUser = result.map { toUserResponse(it, authenticatedUserId) }
            ResponseEntity(desiredUser, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("nameSearched" to name) +
                    ("amount" to amount)
            )
        }
    }


    @GetMapping("/{userId}/challenge", produces=["application/json;charset=UTF-8"])
    fun getChallengesFromUserById(
            @PathVariable("userId") userId: String,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<ChallengeResponse>> {
        val eventName = "get_challenges_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, RETRIEVAL, CHALLENGE, USER)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val challenges = challengesFromUser.run(userId,start)
            val challengesResponse = challenges.map { toChallengeResponse(it) }
            ResponseEntity(challengesResponse, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @GetMapping("/{userId}/posts", produces=["application/json;charset=UTF-8"])
    fun getPostsFromUser(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            @RequestParam(value = "amount", defaultValue = "20") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>> {
        val eventName = "get_posts_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, USER, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val postsRetrieved = getPostsFromUser.run(userId,authenticatedUserId,amount,start)
            ResponseEntity(postsRetrieved.map { toPostResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("pathVariableUserId" to userId) +
                    ("authenticatedUserId" to authenticatedUserId)
            )
        }
    }


    @GetMapping ("/{userId}/activities", produces=["application/json;charset=UTF-8"])
    fun getActivityFromUser (
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestParam(value = "amount", defaultValue = "20") amount: Int,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            @RequestParam(value = "type", defaultValue = "") type: List<String>,
            request: HttpServletRequest
    ): ResponseEntity<List<ActivityResponse>>{
        val eventName = "get_activity_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, ACTIVITY, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            if(userId != authenticatedUserId){
                throw throw AuthenticationException("user attempting to retrieve other user properties")
            }
            val activitiesRetrieved = getActivityFromUser.run(userId,amount,type,start)
            ResponseEntity(activitiesRetrieved.map { toActivityResponse(it) },HttpStatus.OK)
        }catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("amount" to amount)
            )
        }
    }

    @GetMapping ("/{userId}/savedPosts", produces=["application/json;charset=UTF-8"])
    fun getSavedPosts (
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestParam(value = "amount", defaultValue = "20") amount: Int,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<String>>{
        val eventName = "get_posts_saved_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, USER,POST, SAVED,RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            if(userId != authenticatedUserId){
                throw throw AuthenticationException("user attempting to retrieve other user properties")
            }
            val postsSavedRetrieved = getPostsSaved.run(userId,amount,start)
            ResponseEntity(postsSavedRetrieved,HttpStatus.OK)
        }catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("start" to start) +
                    ("amount" to amount)
            )
        }
    }


    @PutMapping("/{userId}/locale")
    fun updateLocation(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_location"
        val eventTags = listOf(CONTROLLER, NETWORK, LOCATION, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val longitude = body["longitude"] as Double? ?: throw ValidationException()
            val latitude = body["latitude"] as Double? ?: throw ValidationException()
            updateLocation.run(userId, longitude, latitude)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/school")
    fun updateSchool(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_school"
        val eventTags = listOf(CONTROLLER, NETWORK, SCHOOL, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val school = body["school"] as String? ?: throw ValidationException()
            updateSchool.run(userId, school)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/age")
    fun updateAge(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_age"
        val eventTags = listOf(CONTROLLER, NETWORK, AGE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val day = body["day"] as Int? ?: throw ValidationException()
            val month = body["month"] as Int? ?: throw ValidationException()
            val year = body["year"] as Int? ?: throw ValidationException()
            updateAge.run(userId, day, month, year)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("day" to body["day"]) +
                    ("month" to body["month"]) +
                    ("year" to body["year"])
            )
        }
    }

    @PutMapping("/{userId}/youtube")
    fun updateYoutubeChannel(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_youtube_channel"
        val eventTags = listOf(CONTROLLER, NETWORK, YOUTUBE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val youtubeChannel = body["youtubeChannel"] as String? ?: throw ValidationException()
            updateYoutubeChannel.run(userId, youtubeChannel)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("youtubeChannel" to body["youtubeChannel"])
            )
        }
    }

    @PutMapping("/{userId}/instagram")
    fun updateInstagramProfile(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_instagram_profile"
        val eventTags = listOf(CONTROLLER, NETWORK, INSTAGRAM, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val instagramProfile = body["instagramProfile"] as String? ?: throw ValidationException()
            updateInstagramProfile.run(userId, instagramProfile)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("instagramProfile" to body["instagramProfile"])
            )
        }
    }

    @PutMapping("/{userId}/description")
    fun updateDescription(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_description"
        val eventTags = listOf(CONTROLLER, NETWORK, DESCRIPTION, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val description = body["description"] as String? ?: throw ValidationException()
            updateDescription.run(userId, description)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/website")
    fun updateWebsite(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_website"
        val eventTags = listOf(CONTROLLER, NETWORK, WEBSITE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val website = body["website"] as String? ?: throw ValidationException()
            updateWebsite.run(userId, website)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{producerId}/followers")
    fun updateProducerToBeFollowed(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("producerId") producerId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_producer_followed_by_user"
        val eventTags = listOf(CONTROLLER, NETWORK, PRODUCER, FOLLOWED, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            updateProducerToBeFollowed.run(authenticatedUserId, producerId)
            ResponseEntity(HttpStatus.OK)

        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to producerId)
            )
        }
    }

    @DeleteMapping("/{producerId}/followers")
    fun updateProducerToBeUnfollowed(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("producerId") producerId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_producer_unfollowed_by_user"
        val eventTags = listOf(CONTROLLER, NETWORK, PRODUCER, UNFOLLOWED, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            updateProducerToBeUnfollowed.run(authenticatedUserId, producerId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to producerId)
            )
        }
    }

    @PutMapping("/{userId}/profileImage")
    fun updateProfileImage(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_profile_image"
        val eventTags = listOf(CONTROLLER, NETWORK, PROFILE, IMAGE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val imageData = body["imageData"] as String? ?: throw ValidationException()
            updateProfileImage.run(authenticatedUserId,imageData)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/cloudMessaging")
    fun updateFcmToken(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_fcmToken"
        val eventTags = listOf(CONTROLLER, NETWORK, FCMTOKEN, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val fcmToken = body["fcmToken"] as String? ?: throw ValidationException()
            updateFcmToken.run(authenticatedUserId,fcmToken)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("fcmTokenSend" to body["fcmToken"]) +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/check")
    fun updateLastAccess(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_last_access"
        val eventTags = listOf(CONTROLLER, NETWORK, LAST, ACCESS, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            updateLastAccess.run(authenticatedUserId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId)
            )
        }
    }

    @PutMapping("/{userId}/course")
    fun updateDesiredCourse(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_course_desired"
        val eventTags = listOf(CONTROLLER, NETWORK, COURSE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val desiredCourse = body["desiredCourse"] as String? ?: throw ValidationException ()
            updateDesiredCourse.run(authenticatedUserId,desiredCourse)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("course" to body["desiredCourse"])
            )
        }
    }

    @PutMapping("/{userId}/telephone")
    fun updateTelephoneNumber(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_telephone_number"
        val eventTags = listOf(CONTROLLER, NETWORK, TELEPHONE, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val telephoneNumber = body["telephoneNumber"] as String? ?: throw ValidationException ()
            updateTelephoneNumber.run(authenticatedUserId,telephoneNumber)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("telephoneNumber" to body["telephoneNumber"])
            )
        }
    }

    @PutMapping("/{userId}/savePost")
    fun updateSavedPosts(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "update_saved_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, SAVED, POST, UPDATE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            val postId = body["postId"] as String? ?: throw ValidationException ()
            updatePostToBeSaved.run(authenticatedUserId,postId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("postId" to body["postId"])
            )
        }
    }

    @DeleteMapping("/{userId}/{postId}")
    fun removeSavedPost(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("userId") userId: String,
            @PathVariable("postId") postId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "remove_saved_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, SAVED, POST, DELETE)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            if (authenticatedUserId != userId) {
                throw AuthenticationException("user attempting to change other user properties")
            }
            updatePostSavedToBeRemoved.run(authenticatedUserId,postId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("authenticatedUserId" to authenticatedUserId) +
                    ("pathVariableUserId" to userId) +
                    ("postId" to postId)
            )
        }
    }

}

