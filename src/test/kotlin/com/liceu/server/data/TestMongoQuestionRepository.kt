package com.liceu.server.data

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.domain.exception.AlreadyExistsException
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.video.Video
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception

@ComponentScan
@ExtendWith(SpringExtension::class)
@DataMongoTest
class TestMongoQuestionRepository {

    @Autowired
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository

    @BeforeEach
    fun dataSetup() {
        val q1 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2017,
                3,
                "matemática",
                1,
                listOf("primeira", "segunda"),
                "12345",
                "referenceId",
                2,
                100,
                200
        )
        q1.id = "id1"
        questionRepo.insert(q1)
        val q2 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2016,
                5,
                "linguagens",
                1,
                listOf("segunda"),
                "54321",
                "referenceId2",
                2,
                100,
                200
        )
        q2.id = "id2"
        questionRepo.insert(q2)
        val q3 = MongoDatabase.MongoQuestion(
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AZUL",
                2015,
                15,
                "linguagens",
                1,
                listOf(),
                "54321",
                "referenceId3",
                1,
                100,
                200
        )
        q3.id = "id3"
        questionRepo.insert(q3)

        val item1 = MongoDatabase.MongoVideo(
                "primeira",
                "primeiro video",
                "videoId1",
                "id1",
                1.1f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                3
        )
        item1.id = "id1"
        videoRepo.insert(item1)
        val item2 = MongoDatabase.MongoVideo(
                "segundo",
                "segundo video",
                "videoId3",
                "id2",
                1.1f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                2
        )
        item2.id = "id2"
        videoRepo.insert(item2)
        val item3 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                "id1",
                1.3f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "channelTitle",
                        "channelId"
                ),
                1
        )
        item3.id = "id3"
        videoRepo.insert(item3)
    }

    @AfterEach
    fun destroy() {
        questionRepo.deleteAll()
        videoRepo.deleteAll()
    }

    @Test
    fun addTag_hasDifferentTags_adds() {
        data.addTag("id1", "terceira")

        assertThat(questionRepo.findById("id1").get().tags)
                .containsExactly("primeira", "segunda", "terceira")
                .inOrder()
    }

    @Test
    fun addTag_hasNoTags_adds() {
        data.addTag("id3", "terceira")

        assertThat(questionRepo.findById("id3").get().tags)
                .containsExactly("terceira")
                .inOrder()
    }

    @Test
    fun addTag_alreadyHasTag_throwsError() {
        try {
            data.addTag("id1", "primeira")
            fail("should throw exception")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(AlreadyExistsException::class.java)
        }

        assertThat(questionRepo.findById("id1").get().tags)
                .containsExactly("primeira", "segunda")
                .inOrder()
    }

    @Test
    fun addTag_nonExistentQuestion_throwsError() {
        try {
            data.addTag("id0", "primeira")
            fail("should throw exception")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(ItemNotFoundException::class.java)
        }
    }

    @Test
    fun randomByTags_requestMoreThanExists_returnsExistent() {
        val questions = data.randomByTags(listOf("segunda"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly("id1", "id2")
    }

    @Test
    fun randomByTags_requestLessThanExists_returnsExistentRandomly() {
        val ids = arrayListOf<String>()
        var i = 0
        while (i < 10) {
            val questions = data.randomByTags(listOf("segunda"), 1)
            assertThat(questions.size).isEqualTo(1)
            assertThat(questions[0].id).matches("id[12]")
            ids.add(questions[0].id)
            i++
        }
        assertThat(ids).containsAtLeast("id1", "id2")
    }

    @Test
    fun randomByTags_notEveryoneHasTag_filters() {
        val questions = data.randomByTags(listOf("primeira"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly("id1")
    }

    @Test
    fun randomByTags_noOneHasTag_empty() {
        val questions = data.randomByTags(listOf("ultima"), 10)
        assertThat(questions).isEmpty()
    }

    @Test
    fun randomByTags_correctRequest_dataIsValid() {
        val result = data.randomByTags(listOf("primeira"), 10)[0]

        val question = Question(
                "id1",
                listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                "ENEM",
                "AMARELA",
                2017,
                3,
                "matemática",
                1,
                listOf("primeira", "segunda"),
                "12345",
                "referenceId",
                2,
                100,
                200
        )

        EqualsTester()
                .addEqualityGroup(question, result)
                .testEquals()
    }

    @Test
    fun videos_HasRelatedVideos_ReturnsThemOrdered() {
        val videos = data.videos("id1", 0, 10).map { it.id }
        assertThat(videos).containsExactly("id3", "id1")
    }

    @Test
    fun videos_NoRelatedVideos_ReturnsEmpty() {
        val videos = data.videos("id0", 0, 10).map { it.id }
        assertThat(videos).isEmpty()
    }

    @Test
    fun videos_CountEqualsOne_ReturnsFirst() {
        val videos = data.videos("id1", 0, 1).map { it.id }
        assertThat(videos).containsExactly("id1")
    }

    @Test
    fun videos_StartEqualsOne_SkipsFirst() {
        val videos = data.videos("id1", 1, 10).map { it.id }
        assertThat(videos).containsExactly("id3")
    }

    @Test
    fun videos_ValidRequest_DataIsValid() {
        val result = data.videos("id1", 0, 1)[0]
        val video = Video(
                "id1",
                "primeira",
                "primeiro video",
                "videoId1",
                "id1",
                1.1f,
                "defaultQuality",
                "channelTitle"
        )

        EqualsTester().addEqualityGroup(result, video).testEquals()
    }
}