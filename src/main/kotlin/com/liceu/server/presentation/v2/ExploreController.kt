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
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v2/explore")
class ExploreController(
        @Autowired val getRandomPosts: PostBoundary.IGetRandomPosts
) {
    @Autowired
    lateinit var netUtils: NetworkUtils

    @GetMapping(produces=["application/json;charset=UTF-8"])
    fun getRandomPosts(
            @RequestParam("amount") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>> {
        val eventName = "get_random_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, RANDOM, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val postsRetrieved = getRandomPosts.run(amount)
            ResponseEntity(postsRetrieved.map { toPostResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData)
        }
    }
}