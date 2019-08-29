package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class VideoPost(
        private val postRepository: MongoPostRepository
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
            if(post.description.length > 800){
                throw OverflowSizeException ("Description is too long")
            }
            if(post.video?.videoUrl?.isEmpty()!!){
                throw OverflowSizeException ("Video URl can't be null")
            }
            if(post.video.videoUrl.length > 250){
                throw OverflowSizeException ("Video URL is too long")
            }
            if(post.video.thumbnails?.high?.isEmpty()!! or post.video.thumbnails?.default?.isEmpty()!! or post.video.thumbnails?.medium?.isEmpty()!!){
                throw OverflowSizeException ("None of the thumbnails can be null")
            }
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
                                    post.video.thumbnails!!.high,
                                    post.video.thumbnails!!.default,
                                    post.video.thumbnails!!.medium
                            )
                    ),
                    Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant()),
                    null
            ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }    }
}
