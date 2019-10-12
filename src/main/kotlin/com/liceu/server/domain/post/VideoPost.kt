package com.liceu.server.domain.post

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging
import org.springframework.web.util.UriComponentsBuilder
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.domain.util.postsFunctions.postsAutomaticApproval


class VideoPost(
        private val postRepository: PostBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository,
        private val postsMinimumApproval: Int
): PostBoundary.IVideoPost {

    companion object{
        const val EVENT_NAME = "video_post_submission"
        val TAGS = listOf(INSERTION, VIDEO, POST)
    }

    override fun run(post: PostSubmission): String {
        try {
            if(post.description.isEmpty()){
                throw OverflowSizeException ("Description can't be null")
            }
            if(post.description.length > 3000){
                throw OverflowSizeException ("Description is too long")
            }
            if(post.description.length < 100){
                throw UnderflowSizeException ("Description is too short")
            }
            if(post.video?.videoUrl?.isEmpty()!!){
                throw OverflowSizeException ("Video URl can't be null")
            }
            if(post.video.videoUrl.length > 250){
                throw OverflowSizeException ("Video URL is too long")
            }
            val queryParams = UriComponentsBuilder.fromUriString(post.video.videoUrl).build().queryParams
            if(queryParams.size == 0){
                throw OverflowSizeException("It's necessary have parameters in video Url")
            }
            val defaultUrl = "http://i.ytimg.com/vi/"
            val highThumbails = defaultUrl+ queryParams["v"]!![0] + "/hqdefault.jpg"
            val defaultThumbails = defaultUrl+ queryParams["v"]!![0] + "/default.jpg"
            val mediumThumbails = defaultUrl+ queryParams["v"]!![0] + "/mqdefault.jpg"
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to post.userId,
                    "type" to post.type,
                    "description" to post.description,
                    "videoURL" to post.video.videoUrl
            ))
            return postRepository.insertPost(PostToInsert(
                    post.userId,
                    post.type,
                    post.description,
                    null,
                    PostVideo(
                            post.video.videoUrl,
                            PostThumbnails(
                                    highThumbails,
                                    defaultThumbails,
                                    mediumThumbails
                            )
                    ),
                    retrieveActualTimeStamp(),
                    null,
                    post.questions,
                    postsAutomaticApproval(postRepository,userRepository,post.userId,postsMinimumApproval)
            ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }    }
}
