package com.liceu.server.data

import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository

@Repository
class MongoUserRepository(
        val template: MongoTemplate
) : UserBoundary.IRepository {

    override fun save(user: UserForm): String {
        val query = Query(Criteria("email").isEqualTo(user.email))
        val userExists = template.exists<MongoDatabase.MongoUser>(
                query
        )
        val mongoUser = MongoDatabase.MongoUser(
                user.name, user.email,
                MongoDatabase.MongoPicture(
                        user.picture.url,
                        user.picture.width,
                        user.picture.height
                ),
                user.facebookId
        )
        return if (!userExists) {
            template.insert(mongoUser).id.toHexString()
        } else {
            template.updateFirst<MongoDatabase.MongoUser>(query,
                    Update.update("name", user.name)
                            .set("email", user.email)
                            .set("picture", user.picture)
                            .set("facebookId", user.facebookId)
            ).upsertedId.toString()
        }
    }

}