package com.liceu.server.domain.post

import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.util.Logging
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
            if(post.description.length > 3000){
                throw OverflowSizeException ("Description is too long")
            }
            if(post.description.length < 100){
                throw UnderflowSizeException ("Description is too short")
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
                retrieveActualTimeStamp(),
                null,
                post.questions
            ))
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}