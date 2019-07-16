package com.liceu.server.domain.user

import com.liceu.server.domain.game.GameRanking
import com.liceu.server.domain.global.ID
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.global.USER
import com.liceu.server.util.Logging
import java.lang.Exception

class UserById(val repo: UserBoundary.IRepository): UserBoundary.IUserById {

    companion object {
        const val EVENT_NAME = "user_from_id"
        val TAGS = listOf(RETRIEVAL, USER, ID)
    }

    override fun run(userId: String): User {
        try {
            val user = repo.getUserById(userId)
            Logging.info(
                    UserById.EVENT_NAME,
                    UserById.TAGS,
                    hashMapOf(
                            "userId" to userId
                    )
            )
            return user
        } catch (e: Exception) {
            Logging.error(EVENT_NAME,TAGS, e)
            throw e
        }

    }
}