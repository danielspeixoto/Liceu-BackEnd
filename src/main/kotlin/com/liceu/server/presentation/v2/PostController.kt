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
import kotlin.collections.HashMap

@RestController
@RequestMapping("/v2/post")
class PostController(
        @Autowired val textPost: PostBoundary.ITextPost,
        @Autowired val imagePost: PostBoundary.IImagePost,
        @Autowired val videoPost: PostBoundary.IVideoPost,
        @Autowired val multipleImagesPost: PostBoundary.IMultipleImagesPosts,
        @Autowired val updateComments: PostBoundary.IUpdateListOfComments,
        @Autowired val updateDocument: PostBoundary.IUpdateDocument,
        @Autowired val updateRating: PostBoundary.IUpdateRating,
        @Autowired val deletePosts: PostBoundary.IDeletePost,
        @Autowired val deleteCommentPost: PostBoundary.IDeleteCommentPost,
        @Autowired val getPostById: PostBoundary.IGetPostById,
        @Autowired val getPostsByDescriptions: PostBoundary.IGetPostsByDescription
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
                            null,
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
                            null,
                            questions
                    ))
                }
                "multipleImages" -> {
                    val multipleImages = body["imagesData"] as List<HashMap<String,Any?>>
                    val images = multipleImages.map {
                        PostImage(
                                it["imageTitle"] as String? ?: throw ValidationException(),
                                null,
                                it["imageData"] as String? ?: throw ValidationException()
                        )
                    }
                    multipleImagesPost.run(PostSubmission(
                            userId,
                            type,
                            description,
                            null,
                            null,
                            images,
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
            val multipleDocuments = body["documentsData"] as List<HashMap<String,Any?>>
            val documents = multipleDocuments.map {
                PostDocumentSubmission(
                        it["documentTitle"] as String? ?: throw ValidationException(),
                        it["documentData"] as String? ?: throw ValidationException()
                )
            }
            updateDocument.run(postId,authenticatedUserId,documents)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId)
            )
        }
    }

    @PutMapping("/{postId}/rating")
    fun updatePostRating(
            @PathVariable("postId") postId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "put_post_likes"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, RATING,UPDATE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            updateRating.run(postId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId)
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

    @DeleteMapping("/{postId}/{commentId}")
    fun deleteComment(
            @RequestAttribute("userId") authenticatedUserId: String,
            @PathVariable("postId") postId: String,
            @PathVariable("commentId") commentId: String,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "delete_comment_post"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, COMMENT,DELETE)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            deleteCommentPost.run(postId, commentId,authenticatedUserId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("postId" to postId) +
                    ("userId" to authenticatedUserId) +
                    ("commentId" to commentId)
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

    @GetMapping
    fun getPostsByDescriptions(
            @RequestAttribute("userId") authenticatedUserId: String,
            @RequestParam("description") description: String,
            @RequestParam("amount",defaultValue = "15") amount: Int,
            request: HttpServletRequest
    ): ResponseEntity<List<PostResponse>> {
        val eventName = "get_post_by_id"
        val eventTags = listOf(CONTROLLER, NETWORK, POST, ID, RETRIEVAL)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val postsRetrieved = getPostsByDescriptions.run(description,amount)
            ResponseEntity(postsRetrieved.map { toPostResponse(it) },HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("userId" to authenticatedUserId) +
                    ("description" to description) +
                    ("amount" to amount)
            )
        }
    }

}