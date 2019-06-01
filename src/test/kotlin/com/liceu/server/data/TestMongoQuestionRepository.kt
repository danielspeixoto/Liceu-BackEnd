package com.liceu.server.data

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.video.Video
import com.liceu.server.util.*
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
    @Autowired
    lateinit var tagRepo: TagRepository

    @BeforeEach
    fun dataSetup() {
        setup(questionRepo, videoRepo, tagRepo)
    }

    @Test
    fun addTag_hasDifferentTags_adds() {
        data.addTag(QUESTION_ID_1, "terceira")

        assertThat(questionRepo.findById(QUESTION_ID_1).get().tags)
                .containsExactly("primeira", "segunda", "terceira")
                .inOrder()
    }

    @Test
    fun addTag_hasNoTags_adds() {
        data.addTag(QUESTION_ID_3, "terceira")

        assertThat(questionRepo.findById(QUESTION_ID_3).get().tags)
                .containsExactly("terceira")
                .inOrder()
    }

    @Test
    fun addTag_alreadyHasTag_throwsError() {
        try {
            data.addTag(QUESTION_ID_1, "primeira")
            fail("should throw global")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TagAlreadyExistsException::class.java)
        }

        assertThat(questionRepo.findById(QUESTION_ID_1).get().tags)
                .containsExactly("primeira", "segunda")
                .inOrder()
    }

    @Test
    fun addTag_nonExistentQuestion_throwsError() {
        try {
            data.addTag("id0", "primeira")
            fail("should throw global")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(QuestionNotFoundException::class.java)
        }
    }

    @Test
    fun randomByTags_requestMoreThanExists_returnsExistent() {
        val questions = data.randomByTags(listOf("segunda"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(QUESTION_ID_1, QUESTION_ID_2)
    }

    @Test
    fun randomByTags_emptyTags_returnsAll() {
        val questions = data.randomByTags(listOf(), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(QUESTION_ID_1, QUESTION_ID_2, QUESTION_ID_3)
    }

    @Test
    fun randomByTags_requestLessThanExists_returnsExistentRandomly() {
        val ids = arrayListOf<String>()
        var i = 0
        while (i < 10) {
            val questions = data.randomByTags(listOf("segunda"), 1)
            assertThat(questions.size).isEqualTo(1)
            assertThat(questions[0].id).isIn(listOf(QUESTION_ID_1, QUESTION_ID_2))
            ids.add(questions[0].id)
            i++
        }
        assertThat(ids).containsAtLeast(QUESTION_ID_1, QUESTION_ID_2)
    }

    @Test
    fun randomByTags_notEveryoneHasTag_filters() {
        val questions = data.randomByTags(listOf("primeira"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(QUESTION_ID_1)
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
                QUESTION_ID_1,
                "YWI=",
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
        val videos = data.videos(QUESTION_ID_1, 0, 10).map { it.id }
        assertThat(videos).containsExactly(VIDEO_ID_3, VIDEO_ID_1).inOrder()
    }

    @Test
    fun videos_NoRelatedVideos_ReturnsEmpty() {
        val videos = data.videos("99c54d325b75357a571d4cc2", 0, 10).map { it.id }
        assertThat(videos).isEmpty()
    }

    @Test
    fun videos_CountEqualsOne_ReturnsFirst() {
        val videos = data.videos(QUESTION_ID_1, 0, 1).map { it.id }
        assertThat(videos).containsExactly(VIDEO_ID_3)
    }

    @Test
    fun videos_StartEqualsOne_SkipsFirst() {
        val videos = data.videos(QUESTION_ID_1, 1, 10).map { it.id }
        assertThat(videos).containsExactly(VIDEO_ID_1)
    }

    @Test
    fun videos_ValidRequest_DataIsValid() {
        val result = data.videos(QUESTION_ID_1, 1, 1)[0]
        val video = Video(
                VIDEO_ID_1,
                "primeira",
                "primeiro video",
                "videoId1",
                QUESTION_ID_1,
                1.1f,
                "defaultQuality",
                "channelTitle"
        )

        EqualsTester().addEqualityGroup(result, video).testEquals()
    }
}