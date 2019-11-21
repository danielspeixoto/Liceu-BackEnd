package com.liceu.server.data

import com.liceu.server.domain.activities.Activity
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

class MongoActivityRepository(
        private val template: MongoTemplate
): ActivityBoundary.IRepository {

    override fun insertActivity(activityToInsert: ActivityToInsert): String {
        val result = template.insert(MongoDatabase.MongoActivities(
                ObjectId(activityToInsert.userId),
                activityToInsert.type,
                activityToInsert.params,
                activityToInsert.submissionDate
        ))
        return result.id.toHexString()
    }

    override fun getActivitiesFromUser(userId: String, amount: Int, tags: List<String>, start: Int): List<Activity> {
        if(amount == 0) {
            return emptyList()
        }
        var match = Aggregation.match(Criteria("type").`in`(tags) .and("userId").isEqualTo(ObjectId(userId)))
        if(tags.isEmpty()) {
            match = Aggregation.match(Criteria.where("userId").isEqualTo(ObjectId(userId)))
        }
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val limitOfActivitiesRetrieved = Aggregation.limit(amount.toLong())
        val skip = Aggregation.skip(start.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,skip,limitOfActivitiesRetrieved)
        val results = template.aggregate(agg,MongoDatabase.ACTIVITIES_COLLECTION,MongoDatabase.MongoActivities::class.java)
        return results.map {
            Activity(
                    it.id.toHexString(),
                    it.userId.toHexString(),
                    it.type,
                    it.params,
                    it.submissionDate
            )
        }
    }
}