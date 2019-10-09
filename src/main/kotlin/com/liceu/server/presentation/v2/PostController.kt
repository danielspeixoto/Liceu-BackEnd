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
import javax.validation.ValidationException

@RestController
@RequestMapping("/v2/post")
class PostController(
        @Autowired val textPost: PostBoundary.ITextPost,
        @Autowired val imagePost: PostBoundary.IImagePost,
        @Autowired val videoPost: PostBoundary.IVideoPost,
        @Autowired val updateComments: PostBoundary.IUpdateListOfComments,
        @Autowired val updateDocument: PostBoundary.IUpdateDocument,
        @Autowired val deletePosts: PostBoundary.IDeletePost,
        @Autowired val getPostById: PostBoundary.IGetPostById
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
        return try {
            val type = body["type"] as String? ?: throw ValidationException()
            val description = body["description"] as String? ?: throw ValidationException()
            var questions = emptyList<PostQuestions>()
            if (body["hasQuestions"] as String? == "true") {
                val questionsPassed = body["questions"] as List<HashMap<String, Any>>
                questions = questionsPassed.map {
                    PostQuestions(
                            it["question"] as String? ?: throw ValidationException(),
                            it["correctAnswer"] as String? ?: throw ValidationException(),
                            it["otherAnswers"] as List<String>? ?: throw ValidationException()
                    )
                }
            }
            val id = when (type) {
                "text" -> textPost.run(PostSubmission(
                        userId,
                        type,
                        description,
                        null,
                        null,
                        questions
                ))
                "video" -> {
                    val video = PostVideo(
                            body["videoUrl"] as String? ?: throw ValidationException(),
                            PostThumbnails(
                                    null,
                                    null,
                                    null

                            )
                    )
                    videoPost.run(PostSubmission(
                            userId,
                            type,
                            description,
                            null,
                            video,
                            questions
                    ))
                }
                "image" -> {
                    val image = PostImage(
                            body["imageTitle"] as String? ?: throw ValidationException(),
                            null,
                            body["imageData"] as String? ?: throw ValidationException()
                    )
                    imagePost.run(PostSubmission(
                            userId,
                            type,
                            description,
                            image,
                            null,
                            questions
                    ))
                }
                else -> throw ValidationException()
            }
            ResponseEntity(hashMapOf<String, Any>(
                    "id" to id
            ), HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("userId" to userId) +
                    ("type" to body["type"]) +
                    ("description" to  body["description"]) +
                    ("hasQuestions" to body["hasQuestions"])
            )
        }
    }

    @PutMapping("/{postId}/comment")
    fun updatePostComments(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("postId") postId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "put_comment_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, COMMENT, UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val comment = body["comment"] as String? ?: throw ValidationException()
            updateComments.run(postId, authenticatedUserId, comment)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId) +
                    ("comment" to body["comment"])
            )
        }
    }

    @PutMapping("/{postId}/docs")
    fun updatePostDocument(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("postId") postId: String,
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "put_document_posts"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, DOCUMENT, UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val documentData = body["documentData"] as String? ?: throw ValidationException()
            val documentTitle = body["documentTitle"] as String? ?: throw ValidationException()
            updateDocument.run(postId, authenticatedUserId,documentTitle,documentData)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId) +
                    ("documentTitle" to body["documentTitle"])
            )
        }
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("postId") postId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "delete_post"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, DELETE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            deletePosts.run(postId, authenticatedUserId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId)
            )
        }
    }

    @GetMapping("/{postId}")
    fun getPostById(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("postId") postId: String,
            request: HttpServletRequest
    ): ResponseEntity<PostResponse> {
        val eventName = "get_post_by_id"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, ID, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val postRetrieved = getPostById.run(postId)
            ResponseEntity(toPostResponse(postRetrieved),HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId)
            )
        }
    }

}