package com.liceu.server.integration

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoQuestionRepository
import com.liceu.server.data.QuestionRepository
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.question.Question
import com.liceu.server.domain.video.Video
import org.junit.Assert
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
        assertThat(ids).containsExactly(testSetup.QUESTION_ID_1, testSetup.QUESTION_ID_2)
    }

    @Test
    fun randomByTags_emptyTags_returnsAll() {
        val questions = data.randomByTags(listOf(), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(testSetup.QUESTION_ID_1, testSetup.QUESTION_ID_2, testSetup.QUESTION_ID_3)
    }

    @Test
    fun randomByTags_requestLessThanExists_returnsExistentRandomly() {
        val ids = arrayListOf<String>()
        var i = 0
        while (i < 10) {
            val questions = data.randomByTags(listOf("segunda"), 1)
            assertThat(questions.size).isEqualTo(1)
            assertThat(questions[0].id).isIn(listOf(testSetup.QUESTION_ID_1, testSetup.QUESTION_ID_2))
            ids.add(questions[0].id)
            i++
        }
        assertThat(ids).containsAtLeast(testSetup.QUESTION_ID_1, testSetup.QUESTION_ID_2)
    }

    @Test
    fun randomByTags_notEveryoneHasTag_filters() {
        val questions = data.randomByTags(listOf("primeira"), 10)
        val ids = questions.map { it.id }
        assertThat(ids).containsExactly(testSetup.QUESTION_ID_1)
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
                testSetup.QUESTION_ID_1,
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
                200,
                "https://url1.com"
        )

        EqualsTester()
                .addEqualityGroup(question, result)
                .testEquals()
    }

    @Test
    fun videos_HasRelatedVideos_ReturnsThemOrdered() {
        val videos = data.videos(testSetup.QUESTION_ID_1, 0, 10).map { it.id }
        assertThat(videos).containsExactly(testSetup.VIDEO_ID_3, testSetup.VIDEO_ID_1).inOrder()
    }

    @Test
    fun videos_NoRelatedVideos_ReturnsEmpty() {
        val videos = data.videos("99c54d325b75357a571d4cc2", 0, 10).map { it.id }
        assertThat(videos).isEmpty()
    }

    @Test
    fun videos_CountEqualsOne_ReturnsFirst() {
        val videos = data.videos(testSetup.QUESTION_ID_1, 0, 1).map { it.id }
        assertThat(videos).containsExactly(testSetup.VIDEO_ID_3)
    }

    @Test
    fun videos_StartEqualsOne_SkipsFirst() {
        val videos = data.videos(testSetup.QUESTION_ID_1, 1, 10).map { it.id }
        assertThat(videos).containsExactly(testSetup.VIDEO_ID_1)
    }

    @Test
    fun videos_ValidRequest_DataIsValid() {
        val result = data.videos(testSetup.QUESTION_ID_1, 1, 1)[0]
        val video = Video(
                testSetup.VIDEO_ID_1,
                "primeira",
                "primeiro video",
                "videoId1",
                testSetup.QUESTION_ID_1,
                1.1f,
                "defaultQuality",
                "channelTitle"
        )

        EqualsTester().addEqualityGroup(result, video).testEquals()
    }

    @Test
    fun questionById_QuestionExits_ReturnQuestion(){
        val result1 = data.getQuestionById(testSetup.QUESTION_ID_1)
        assertThat(result1.source).isEqualTo("ENEM")
        assertThat(result1.variant).isEqualTo("AMARELA")
        assertThat(result1.edition).isEqualTo(2017)
        assertThat(result1.number).isEqualTo(3)
        assertThat(result1.domain).isEqualTo("matemática")
        assertThat(result1.answer).isEqualTo(1)
        assertThat(result1.tags[0]).isEqualTo("primeira")
        assertThat(result1.tags[1]).isEqualTo("segunda")
        assertThat(result1.itemCode).isEqualTo("12345")
        assertThat(result1.referenceId).isEqualTo("referenceId")
        assertThat(result1.stage).isEqualTo(2)
        assertThat(result1.width).isEqualTo(100)
        assertThat(result1.height).isEqualTo(200)
        assertThat(result1.imageURL).isEqualTo("https://url1.com")

    }

    @Test
    fun questionById_QuestionDontExists_ReturnError(){
        assertThrows<ItemNotFoundException> {
            data.getQuestionById("0a1449a4bdb40abd5ae1e432")
        }
    }

}