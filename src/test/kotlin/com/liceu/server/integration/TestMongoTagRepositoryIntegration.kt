//package com.liceu.server.data
//
//import com.google.common.testing.EqualsTester
//import com.google.common.truth.Truth.assertThat
//import com.liceu.server.domain.global.QuestionNotFoundException
//import com.liceu.server.domain.tag.Tag
//import org.junit.jupiter.api.*
//import org.junit.jupiter.api.extension.ExtendWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.test.context.junit.jupiter.SpringExtension
//
//@ComponentScan
//@ExtendWith(SpringExtension::class)
//@DataMongoTest
//class TestMongoTagRepositoryIntegration {
//
//    @Autowired
//    lateinit var data: MongoTagRepository
//    @Autowired
//    lateinit var repo: TagRepository
//
//    @BeforeEach
//    fun dataSetup() {
//        val item1 = MongoDatabase.MongoTag(
//                "primeira",
//                0
//        )
//        item1.id = "id1"
//        repo.insert(item1)
//        val item2 = MongoDatabase.MongoTag(
//                "segunda",
//                10
//        )
//        item2.id = "id2"
//        repo.insert(item2)
//        val item3 = MongoDatabase.MongoTag(
//                "terceira",
//                5
//        )
//        item3.id = "id3"
//        repo.insert(item3)
//    }
//
//    @AfterEach
//    fun destroy() {
//        repo.deleteAll()
//    }
//
//
//    @Test
//    fun incrementCount_tagExists_shouldSucceed() {
//        data class Param(
//                val name: String,
//                val id: String,
//                val amount: Int
//        )
//
//        val params = listOf(
//                Param("primeira", "id1", 1),
//                Param("segunda", "id2", 11),
//                Param("terceira", "id3", 6)
//        )
//        params.forEach {
//            data.incrementCount(it.name)
//            val item = repo.findById(it.id).get()
//
//            assertThat(item.amount).isEqualTo(it.amount)
//        }
//    }
//
//    @Test
//    fun incrementCount_tagDoesntExists_throwsErrors() {
//        assertThrows<QuestionNotFoundException> {
//            data.incrementCount("id0")
//        }
//    }
//
//    @Test
//    fun suggestions_emptyString_ReturnsAll() {
//        val results = data.suggestions("", 0).map { it.id }
//        assertThat(results).containsExactly("id1", "id2", "id3")
//    }
//
//    @Test
//    fun suggestions_matchesFew_ReturnsThem() {
//        val results = data.suggestions("eira", 0).map { it.id }
//        assertThat(results).containsExactly("id1", "id3")
//    }
//
//    @Test
//    fun suggestions_matchesFewAndFiltersByAmount_ReturnsAboveAmount() {
//        val results = data.suggestions("eira", 3).map { it.id }
//        assertThat(results).containsExactly("id3")
//    }
//
//    @Test
//    fun suggestions_matchesNone_ReturnsNothing() {
//        val results = data.suggestions("nada", 0)
//        assertThat(results).isEmpty()
//    }
//
//    @Test
//    fun suggestions_matchesOne_DataIsValid() {
//        val results = data.suggestions("primeira", 0)
//
//        val tag = Tag(
//                "id1",
//                "primeira",
//                0
//        )
//
//        EqualsTester().addEqualityGroup(results[0], tag).testEquals()
//    }
//}