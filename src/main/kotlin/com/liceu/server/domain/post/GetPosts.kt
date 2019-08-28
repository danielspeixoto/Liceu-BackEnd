package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.util.Logging
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

class GetPosts(
        private val postRepository: MongoPostRepository,
        private val userRepository: MongoUserRepository
): PostBoundary.IGetPosts {

    companion object{
        const val EVENT_NAME = "get_posts"
        val TAGS = listOf(RETRIEVAL,POST)
    }
    override fun run(userId: String, date: Date, amount: Int): List<Post> {
        try{
            val user = userRepository.getUserById(userId)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "dateRequired" to date,
                    "amount" to amount
            ))
            return postRepository.getPosts(user,date,amount)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}