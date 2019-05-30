package com.liceu.server.data

import com.liceu.server.domain.exception.AlreadyExistsException
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import java.lang.Exception

@Repository
class MongoQuestionRepository(
        @Autowired val repo: QuestionRepository,
        @Autowired val template: MongoTemplate
) : QuestionBoundary.IRepository {

    companion object {
        const val COLLECTION_NAME = "questions"
    }

    override fun randomByTags(tags: List<String>, amount: Int): List<Question> {
        val match = Aggregation.match(Criteria("tags").all(tags))
        val sample = Aggregation.sample(amount.toLong())
        val agg = Aggregation.newAggregation(match, sample)

        val results = template.aggregate(agg, COLLECTION_NAME, MongoQuestion::class.java)
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

    @Document(collection = "questions")
    data class MongoQuestion(
            var view: List<Byte>,
            var source: String,
            var variant: String,
            var edition: Int,
            var number: Int,
            var domain: String,
            var answer: Int,
            var tags: List<String>,
            var itemCode: String,
            var referenceId: String,
            var stage: Int,
            var width: Int,
            var height: Int
    ) {
        @Id
        lateinit var id: String
    }
}