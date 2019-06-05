package com.liceu.server.integration

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.*
import com.liceu.server.data.MongoQuestionRepository
import com.liceu.server.data.QuestionRepository
import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.video.Video
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoQuestionRepositoryIntegration {

    @Autowired
    lateinit var data: MongoQuestionRepository
    @Autowired
    lateinit var questionRepo: QuestionRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun randomByTags_requestMoreThanExists_returnsExistent() {
        val questions = data.randomByTags(listOf("segunda"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2)
    }

    @Test
    fun randomByTags_emptyTags_returnsAll() {
        val questions = data.randomByTags(listOf(), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2, DataSetup.QUESTION_ID_3)
    }

    @Test
    fun randomByTags_requestLessThanExists_returnsExistentRandomly() {
        val ids = arrayListOf<String>()
        var i = 0
        while (i < 10) {
            val questions = data.randomByTags(listOf("segunda"), 1)
            assertThat(questions.size).isEqualTo(1)
            assertThat(questions[0].id).isIn(listOf(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2))
            ids.add(questions[0].id)
            i++
        }
        assertThat(ids).containsAtLeast(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2)
    }

    @Test
    fun randomByTags_notEveryoneHasTag_filters() {
        val questions = data.randomByTags(listOf("primeira"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(DataSetup.QUESTION_ID_1)
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
                DataSetup.QUESTION_ID_1,
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
        val videos = data.videos(DataSetup.QUESTION_ID_1, 0, 10).map { it.id }
        assertThat(videos).containsExactly(DataSetup.VIDEO_ID_3, DataSetup.VIDEO_ID_1).inOrder()
    }

    @Test
    fun videos_NoRelatedVideos_ReturnsEmpty() {
        val videos = data.videos("99c54d325b75357a571d4cc2", 0, 10).map { it.id }
        assertThat(videos).isEmpty()
    }

    @Test
    fun videos_CountEqualsOne_ReturnsFirst() {
        val videos = data.videos(DataSetup.QUESTION_ID_1, 0, 1).map { it.id }
        assertThat(videos).containsExactly(DataSetup.VIDEO_ID_3)
    }

    @Test
    fun videos_StartEqualsOne_SkipsFirst() {
        val videos = data.videos(DataSetup.QUESTION_ID_1, 1, 10).map { it.id }
        assertThat(videos).containsExactly(DataSetup.VIDEO_ID_1)
    }

    @Test
    fun videos_ValidRequest_DataIsValid() {
        val result = data.videos(DataSetup.QUESTION_ID_1, 1, 1)[0]
        val video = Video(
                DataSetup.VIDEO_ID_1,
                "primeira",
                "primeiro video",
                "videoId1",
                DataSetup.QUESTION_ID_1,
                1.1f,
                "defaultQuality",
                "channelTitle"
        )

        EqualsTester().addEqualityGroup(result, video).testEquals()
    }
}