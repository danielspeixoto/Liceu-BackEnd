package com.liceu.server.data

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.global.TagNotFoundException
import com.liceu.server.domain.tag.Tag
import com.liceu.server.util.TAG_ID_1
import com.liceu.server.util.TAG_ID_2
import com.liceu.server.util.TAG_ID_3
import com.liceu.server.util.setup
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension

@ComponentScan
@ExtendWith(SpringExtension::class)
@DataMongoTest
class TestMongoTagRepository {

    @Autowired
    lateinit var data: MongoTagRepository
    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository
    @Autowired
    lateinit var tagRepo: TagRepository
    @BeforeEach
    fun dataSetup() {
        setup(questionRepo, videoRepo, tagRepo)
    }

    @Test
    fun incrementCount_tagExists_shouldSucceed() {
        data class Param(
                val name: String,
                val id: String,
                val amount: Int
        )

        val params = listOf(
                Param("primeira", TAG_ID_1, 2),
                Param("segunda", TAG_ID_2, 3),
                Param("terceira", TAG_ID_3, 1)
        )
        params.forEach {
            data.incrementCount(it.name)
            val item = tagRepo.findById(it.id).get()

            assertThat(item.amount).isEqualTo(it.amount)
        }
    }

    @Test
    fun incrementCount_tagDoesntExists_throwsErrors() {
        assertThrows<TagNotFoundException> {
            data.incrementCount("id0")
        }
    }

    @Test
    fun suggestions_emptyString_ReturnsAll() {
        val results = data.suggestions("", 0).map { it.id }
        assertThat(results).containsExactly(TAG_ID_3, TAG_ID_2, TAG_ID_1)
    }

    @Test
    fun suggestions_matchesFew_ReturnsThem() {
        val results = data.suggestions("eira", 0).map { it.id }
        assertThat(results).containsExactly(TAG_ID_3, TAG_ID_1)
    }

    @Test
    fun suggestions_matchesFewAndFiltersByAmount_ReturnsAboveAmount() {
        val results = data.suggestions("eira", 1).map { it.id }
        assertThat(results).containsExactly(TAG_ID_1)
    }

    @Test
    fun suggestions_matchesNone_ReturnsNothing() {
        val results = data.suggestions("nada", 0)
        assertThat(results).isEmpty()
    }

    @Test
    fun suggestions_matchesOne_DataIsValid() {
        val results = data.suggestions("primeira", 0)

        val tag = Tag(
                TAG_ID_1,
                "primeira",
                1
        )

        EqualsTester().addEqualityGroup(results[0], tag).testEquals()
    }
}