package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.CONTROLLER
import com.liceu.server.domain.global.NETWORK
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.domain.post.PostSubmission
import com.liceu.server.domain.post.PostThumbnails
import com.liceu.server.domain.post.PostVideo
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
@RequestMapping("/v2/post")
class PostController(
        @Autowired val textPost: PostBoundary.ITextPost,
        @Autowired val videoPost: PostBoundary.IVideoPost
) {
    @Autowired
    lateinit var netUtils: NetworkUtils

    @PostMapping
    fun post(
        @RequestAttribute("userId") userId: String,
        @RequestBody body: HashMap<String, Any>,
        request: HttpServletRequest
    ): ResponseEntity<HashMap<String, Any>> {
        val eventName = "post_submission"
        val eventTags = listOf(CONTROLLER, NETWORK, POST)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            var id = ""
            val type = body["type"] as String? ?: throw ValidationException()
            val description = body["description"] as String? ?: throw ValidationException()
            if(type == "text"){
                id = textPost.run(PostSubmission(
                        userId,
                        type,
                        description,
                        null,
                        null
                ))
            }else if(type == "video"){
                val video = PostVideo(
                    body["videoUrl"] as String? ?: throw ValidationException(),
                    PostThumbnails(
                            body["high"] as String? ?: throw ValidationException(),
                            body["default"] as String? ?: throw ValidationException(),
                            body["medium"] as String? ?: throw ValidationException()
                    )
                )
                id = videoPost.run(PostSubmission(
                        userId,
                        type,
                        description,
                        null,
                        video
                ))
            }

            ResponseEntity(hashMapOf<String,Any>(
                    "id" to id
            ), HttpStatus.OK)

        }catch (e: Exception) {
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