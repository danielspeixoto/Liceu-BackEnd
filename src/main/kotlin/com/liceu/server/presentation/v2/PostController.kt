package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.post.*
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException
import kotlin.collections.HashMap

@RestController
@RequestMapping("/v2/post")
class PostController(
        @Autowired val textPost: PostBoundary.ITextPost,
        @Autowired val videoPost: PostBoundary.IVideoPost,
        @Autowired val getPostsForFeed: PostBoundary.IGetPosts,
        @Autowired val getPostsFromUSer: PostBoundary.IGetPostsFromUser,
        @Autowired val getRandomPosts: PostBoundary.IGetRandomPosts,
        @Autowired val updateComments: PostBoundary.IUpdateListOfComments
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
            var questions = emptyList<PostQuestions>()
            if(body["hasQuestions"] as String?  == "true"){
                val questionsPassed = body["questions"] as List<HashMap<String,Any>>
                questions = questionsPassed.map {
                    PostQuestions(
                            it["question"] as String? ?: throw ValidationException(),
                            it["correctAnswer"] as String? ?: throw ValidationException(),
                            it["otherAnswers"] as List<String>? ?: throw ValidationException()
                    )
                }
            }
            if(type == "text"){
                id = textPost.run(PostSubmission(
                        userId,
                        type,
                        description,
                        null,
                        null,
                        questions
                ))
            }
            if(type == "video"){
                val video = PostVideo(
                    body["videoUrl"] as String? ?: throw ValidationException(),
                    PostThumbnails(
                            null,
                            null,
                            null
                    )
                )
                id = videoPost.run(PostSubmission(
                        userId,
                        type,
                        description,
                        null,
                        video,
                        questions
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

    @GetMapping
    fun getPostsForFeed(
            @RequestAttribute("userId") userId: String,
            @RequestParam("before") before: String,
            @RequestParam("amount") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>>{
        val eventName = "posts_get_for_feed"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, FEED, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val date = Date.from(Instant.parse(before))
            val postsRetrieved = getPostsForFeed.run(userId,date,amount)
            ResponseEntity(postsRetrieved?.map { toPostResponse(it) }, HttpStatus.OK)
        }catch (e: Exception){
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{userId}")
    fun getPostsFromUser(
        @PathVariable("userId") userId: String,
        request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>>{
        val eventName = "posts_get_from_user"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, USER, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try{
            val postsRetrieved = getPostsFromUSer.run(userId)
            ResponseEntity(postsRetrieved.map { toPostResponse(it) }, HttpStatus.OK)
        }catch (e: Exception){
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }

    @GetMapping("/explore")
    fun getRandomPosts (
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
            ResponseEntity(postsRetrieved.map { toPostResponse(it) },HttpStatus.OK)
        }catch (e: Exception){
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping ("/{postId}/comment")
    fun updatePostComments(
            @PathVariable("postId") postId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void>{
        val eventName = "put_comment_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, COMMENT, UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val userId = body["userId"] as String? ?:throw ValidationException()
            val comment = body["comment"] as String? ?:throw ValidationException()
            updateComments.run(postId,userId,comment)
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

    data class PostResponse(
            val id: String,
            val userId: String,
            val type: String,
            val description: String,
            val imageURL: String?,
            val video: PostVideo?,
            val submissionDate: Date,
            val comments: List<PostComment>?
    )

    fun toPostResponse(post: Post): PostResponse{
        return PostResponse(
                post.id,
                post.userId,
                post.type,
                post.description,
                post.imageURL,
                post.video,
                post.submissionDate,
                post.comments
        )
    }

}