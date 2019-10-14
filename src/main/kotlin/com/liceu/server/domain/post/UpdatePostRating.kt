package com.liceu.server.domain.post

import com.liceu.server.domain.global.POST
import com.liceu.server.domain.global.RATING
import com.liceu.server.domain.global.UPDATE
import com.liceu.server.domain.trivia.UpdateRating
import com.liceu.server.util.Logging

class UpdatePostRating(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.IUpdateRating {

    companion object {
        const val EVENT_NAME = "post_rating_update"
        val TAGS = listOf(UPDATE, POST, RATING)
    }

    override fun run(postId: String) {
        try {
            Logging.info(UpdateRating.EVENT_NAME, UpdateRating.TAGS, hashMapOf(
                    "postId" to postId
            ))
            postRepository.updateLike(postId)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}