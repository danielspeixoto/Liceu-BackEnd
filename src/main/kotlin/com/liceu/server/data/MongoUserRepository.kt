package com.liceu.server.data

import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import org.springframework.data.mongodb.core.MongoTemplate

class MongoUserRepository(
        val template: MongoTemplate
) : UserBoundary.IRepository {

    override fun save(user: UserForm): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}