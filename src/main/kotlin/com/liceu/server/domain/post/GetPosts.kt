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
    override fun run(userId: String, day: Int, month: Int, year: Int, amount: Int): List<Post> {
        try{
            val user = userRepository.getUserById(userId)
            val date = LocalDate.of(year, month, day)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "dateRequired" to date,
                    "amount" to amount
            ))
            //postRepository.getPosts(user,)
        return listOf(Post(
                "123",
                "text",
                "123132",
                null,
                null,
                Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant())
        ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}