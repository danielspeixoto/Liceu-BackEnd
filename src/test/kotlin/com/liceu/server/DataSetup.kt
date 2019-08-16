package com.liceu.server

import com.liceu.server.data.*
import com.liceu.server.util.JWTAuth
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import java.time.Instant
import java.util.*

@Component
@ContextConfiguration(classes=[TestConfiguration::class])
class DataSetup {

    @Autowired
    lateinit var jwtAuth: JWTAuth

    val QUESTION_ID_1 = "0a1449a4bdb40abd5ae1e431"
    val QUESTION_ID_2 = "09c54d325b75357a571d4cc2"
    val QUESTION_ID_3 = "07235b2a67c76abebce3f6e6"

    val QUESTION_TRIVIA_ID_1 = "0a1449a4bdb40abd5ae1e461"
    val QUESTION_TRIVIA_ID_2 = "0a1449a4bdb40abd5ae1e411"
    val QUESTION_TRIVIA_ID_3 = "0a1449a4bdb40abd5ae1e421"
    val QUESTION_TRIVIA_ID_4 = "0a1449a4bdb40abd5ae1e432"
    val QUESTION_TRIVIA_ID_5 = "0a1449a4bdb40abd5ae1e433"

    val CHALLENGE_TRIVIA_ID_1 = "09c54d325b75357a571d4ca2"
    val CHALLENGE_TRIVIA_ID_2 = "09c54d325b75357a571d4cb2"
    val CHALLENGE_TRIVIA_ID_3 = "09c54d325b75357a571d4cc1"
    val CHALLENGE_TRIVIA_ID_4 = "09c54d325b75357a571d4cd2"
    val CHALLENGE_TRIVIA_ID_5 = "09c54d325b75357a571d4ce2"

    val VIDEO_ID_1 = "1a1449a4bdb40abd5ae1e431"
    val VIDEO_ID_2 = "19c54d325b75357a571d4cc2"
    val VIDEO_ID_3 = "17235b2a67c76abebce3f6e6"

    val TAG_ID_1 = "2a1449a4bdb40abd5ae1e431"
    val TAG_ID_2 = "29c54d325b75357a571d4cc2"
    val TAG_ID_3 = "27235b2a67c76abebce3f6e6"

    val USER_ID_1 = "3a1449a4bdb40abd5ae1e431"
    val USER_1_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_1)
    }

    val USER_ID_2 = "39c54d325b75357a571d4cc2"
    val USER_ID_3 = "37235b2a67c76abebce3f6e6"
    val USER_ID_4 = "37235b2a67c76abebce3f6e3"
    val USER_ID_5 = "37235b2a67c76abebce3h6e8"

    val GAME_ID_1 = "4a1449a4bdb40abd5ae1e431"
    val GAME_ID_2 = "49c54d325b75357a571d4cc2"
    val GAME_ID_3 = "47235b2a67c76abebce3f6e6"
    val GAME_ID_4 = "47235b2a67c76abebce3f6e9"
    val GAME_ID_5 = "47235b2a67c76abebce3f6e3"
    val GAME_ID_6 = "47235b2a67c76abebce3f6e2"
    val GAME_ID_7 = "47235b2a67c76abebce3f6e1"

    val INVALID_ID = "99235b2a67c76abebce3f6e6"

    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository
    @Autowired
    lateinit var userRepo: UserRepository
    @Autowired
    lateinit var gameRepo: GameRepository
    @Autowired
    lateinit var triviaRepo: TriviaRepository
    @Autowired
    lateinit var challengeRepo: ChallengeRepository

    fun setup() {
        questionRepo.deleteAll()
        videoRepo.deleteAll()
        userRepo.deleteAll()
        gameRepo.deleteAll()
        triviaRepo.deleteAll()
        challengeRepo.deleteAll()
        questions()
        videos()
        users()
        games()
        trivia()
        challenge()
    }

    fun questions() {
        val q1 = MongoDatabase.MongoQuestion(
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

    fun users() {
        val user1 = MongoDatabase.MongoUser(
                "user1",
                "user1@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture1.jpg",
                        200,
                        200
                ),
                "facebookId1",
                null,
                null
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
                "facebookId2",
                null,
                null
        )
        user2.id = ObjectId(USER_ID_2)
        userRepo.insert(user2)
        val user3 = MongoDatabase.MongoUser(
                "user3",
                "user3@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture3.jpg",
                        200,
                        200
                ),
                "facebookId3",
                null,
                null
        )
        user3.id = ObjectId(USER_ID_3)
        userRepo.insert(user3)
        val user4 = MongoDatabase.MongoUser(
                "user4",
                "user4@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture4.jpg",
                        200,
                        200
                ),
                "facebookId4",
                null,
                null
        )
        user4.id = ObjectId(USER_ID_4)
        userRepo.insert(user4)

    }

    fun games() {
        val game1 = MongoDatabase.MongoGame(
                ObjectId( USER_ID_1),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                1,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-10-10T10:10:10.00Z")),
                1,
                1
        )
        game1.id = ObjectId(GAME_ID_1)
        gameRepo.insert(game1)
        val game2 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_2),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                1,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                2
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                2,
                0
        )
        game2.id = ObjectId(GAME_ID_2)
        gameRepo.insert(game2)
        val game3 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_3),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                2,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-10-12T11:20:20.00Z")),
                3,
                2
        )
        game3.id = ObjectId(GAME_ID_3)
        gameRepo.insert(game3)
        val game4 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_3),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                2,
                                1
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                1
                        )
                ),
                Date.from(Instant.parse("2019-10-13T11:20:20.00Z")),
                1,
                0
        )
        game4.id = ObjectId(GAME_ID_4)
        gameRepo.insert(game4)
        val game5 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_4),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                2,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-10-14T11:20:20.00Z")),
                1,
                2
        )
        game5.id = ObjectId(GAME_ID_5)
        gameRepo.insert(game5)
        val game6 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_4),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                2,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-09-14T11:20:20.00Z")),
                2,
                2
        )
        game6.id = ObjectId(GAME_ID_6)
        gameRepo.insert(game6)
        val game7 = MongoDatabase.MongoGame(
                ObjectId(USER_ID_4),
                listOf(
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_1),
                                2,
                                2
                        ),
                        MongoDatabase.MongoAnswer(
                                ObjectId(QUESTION_ID_3),
                                3,
                                3
                        )
                ),
                Date.from(Instant.parse("2019-05-23T10:30:50.00Z")),
                2,
                2
        )
        game7.id = ObjectId(GAME_ID_7)
        gameRepo.insert(game7)
    }

    fun trivia(){
        val q1 = MongoDatabase.MongoTriviaQuestion(
                ObjectId(USER_ID_1),
                "1+1 = ?",
                "2",
                "0",
                listOf(
                        "matematica",
                        "angulos",
                        "trigonometria"
                )
        )
        q1.id = ObjectId(QUESTION_TRIVIA_ID_1)
        triviaRepo.insert(q1)

        val q2 = MongoDatabase.MongoTriviaQuestion(
                    ObjectId(USER_ID_2),
                    "1+1+1 = ?",
                    "3",
                    "1",
                    listOf(
                            "matematica",
                            "angulos",
                            "trigonometria"
                    )
            )
            q2.id = ObjectId(QUESTION_TRIVIA_ID_2)
            triviaRepo.insert(q2)

        val q3 = MongoDatabase.MongoTriviaQuestion(
                    ObjectId(USER_ID_3),
                    "1+1+2= ?",
                    "4",
                    "3",
                    listOf(
                            "matematica",
                            "angulos",
                            "trigonometria"
                    )
            )
            q3.id = ObjectId(QUESTION_TRIVIA_ID_3)
            triviaRepo.insert(q3)

        val q4 = MongoDatabase.MongoTriviaQuestion(
                    ObjectId(USER_ID_4),
                    "1+1+3 = ?",
                    "5",
                    "2",
                    listOf(
                            "matematica",
                            "angulos",
                            "trigonometria"
                    )
            )
            q4.id = ObjectId(QUESTION_TRIVIA_ID_4)
            triviaRepo.insert(q4)

        val q5 = MongoDatabase.MongoTriviaQuestion(
                ObjectId(USER_ID_3),
                "Quem descobriu o Brasil?",
                "Pedro Alvares Cabral",
                "Geralt",
                listOf(
                        "historia",
                        "descoberta"
                )
        )
        q5.id = ObjectId(QUESTION_TRIVIA_ID_5)
        triviaRepo.insert(q5)
    }

    fun challenge() {
        val ch1 = MongoDatabase.MongoChallenge(
                USER_ID_3,
                USER_ID_2,
                //null,
                listOf(
                        "oi",
                        "abriu",
                        "testando",
                        "4"
                ),
                //listOf(),
                listOf(
                        "oi2",
                        "abriu2",
                        "testando2",
                        "2"
                ),
                10,
                9,
                listOf(
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_2),
                                ObjectId(USER_ID_3),
                                "1+1?",
                                "2",
                                "1",
                                listOf(
                                        "matematica",
                                        "algebra"
                                )
                        )
                ),
                Date.from(Instant.parse("2019-10-11T19:20:20.00Z"))
        )
        ch1.id = ObjectId(CHALLENGE_TRIVIA_ID_1)
        challengeRepo.insert(ch1)

        val ch2 = MongoDatabase.MongoChallenge(
                USER_ID_2,
                USER_ID_1,
                listOf(
                        "oaai",
                        "aaabriu",
                        "taaestando",
                        "4aa"
                ),
                listOf(
                        "oaai2",
                        "abaariu2",
                        "teaastando2",
                        "2aa"
                ),
                6,
                3,
                listOf(
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_1),
                                ObjectId(USER_ID_4),
                                "1+2?",
                                "3",
                                "0",
                                listOf(
                                        "graficos",
                                        "algebra"
                                )
                        ),
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_1),
                                ObjectId(USER_ID_4),
                                "1+3?",
                                "4",
                                "0",
                                listOf(
                                        "graficos",
                                        "algebra"
                                )
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
                //Date.from(Instant.parse("2019-10-11T19:20:20.00Z"))
        )
        ch2.id = ObjectId(CHALLENGE_TRIVIA_ID_2)
        challengeRepo.insert(ch2)

        val ch3 = MongoDatabase.MongoChallenge(
                USER_ID_3,
                null,
                listOf(
                        "oi",
                        "abriu",
                        "testando",
                        "4"
                ),
                listOf(),
                10,
                9,
                listOf(
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_2),
                                ObjectId(USER_ID_3),
                                "1+1?",
                                "2",
                                "1",
                                listOf(
                                        "matematica",
                                        "algebra"
                                )
                        )
                ),
                Date.from(Instant.parse("2019-06-11T11:20:20.00Z"))
        )
        ch3.id = ObjectId(CHALLENGE_TRIVIA_ID_3)
        challengeRepo.insert(ch3)

        val ch4 = MongoDatabase.MongoChallenge(
                USER_ID_1,
                null,
                listOf(
                        "oi",
                        "abriu",
                        "testando",
                        "4"
                ),
                listOf(),
                10,
                9,
                listOf(
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_2),
                                ObjectId(USER_ID_3),
                                "1+1?",
                                "2",
                                "1",
                                listOf(
                                        "matematica",
                                        "algebra"
                                )
                        )
                ),
                Date.from(Instant.parse("2019-10-11T19:20:20.00Z"))
        )
        ch4.id = ObjectId(CHALLENGE_TRIVIA_ID_4)
        challengeRepo.insert(ch4)


        val ch5 = MongoDatabase.MongoChallenge(
                USER_ID_5,
                null,
                listOf(),
                listOf(),
                null,
                null,
                listOf(
                        MongoDatabase.MongoChallengeTrivia(
                                ObjectId(QUESTION_TRIVIA_ID_2),
                                ObjectId(USER_ID_3),
                                "1+1?",
                                "2",
                                "1",
                                listOf(
                                        "matematica",
                                        "algebra"
                                )
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        )
        ch5.id = ObjectId(CHALLENGE_TRIVIA_ID_5)
        challengeRepo.insert(ch5)
    }
}


