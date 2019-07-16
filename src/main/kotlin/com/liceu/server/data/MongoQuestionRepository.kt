package com.liceu.server.data

import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.video.Video
import org.apache.tomcat.util.codec.binary.Base64
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class MongoQuestionRepository(
        val template: MongoTemplate
) : QuestionBoundary.IRepository {

    override fun randomByTags(tags: List<String>, amount: Int): List<Question> {
        if(amount == 0) {
            return emptyList()
        }
        var match = Aggregation.match(Criteria("tags").all(tags))
        if(tags.isEmpty()) {
           match = Aggregation.match(Criteria())
        }
        val sample = Aggregation.sample(amount.toLong())
        val agg = Aggregation.newAggregation(match, sample)

        val results = template.aggregate(agg, MongoDatabase.QUESTION_COLLECTION, MongoDatabase.MongoQuestion::class.java)
        return results.map {
            Question(
                    it.id.toHexString(),
                    it.source,
                    it.variant,
                    it.edition,
                    it.number,
                    it.domain,
                    it.answer,
                    it.tags,
                    it.itemCode,
                    it.referenceId,
                    it.stage,
                    it.width,
                    it.height,
                    it.imageURL
            )
        }
    }

    override fun videos(id: String, start: Int, count: Int): List<Video> {
        val match = Aggregation.match(Criteria("questionId").isEqualTo(ObjectId(id)))
        val sort = Aggregation.sort(Sort.Direction.ASC, "retrievalPosition")
        val skip = Aggregation.skip(start.toLong())
        val limit = Aggregation.limit(count.toLong())
        val agg = Aggregation.newAggregation(match, sort, skip, limit)

        val results = template.aggregate(agg, MongoDatabase.VIDEO_COLLECTION, MongoDatabase.MongoVideo::class.java)
        return results.map {
            Video(
                    it.id.toHexString(),
                    it.title,
                    it.description,
                    it.videoId,
                    it.questionId.toHexString(),
                    it.aspectRation?: 1.33F,
                    it.thumbnails.default,
                    it.channel.title
            )
        }
    }

    override fun getQuestionById(questionId: String): Question {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(questionId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.QUESTION_COLLECTION, MongoDatabase.MongoQuestion::class.java)
        val questionRetrieved = results.map {
            Question(
                    it.id.toHexString(),
                    it.source,
                    it.variant,
                    it.edition,
                    it.number,
                    it.domain,
                    it.answer,
                    it.tags,
                    it.itemCode,
                    it.referenceId,
                    it.stage,
                    it.width,
                    it.height,
                    it.imageURL
            )
        }
        if(questionRetrieved.isNotEmpty()){
            return questionRetrieved[0]
        }else{
            throw ItemNotFoundException()
        }
    }
}