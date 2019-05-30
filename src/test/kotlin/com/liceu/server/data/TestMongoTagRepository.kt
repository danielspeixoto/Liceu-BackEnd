package com.liceu.server.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var repo: TagRepository

    @BeforeEach
    fun dataSetup() {
        val item1 = MongoTagRepository.MongoTag(
                "primeira",
                0
        )
        item1.id = "id1"
        repo.insert(item1)
        val item2 = MongoTagRepository.MongoTag(
                "segunda",
                10
        )
        item2.id = "id2"
        repo.insert(item2)
        val item3 = MongoTagRepository.MongoTag(
                "terceira",
                5
        )
        item3.id = "id3"
        repo.insert(item3)
    }

    @AfterEach
    fun destroy() {
        repo.deleteAll()
    }

    @Test
    fun incrementCount() {
        data.toString()
//        data.incrementCount("primeira")
        val item = repo.findById("id1").get()

        assertThat(item.amount).isEqualTo(1)
    }
}