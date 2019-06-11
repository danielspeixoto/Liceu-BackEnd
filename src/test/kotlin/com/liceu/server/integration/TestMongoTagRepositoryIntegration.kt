package com.liceu.server.integration

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoTagRepository
import com.liceu.server.data.TagRepository
import com.liceu.server.domain.global.TagNotFoundException
import com.liceu.server.domain.tag.Tag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoTagRepositoryIntegration {

    @Autowired
    lateinit var data: MongoTagRepository

    @Autowired
    lateinit var tagRepo: TagRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun incrementCount_tagExists_shouldSucceed() {
        data class Param(
                val name: String,
                val id: String,
                val amount: Int
        )

        val params = listOf(
                Param("primeira", DataSetup.TAG_ID_1, 2),
                Param("segunda", DataSetup.TAG_ID_2, 3),
                Param("terceira", DataSetup.TAG_ID_3, 1)
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
        assertThat(results).containsExactly(DataSetup.TAG_ID_3, DataSetup.TAG_ID_2, DataSetup.TAG_ID_1)
    }

    @Test
    fun suggestions_matchesFew_ReturnsThem() {
        val results = data.suggestions("eira", 0).map { it.id }
        assertThat(results).containsExactly(DataSetup.TAG_ID_3, DataSetup.TAG_ID_1)
    }

    @Test
    fun suggestions_matchesFewAndFiltersByAmount_ReturnsAboveAmount() {
        val results = data.suggestions("eira", 1).map { it.id }
        assertThat(results).containsExactly(DataSetup.TAG_ID_1)
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
                DataSetup.TAG_ID_1,
                "primeira",
                1
        )

        EqualsTester().addEqualityGroup(results[0], tag).testEquals()
    }
}