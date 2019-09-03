package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.global.TEXT
import com.liceu.server.util.Logging
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class TextPost(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.ITextPost {

    companion object{
        const val EVENT_NAME = "text_post_submission"
        val TAGS = listOf(INSERTION,TEXT,POST)
    }
    override fun run(post: PostSubmission): String {
        try {
            if(post.description.isEmpty()){
                throw OverflowSizeException ("Description can't be null")
            }
            if(post.description.length > 800){
                throw OverflowSizeException ("Description is too long")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to post.userId,
                    "type" to post.type,
                    "description" to post.description
            ))
            return postRepository.insertPost(PostToInsert(
                post.userId,
                post.type,
                post.description,
                null,
                post.video,
                Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant()),
                null
                ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}