package com.liceu.server.domain.trivia

import com.liceu.server.data.MongoTriviaRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.COMMENT
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.TRIVIA
import com.liceu.server.domain.global.UPDATE
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging

class UpdateCommentsTrivia(
        private val triviaRepository: TriviaBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository
): TriviaBoundary.IUpdateListOfComments {
    companion object {
        const val EVENT_NAME = "trivia_question_comment_update"
        val TAGS = listOf(UPDATE, TRIVIA, COMMENT)
    }
    override fun run(questionId: String, userId: String, comment: String) {
        try {
            if(comment.isBlank()){
                throw OverflowSizeException("Comment can't be null")
            }
            if(comment.length > 300){
                throw OverflowSizeException("Comment is too long")
            }
            if(userId.isBlank()){
                throw OverflowSizeException("userId can't be null")
            }
            val authorComment = userRepository.getUserById(userId).name
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "questionId" to questionId,
                    "author" to authorComment,
                    "comment" to comment
            ))
            triviaRepository.updateListOfComments(questionId,userId,authorComment,comment)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }    }
}