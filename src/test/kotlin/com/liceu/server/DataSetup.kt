package com.liceu.server

import com.liceu.server.data.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class DataSetup {

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

        const val USER_ID_1 = "3a1449a4bdb40abd5ae1e431"
        const val USER_ID_2 = "39c54d325b75357a571d4cc2"
        const val USER_ID_3 = "37235b2a67c76abebce3f6e6"

        const val GAME_ID_1 = "4a1449a4bdb40abd5ae1e431"
        const val GAME_ID_2 = "49c54d325b75357a571d4cc2"
        const val GAME_ID_3 = "47235b2a67c76abebce3f6e6"

        const val INVALID_ID = "99235b2a67c76abebce3f6e6"
    }

    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository
    @Autowired
    lateinit var tagRepo: TagRepository
    @Autowired
    lateinit var userRepo: UserRepository
    @Autowired
    lateinit var gameRepo: GameRepository

    fun setup() {
        questionRepo.deleteAll()
        videoRepo.deleteAll()
        tagRepo.deleteAll()
        userRepo.deleteAll()
        gameRepo.deleteAll()
        questions()
        videos()
        tags()
        users()
        games()
    }

    fun questions() {
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
                200,
                "https://url1.com"
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
                200,
                "https://url2.com"
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
                3,
                listOf(),
                "54321",
                "referenceId3",
                1,
                100,
                200,
                "https://url3.com"
        )
        q3.id = ObjectId(QUESTION_ID_3)
        questionRepo.insert(q3)

    }

    fun videos() {
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
    }

    fun tags() {
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

    fun users() {
        val user1 = MongoDatabase.MongoUser(
                "user1",
                "user1@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture1.jpg",
                        200,
                        200
                ),
                "facebookId1"
        )
        user1.id = ObjectId(USER_ID_1)
        userRepo.insert(user1)
        val user2 = MongoDatabase.MongoUser(
                "user2",
                "user2@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture2.jpg",
                        200,
                        200
                ),
                "facebookId2"
        )
        user2.id = ObjectId(USER_ID_2)
        userRepo.insert(user2)
    }

    fun games() {
        val game1 = MongoDatabase.MongoGame(
                ObjectId( USER_ID_1),
                listOf(
                        MongoDatabase.MongoAnswer(
                                QUESTION_ID_1,
                                1,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                QUESTION_ID_3,
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-10-10T10:10:10.00Z"))
        )
        game1.id = ObjectId(GAME_ID_1)
        gameRepo.insert(game1)
        val game2 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_2),
                listOf(
                        MongoDatabase.MongoAnswer(
                                QUESTION_ID_1,
                                1,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                QUESTION_ID_3,
                                3,
                                2
                        )
                ),
                Date.from(Instant.parse("2019-11-11T11:20:20.00Z"))
        )
        game2.id = ObjectId(GAME_ID_2)
        gameRepo.insert(game2)
    }
}


