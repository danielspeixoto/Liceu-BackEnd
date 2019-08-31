package com.liceu.server.presentation.v2

import com.liceu.server.domain.activities.Activity
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivitySubmission
import com.liceu.server.domain.global.*
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
import kotlin.collections.HashMap


@RestController
@RequestMapping("/v2/activity")
class ActivityController(
        @Autowired val insertActivity: ActivityBoundary.IInsertActivity,
        @Autowired val getActivityFromUser: ActivityBoundary.IGetActivitiesFromUser
) {

    @Autowired
    lateinit var netUtils: NetworkUtils

    @PostMapping
    fun insertActivity(
            @RequestAttribute("userId") userId: String,
            @RequestBody body: java.util.HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<HashMap<String,Any>> {
        val eventName = "activity_post"
        val eventTags = listOf(CONTROLLER, NETWORK, ACTIVITY, INSERTION)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val type = body["type"] as String? ?: throw ValidationException()
            val params = body["params"] as HashMap<String,Any>

            val id = insertActivity.run(ActivitySubmission(
                    userId,
                    type,
                    params
            ))
            ResponseEntity(hashMapOf<String,Any>(
                    "id" to id
            ),HttpStatus.OK)

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

    @GetMapping ("/{userId}")
    fun getActivityFromUser (
        @PathVariable("userId") userId: String,
        @RequestParam(value = "amount", defaultValue = "0") amount: Int,
        request: HttpServletRequest
    ): ResponseEntity<List<ActivityResponse>>{
        val eventName = "get_activity_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, ACTIVITY, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val activitiesRetrieved = getActivityFromUser.run(userId,amount)
            ResponseEntity(activitiesRetrieved.map { toActivityResponse(it) },HttpStatus.OK)
        }catch (e: Exception){
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }

    data class ActivityResponse (
            val id: String,
            val userId: String,
            val type: String,
            val params: HashMap<String,Any>,
            val submissionDate: Date
    )

    fun toActivityResponse(activity: Activity): ActivityResponse{
        return ActivityResponse(
                activity.id,
                activity.userId,
                activity.type,
                activity.params,
                activity.submissionDate
        )
    }

}