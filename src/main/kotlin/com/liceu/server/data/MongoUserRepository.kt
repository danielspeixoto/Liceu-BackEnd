package com.liceu.server.data

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.user.User
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class MongoUserRepository(
        val template: MongoTemplate
) : UserBoundary.IRepository {

    override fun save(user: UserForm): String {
        val query = Query(Criteria("email").isEqualTo(user.email))
        val mongoUser = MongoDatabase.MongoUser(
                user.name, user.email,
                MongoDatabase.MongoPicture(
                        user.picture.url,
                        user.picture.width,
                        user.picture.height
                ),
                user.facebookId
        )
        val user = template.findOne<MongoDatabase.MongoUser>(query)
        if(user != null) {
            mongoUser.id = user.id
        }
        return template.save(mongoUser).id.toHexString()
    }

    override fun getUserById(userId: String): User {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(userId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.USER_COLLECTION, MongoDatabase.MongoUser::class.java)
        val userRetrieved = results.map {
            User(
                    it.id.toHexString(),
                    it.name,
                    it.email,
                    Picture(
                            it.picture.url,
                            it.picture.width,
                            it.picture.height
                    )
            )
        }
        if(userRetrieved.isNotEmpty()){
            return userRetrieved[0]
        }else{
            throw ItemNotFoundException()
        }
    }
}
