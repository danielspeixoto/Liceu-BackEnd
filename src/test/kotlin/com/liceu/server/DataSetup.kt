package com.liceu.server

import com.liceu.server.data.MongoDatabase
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.TagRepository
import com.liceu.server.data.VideoRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestSetup {

    companion object {
        const val QUESTION_ID_1 = "0a1449a4bdb40abd5ae1e431"
        const val QUESTION_ID_2 = "09c54d325b75357a571d4cc2"
        const val QUESTION_ID_3 = "07235b2a67c76abebce3f6e6"

        const val VIDEO_ID_1 = "1a1449a4bdb40abd5ae1e431"
        const val VIDEO_ID_2 = "19c54d325b75357a571d4cc2"
        const val VIDEO_ID_3 = "17235b2a67c76abebce3f6e6"

        const val TAG_ID_1 = "2a1449a4bdb40abd5ae1e431"
        const val TAG_ID_2 = "29c54d325b75357a571d4cc2"
        const val TAG_ID_3 = "27235b2a67c76abebce3f6e6"

        const val INVALID_ID = "99235b2a67c76abebce3f6e6"
    }

    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository
    @Autowired
    lateinit var tagRepo: TagRepository

    fun setup() {
        questionRepo.deleteAll()
        videoRepo.deleteAll()
        tagRepo.deleteAll()
        val q1 = MongoDatabase.MongoQuestion(
                byteArrayOf('a'.toByte(), 'b'.toByte()),
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
        q1.id = ObjectId(QUESTION_ID_1)
        questionRepo.insert(q1)
        val q2 = MongoDatabase.MongoQuestion(
                byteArrayOf('e'.toByte(), 'f'.toByte()),
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
        q2.id = ObjectId(QUESTION_ID_2)
        questionRepo.insert(q2)
        val q3 = MongoDatabase.MongoQuestion(
                byteArrayOf('c'.toByte(), 'd'.toByte()),
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
        q3.id = ObjectId(QUESTION_ID_3)
        questionRepo.insert(q3)

        val item1 = MongoDatabase.MongoVideo(
                "primeira",
                "primeiro video",
                "videoId1",
                ObjectId(QUESTION_ID_1),
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
        item1.id = ObjectId(VIDEO_ID_1)
        videoRepo.insert(item1)
        val item2 = MongoDatabase.MongoVideo(
                "segundo",
                "segundo video",
                "videoId3",
                ObjectId(QUESTION_ID_2),
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
        item2.id = ObjectId(VIDEO_ID_2)
        videoRepo.insert(item2)
        val item3 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                ObjectId(QUESTION_ID_1),
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
        item3.id = ObjectId(VIDEO_ID_3)
        videoRepo.insert(item3)

        val tag1 = MongoDatabase.MongoTag(
                "primeira",
                1
        )
        tag1.id = ObjectId(TAG_ID_1)
        tagRepo.insert(tag1)
        val tag2 = MongoDatabase.MongoTag(
                "segunda",
                2
        )
        tag2.id = ObjectId(TAG_ID_2)
        tagRepo.insert(tag2)

        val tag3 = MongoDatabase.MongoTag(
                "terceira",
                0
        )
        tag3.id = ObjectId(TAG_ID_3)
        tagRepo.insert(tag3)
    }
}


