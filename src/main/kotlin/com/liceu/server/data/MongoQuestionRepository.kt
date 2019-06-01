package com.liceu.server.data

import com.liceu.server.domain.global.AlreadyExistsException
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.video.Video
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class MongoQuestionRepository(
        val template: MongoTemplate
) : QuestionBoundary.IRepository {

    @Autowired lateinit var repo: QuestionRepository

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
                    it.id,
                    it.view,
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
                    it.height
            )
        }
    }

    override fun addTag(id: String, tag: String) {
        val result = repo.findById(id)
        if (!result.isPresent) {
            throw ItemNotFoundException()
        }
        val doc = result.get()
        if (doc.tags.contains(tag)) {
            throw AlreadyExistsException()
        }
        val newTags = arrayListOf<String>()
        doc.tags.forEach {
            newTags.add(it)
        }
        newTags.add(tag)
        doc.tags = newTags
        repo.save(doc)
    }

    override fun videos(id: String, start: Int, count: Int): List<Video> {
        val match = Aggregation.match(Criteria("questionId").isEqualTo(id))
        val sort = Aggregation.sort(Sort.Direction.DESC, "retrievalPosition")
        val skip = Aggregation.skip(start.toLong())
        val limit = Aggregation.limit(count.toLong())
        val agg = Aggregation.newAggregation(match, sort, skip, limit)

        val results = template.aggregate(agg, MongoDatabase.VIDEO_COLLECTION, MongoDatabase.MongoVideo::class.java)
        return results.map {
            Video(
                    it.id,
                    it.title,
                    it.description,
                    it.videoId,
                    it.questionId,
                    it.aspectRation,
                    it.thumbnails.default,
                    it.channel.title
            )
        }
    }
}