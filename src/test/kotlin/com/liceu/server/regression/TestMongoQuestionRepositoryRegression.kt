package com.liceu.server.regression

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.*
import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ComponentScan(basePackages = ["com.liceu.server"])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("staging")
@DataMongoTest
class TestMongoQuestionRepositoryRegression {

    @Autowired
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var template: MongoTemplate
    @Autowired
    lateinit var questionRepo: QuestionRepository

    val sampleAmount = 200L
    var questionsList = listOf<MongoDatabase.MongoQuestion>()

    @BeforeEach
    fun dataSetup() {
        val match = Aggregation.match(Criteria())
        val sample = Aggregation.sample(sampleAmount)
        val agg = Aggregation.newAggregation(match, sample)

        questionsList = template.aggregate<MongoDatabase.MongoQuestion>(agg, MongoDatabase.QUESTION_COLLECTION)
                .mappedResults.toList()
    }

    @Test
    fun addTag_hasDifferentTags_adds() {
        val tagToAdd = System.currentTimeMillis().toString()
        questionsList.forEach {
            val expectedTags = it.tags + listOf(tagToAdd)

            data.addTag(it.id.toHexString(), tagToAdd)

            assertThat(questionRepo.findById(it.id.toHexString()).get().tags)
                    .containsExactlyElementsIn(expectedTags)
                    .inOrder()
        }
    }

    @Test
    fun addTag_alreadyHasTag_throwsError() {
        questionsList.forEach {
            val questionTag = it.tags[0]
            assertThrows<TagAlreadyExistsException> {
                data.addTag(it.id.toHexString(), questionTag)
            }
            assertThat(questionRepo.findById(it.id.toHexString()).get().tags)
                    .containsExactlyElementsIn(it.tags)
                    .inOrder()
        }
    }

    @Test
    fun addTag_nonExistentQuestion_throwsError() {
        assertThrows<QuestionNotFoundException> {
            data.addTag("id0", "primeira")
        }
    }

    @Test
    fun randomByTags_atLeastOneQuestionHasTag_ReturnsAtLeastOne() {
        questionsList.forEach { question ->
            val questions = data.randomByTags(question.tags, 1)
            assertThat(questions.size).isEqualTo(1)
        }
    }

    @Test
    fun randomByTags_noOneHasTag_empty() {
        val questions = data.randomByTags(listOf(System.currentTimeMillis().toString()), 10)
        assertThat(questions).isEmpty()
    }

    @Test
    fun videos_HasRelatedVideos_ReturnsThem() {
        val acceptedNoVideosRatio = 0.5
        val maxHasVideosRatio = 0.9

        var hasVideosCount = 0.0
        questionsList.forEach {
            val videos = data.videos(it.id.toHexString(), 0, 1)
            if(videos.isNotEmpty()) {
                hasVideosCount++
            }
        }
        assertThat(hasVideosCount).isGreaterThan(acceptedNoVideosRatio * sampleAmount)
        assertThat(hasVideosCount).isLessThan(maxHasVideosRatio * sampleAmount)
    }
}