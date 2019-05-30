package com.liceu.server.data

import com.google.common.testing.EqualsTester
import com.google.common.truth.Truth.assertThat
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.video.Video
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension

@ComponentScan
@ExtendWith(SpringExtension::class)
@DataMongoTest
class TestMongoVideoRepository {

    @Autowired
    lateinit var data: MongoVideoRepository
    @Autowired
    lateinit var repo: VideoRepository

    @BeforeEach
    fun dataSetup() {
        val item1 = MongoVideoRepository.MongoVideo(
                "primeira",
                "primeiro video",
                "videoId1",
                "question1",
                1.1f,
                MongoVideoRepository.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoVideoRepository.Channel(
                        "channelTitle",
                        "channelId"
                )
        )
        item1.id = "id1"
        repo.insert(item1)
        val item2 = MongoVideoRepository.MongoVideo(
                "segundo",
                "segundo video",
                "videoId3",
                "question2",
                1.1f,
                MongoVideoRepository.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoVideoRepository.Channel(
                        "channelTitle",
                        "channelId"
                )
        )
        item2.id = "id2"
        repo.insert(item2)
        val item3 = MongoVideoRepository.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                "question1",
                1.3f,
                MongoVideoRepository.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoVideoRepository.Channel(
                        "channelTitle",
                        "channelId"
                )
        )
        item3.id = "id3"
        repo.insert(item3)
    }

    @AfterEach
    fun destroy() {
        repo.deleteAll()
    }


    @Test
    fun questionRelatedVideos_HasRelatedVideos_ReturnsThem() {
        val videos = data.questionRelatedVideos("question1", 0, 10).map { it.id }
        assertThat(videos).containsExactly("id1", "id3")
    }

    @Test
    fun questionRelatedVideos_NoRelatedVideos_ReturnsEmpty() {
        val videos = data.questionRelatedVideos("question0", 0, 10).map { it.id }
        assertThat(videos).isEmpty()
    }

    @Test
    fun questionRelatedVideos_CountEqualsOne_ReturnsFirst() {
        val videos = data.questionRelatedVideos("question1", 0, 1).map { it.id }
        assertThat(videos).containsExactly("id1")
    }

    @Test
    fun questionRelatedVideos_StartEqualsOne_SkipsFirst() {
        val videos = data.questionRelatedVideos("question1", 1, 10).map { it.id }
        assertThat(videos).containsExactly("id3")
    }

    @Test
    fun questionRelatedVideos_ValidRequest_DataIsValid() {
        val result = data.questionRelatedVideos("question1", 0, 1)[0]
        val video = Video(
                "id1",
                "primeira",
                "primeiro video",
                "videoId1",
                "question1",
                1.1f,
                "defaultQuality",
                "channelTitle"
        )

        EqualsTester().addEqualityGroup(result, video).testEquals()
    }
}