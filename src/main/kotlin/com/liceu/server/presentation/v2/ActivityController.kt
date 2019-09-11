package com.liceu.server.presentation.v2

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.global.*
import com.liceu.server.presentation.util.converters.ActivityResponse
import com.liceu.server.presentation.util.converters.toActivityResponse
import com.liceu.server.presentation.util.handleException
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/v2/activity")
class ActivityController(
        @Autowired val getActivityFromUser: ActivityBoundary.IGetActivitiesFromUser
) {

    @Autowired
    lateinit var netUtils: NetworkUtils

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
        }catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData)
        }
    }

}