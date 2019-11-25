package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.post.*
import com.liceu.server.presentation.util.converters.PostResponse
import com.liceu.server.presentation.util.converters.toPostResponse
import com.liceu.server.presentation.util.handleException
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v2/feed")
class FeedController(
        @Autowired val getPostsForFeed: PostBoundary.IGetPosts,
        @Autowired val getRandomPosts: PostBoundary.IGetRandomPosts
) {
    @Autowired
    lateinit var netUtils: NetworkUtils

    @GetMapping(produces=["application/json;charset=UTF-8"])
    fun getPostsForFeed(
            @RequestAttribute("userId") userId: String,
            @RequestParam("before") before: String,
            @RequestParam("start", defaultValue = "0") start: Int,
            @RequestParam("amount") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>> {
        val eventName = "posts_get_for_feed"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, FEED, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val date = Date.from(Instant.parse(before))
            val postsRetrieved = getPostsForFeed.run(userId, date,amount,start)
            ResponseEntity(postsRetrieved?.map { toPostResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData)
        }
    }
}