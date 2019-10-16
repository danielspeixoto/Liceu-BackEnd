package com.liceu.server

import com.liceu.server.data.*

import com.liceu.server.util.JWTAuth
import khttp.get
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import java.time.Instant
import java.util.*
import org.json.JSONArray

@Component
@ContextConfiguration(classes=[TestConfiguration::class])
class DataSetup {

    @Value("\${google.clientId}")
    lateinit var googleClientId: String
    @Value("\${google.clientSecret}")
    lateinit var googleClientSecret: String

    @Autowired
    lateinit var jwtAuth: JWTAuth

    val QUESTION_ID_1 = "0a1449a4bdb40abd5ae1e431"
    val QUESTION_ID_2 = "09c54d325b75357a571d4cc2"
    val QUESTION_ID_3 = "07235b2a67c76abebce3f6e6"
    val QUESTION_ID_4 = "07235b2a67c76abebce3f6e7"

    val QUESTION_TRIVIA_ID_1 = "0a1449a4bdb40abd5ae1e461"
    val QUESTION_TRIVIA_ID_2 = "0a1449a4bdb40abd5ae1e411"
    val QUESTION_TRIVIA_ID_3 = "0a1449a4bdb40abd5ae1e421"
    val QUESTION_TRIVIA_ID_4 = "0a1449a4bdb40abd5ae1e432"
    val QUESTION_TRIVIA_ID_5 = "0a1449a4bdb40abd5ae1e433"
    val QUESTION_TRIVIA_ID_6 = "0a1449a4bdb40aba5ae1e433"

    val CHALLENGE_TRIVIA_ID_1 = "09c54d325b75357a571d4ca2"
    val CHALLENGE_TRIVIA_ID_2 = "09c54d325b75357a571d4cb2"
    val CHALLENGE_TRIVIA_ID_3 = "09c54d325b75357a571d4cc1"
    val CHALLENGE_TRIVIA_ID_4 = "09c54d325b75357a571d4cd2"
    val CHALLENGE_TRIVIA_ID_5 = "09c54d325b75357a571d4ce2"
    val CHALLENGE_TRIVIA_ID_6 = "09c54d425b75357a571d4ce2"
    val CHALLENGE_TRIVIA_ID_7 = "09b54d425b75357a571d4ce2"
    val CHALLENGE_TRIVIA_ID_8 = "09b44d425b75357a571d4ce2"

    val POST_ID_1 = "09c54d325b75357a581d4ca2"
    val POST_ID_2 = "09c54d325b75357a581d4ca3"
    val POST_ID_3 = "09c54d325b75357a581d4ca4"
    val POST_ID_4 = "09c54d325b75357a581d4ca5"
    val POST_ID_5 = "09c54d325b75357a581d4ca6"
    val POST_ID_6 = "09c54d325b75357a581d4ca7"
    val POST_ID_7 = "09c54d325b75357a581d4ca8"
    val POST_ID_8 = "09c54d325b75357a581d4ca9"
    val POST_ID_9= "09c54d325b75357a581d4ca0"

    val ACITIVITY_ID_1 = "0a1449a4bdb40abd5ae1e461"
    val ACITIVITY_ID_2 = "0a2449a4bdb40abd5ae1e461"
    val ACITIVITY_ID_3 = "0a3449a4bdb40abd5ae1e461"
    val ACITIVITY_ID_4 = "0a4449a4bdb40abd5ae1e461"
    val ACITIVITY_ID_5 = "0a5449a4bdb40abd5ae1e461"

    val COMMENT_ID_1 = "0a4449a4bdb40abd5ae1e461"

    val VIDEO_ID_1 = "1a1449a4bdb40abd5ae1e431"
    val VIDEO_ID_2 = "19c54d325b75357a571d4cc2"
    val VIDEO_ID_3 = "17235b2a67c76abebce3f6e6"
    val VIDEO_ID_4 = "17235b2a67c76abebce3f6e5"
    val VIDEO_ID_5 = "17235b2a67c76abebce3f6e4"
    val VIDEO_ID_6 = "17235b2a67c76abebce3f6e3"
    val VIDEO_ID_7 = "17235b2a67c76abebce3f6e2"

    val TAG_ID_1 = "2a1449a4bdb40abd5ae1e431"
    val TAG_ID_2 = "29c54d325b75357a571d4cc2"
    val TAG_ID_3 = "27235b2a67c76abebce3f6e6"

    val USER_ID_1 = "3a1449a4bdb40abd5ae1e431"
    val USER_1_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_1)
    }
    val USER_1_FCM_TOKEN = "eSy99pK0I-w:APA91bFnmrQV2FhjcXfNaW5Lp2FRkpOx188xtfX92I-480wEIxfxfVNQqoMEDUrcU7uxN8yGu7uGuoSsaLexKYUNdF6nHYAu7_5hJqgsynPZhViTK5B5KADuC1X3p5DBuOiDXpka8gnY"

    val USER_ID_2 = "39c54d325b75357a571d4cc2"
    val USER_2_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_2)
    }

    val USER_ID_3 = "37235b2a67c76abebce3f6e6"
    val USER_3_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_3)
    }

    val USER_ID_4 = "37235b2a67c76abebce3f6e3"
    val USER_4_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_4)
    }
    val USER_ID_5 = "37235b2a67c76abebce3f6e8"
    val USER_5_ACCESS_TOKEN by lazy {
        jwtAuth.sign(USER_ID_5)
    }
    val FACEBOOK_ID = "3aaa5b2a67c76abebce3f6e8"

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
    @Autowired
    lateinit var postRepo: PostRepository
    @Autowired
    lateinit var activityRepo: ActivityRepository

    fun setup() {
        questionRepo.deleteAll()
        videoRepo.deleteAll()
        userRepo.deleteAll()
        gameRepo.deleteAll()
        triviaRepo.deleteAll()
        challengeRepo.deleteAll()
        postRepo.deleteAll()
        activityRepo.deleteAll()
        questions()
        videos()
        users()
        games()
        trivia()
        challenge()
        post()
        activity()
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

        val item4 = MongoDatabase.MongoVideo(
                "bErnouLLI",
                "terceiro video",
                "videoId2",
                ObjectId(QUESTION_ID_3),
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
                2
        )
        item4.id = ObjectId(VIDEO_ID_4)
        videoRepo.insert(item4)

        val item5 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                ObjectId(QUESTION_ID_3),
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
        item5.id = ObjectId(VIDEO_ID_5)
        videoRepo.insert(item5)

        val item6 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                ObjectId(QUESTION_ID_3),
                1.3f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "bernouLLI",
                        "channelId"
                ),
                1
        )
        item6.id = ObjectId(VIDEO_ID_6)
        videoRepo.insert(item6)

        val item7 = MongoDatabase.MongoVideo(
                "terceiro",
                "terceiro video",
                "videoId2",
                ObjectId(QUESTION_ID_4),
                1.3f,
                MongoDatabase.Thumbnails(
                        "highQuality",
                        "defaultQuality",
                        "mediumQuality"
                ),
                MongoDatabase.Channel(
                        "bernouLLI",
                        "channelId"
                ),
                1
        )
        item7.id = ObjectId(VIDEO_ID_7)
        videoRepo.insert(item7)
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
                GeoJsonPoint(-12.83, -44.86),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                listOf(
                        ObjectId(USER_ID_3),
                        ObjectId(USER_ID_4)
                ),
                USER_1_FCM_TOKEN
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
                GeoJsonPoint(-15.83, -47.86),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                savedPosts = listOf(
                        ObjectId(POST_ID_2)
                )
        )
        user2.id = ObjectId(USER_ID_2)
        userRepo.insert(user2)
        val user3 = MongoDatabase.MongoUser(
                "manitos1",
                "user3@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture3.jpg",
                        200,
                        200
                ),
                "facebookId3",
                GeoJsonPoint(-11.83, -49.86),
                "BA",
                "MARISTA",
                18,
                "Jorginho",
                "jorge",
                "alguma descrição maneira",
                "www.umsite.com.br",
                listOf(
                        ObjectId(USER_ID_1)
                ),
                listOf(
                        ObjectId(USER_ID_2),
                        ObjectId(USER_ID_4)
                ),
                desiredCourse = "Cientista",
                telephoneNumber = "71923232323",
                isFounder = true,
                savedPosts = listOf(
                    ObjectId(POST_ID_3)
                )
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
                GeoJsonPoint(-20.83, -57.86),
                null,
                null,
                null,
                null,
                null,
                null,
                 null,
                listOf(
                        ObjectId(USER_ID_3)
                ),
                listOf(
                        ObjectId(USER_ID_1)
                )
        )
        user4.id = ObjectId(USER_ID_4)
        userRepo.insert(user4)

        val user5 = MongoDatabase.MongoUser(
                "mano5",
                "user54@g.com",
                MongoDatabase.MongoPicture(
                        "https://picture5.jpg",
                        200,
                        200
                ),
                "facebookId5",
                GeoJsonPoint(-20.83, -57.86),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        )
        user5.id = ObjectId(USER_ID_5)
        userRepo.insert(user5)

        val facebookUser = MongoDatabase.MongoUser(
            "Sophia Aldajeidicfbi Okelolasky",
            "sulwxcmqrp_1567856463@tfbnw.net",
            MongoDatabase.MongoPicture(
                    "imagemFace",
                    200,
                    200
            ),
            "115992013112781",
            GeoJsonPoint(-20.83, -57.86),
            null,
            "Colégio legal do face",
            17,
            "souOFace",
            "faceSchool",
            "face estudando",
            null,
            null,
            null
        )
        facebookUser.id = ObjectId(FACEBOOK_ID)
        userRepo.insert(facebookUser)

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
                null
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
                ),
                null,
                3,
                4,
                true
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
                    ),
                null,
                null,
                null,
                true
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
                    ),
                null,
                null,
                null,
                true
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
                    ),
                null,
                null,
                null,
                true
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
                ),
                null,
                null,
                null,
                true
        )
        q5.id = ObjectId(QUESTION_TRIVIA_ID_5)
        triviaRepo.insert(q5)

        val q6 = MongoDatabase.MongoTriviaQuestion(
                ObjectId(USER_ID_3),
                "Quem descobriu o cnancanan?",
                "Pedro Alvares Cabral",
                "Geralt",
                listOf(
                        "historia",
                        "descoberta"
                ),
                null,
                null,
                null,
                false
        )
        q6.id = ObjectId(QUESTION_TRIVIA_ID_6)
        triviaRepo.insert(q6)
    }

    fun challenge() {
        val ch1 = MongoDatabase.MongoChallenge(
                USER_ID_3,
                USER_ID_2,
                listOf(
                        "oi",
                        "abriu",
                        "testando",
                        "4"
                ),
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
                                ),
                                null,
                                null,
                                null
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
                                ),
                                null,
                                null,
                                null
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
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                true
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
                                ),
                                listOf(
                                        MongoDatabase.MongoComment(
                                                ObjectId(COMMENT_ID_1),
                                                ObjectId(USER_ID_1),
                                                "user1",
                                                "essa questao e boa"
                                        )
                                ),
                                null,
                                null
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
                                ),
                                null,
                                null,
                                null
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
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        )
        ch5.id = ObjectId(CHALLENGE_TRIVIA_ID_5)
        challengeRepo.insert(ch5)

    val ch6 = MongoDatabase.MongoChallenge(
                USER_ID_1,
                null,
                listOf(),
                listOf(),
                null,
                null,
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
                            ),
                            null,
                            null,
                            null
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
                            ),
                            null,
                            null,
                            null
                    ),
                    MongoDatabase.MongoChallengeTrivia(
                            ObjectId(QUESTION_TRIVIA_ID_1),
                            ObjectId(USER_ID_4),
                            "1+4?",
                            "5",
                            "1",
                            listOf(
                                    "graficos",
                                    "algebra"
                            ),
                            null,
                            null,
                            null
                    )
            ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        )
        ch6.id = ObjectId(CHALLENGE_TRIVIA_ID_6)
        challengeRepo.insert(ch6)

        val ch7 = MongoDatabase.MongoChallenge(
                USER_ID_1,
                USER_ID_2,
                listOf(
                        "1"
                ),
                listOf(),
                0,
                null,
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
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                true
        )
        ch7.id = ObjectId(CHALLENGE_TRIVIA_ID_7)
        challengeRepo.insert(ch7)

        val ch8 = MongoDatabase.MongoChallenge(
                USER_ID_1,
                USER_ID_3,
                listOf(
                        "1"
                ),
                listOf(),
                0,
                null,
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
                                ),
                                null,
                                null,
                                null
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        )
        ch8.id = ObjectId(CHALLENGE_TRIVIA_ID_8)
        challengeRepo.insert(ch8)
    }

    fun post(){
        val post1 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_1),
                "text",
                "teste de texto",
                null,
                null,
                null,
                Date.from(Instant.parse("2019-08-27T11:40:20.00Z")),
                null,
                listOf(
                        MongoDatabase.MongoPostQuestions(
                                "Qual o primeiro nome de Einstein?",
                                "Albert",
                                listOf(
                                        "José","Albertonio","Lucas"
                                )
                        ),
                        MongoDatabase.MongoPostQuestions(
                                "Qual o primeiro nome de Newton?",
                                "Isaac",
                                listOf(
                                        "José","Albertonio","Albert"
                                )
                        )
                ),
                null,
                true
        )
        post1.id = ObjectId(POST_ID_1)
        postRepo.insert(post1)
        val post2 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_2),
                "text",
                "teste de texto 2",
                null,
                null,
                null,
                Date.from(Instant.parse("2019-08-27T11:40:20.00Z")),
                null,
                listOf(
                        MongoDatabase.MongoPostQuestions(
                                "Qual o primeiro nome de Einstein?",
                                "Albert",
                                listOf(
                                        "José","Albertonio","Lucas"
                                )
                        )
                ),
                null,
                true
        )
        post2.id = ObjectId(POST_ID_2)
        postRepo.insert(post2)
        val post3 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_4),
                "video",
                "teste de video",
                null,
                MongoDatabase.MongoPostVideo(
                        "asassa",
                        MongoDatabase.MongoPostThumbnails(
                                "high",
                                "default",
                                "medium"
                        )
                ),
                null,
                Date.from(Instant.parse("2019-08-27T12:40:20.00Z")),
                null,
                null,
                null
        )
        post3.id = ObjectId(POST_ID_3)
        postRepo.insert(post3)
        val post4 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_3),
                "text",
                "teste de texto 2",
                null,
                null,
                null,
                Date.from(Instant.parse("2019-08-27T11:40:20.00Z")),
                null,
                null,
                null,
                true,
                10
        )
        post4.id = ObjectId(POST_ID_4)
        postRepo.insert(post4)
        val post5 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_3),
                "text",
                "teste de texto 2",
                null,
                null,
                null,
                Date.from(Instant.parse("2019-08-27T13:40:20.00Z")),
                null,
                null,
                null,
                true
        )
        post5.id = ObjectId(POST_ID_5)
        postRepo.insert(post5)
        val post6 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_3),
                "text",
                "teste de texto 2222",
                null,
                null,
                null,
                Date.from(Instant.parse("2019-08-27T13:40:20.00Z")),
                null,
                null,
                null,
                false
        )
        post6.id = ObjectId(POST_ID_6)
        postRepo.insert(post6)


        val post7 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_3),
                "multipleImages",
                "teste de multipleImages",
                MongoDatabase.MongoPostImage(
                        "teste1",
                        "png",
                        "www.minhaimagem1.com.br"
                ),
                null,
                listOf(
                        MongoDatabase.MongoPostImage(
                                "teste1",
                                "png",
                                "www.minhaimagem1.com.br"
                        ),
                        MongoDatabase.MongoPostImage(
                                "teste2",
                                "jpeg",
                                "www.minhaimagem2.com.br"
                        )
                ),
                Date.from(Instant.parse("2019-08-27T13:40:20.00Z")),
                null,
                null,
                null,
                false
        )
        post7.id = ObjectId(POST_ID_7)
        postRepo.insert(post7)

        val post8 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_5),
                "video",
                "teste de video 2",
                null,
                MongoDatabase.MongoPostVideo(
                        "asassa",
                        MongoDatabase.MongoPostThumbnails(
                                "high",
                                "default",
                                "medium"
                        )
                ),
                null,
                Date.from(Instant.parse("2019-08-27T12:40:20.00Z")),
                null,
                null,
                null,
                false
        )
        post8.id = ObjectId(POST_ID_8)
        postRepo.insert(post8)

        val post9 = MongoDatabase.MongoPost(
                ObjectId(USER_ID_5),
                "video",
                "teste de video 3",
                null,
                MongoDatabase.MongoPostVideo(
                        "asassa",
                        MongoDatabase.MongoPostThumbnails(
                                "high",
                                "default",
                                "medium"
                        )
                ),
                null,
                Date.from(Instant.parse("2019-08-27T12:40:20.00Z")),
                null,
                null,
                null,
                true
        )
        post9.id = ObjectId(POST_ID_9)
        postRepo.insert(post9)
    }
    fun activity(){
        val activity1 = MongoDatabase.MongoActivities(
                ObjectId(USER_ID_1),
                "followedUser",
                hashMapOf(
                        "followedBy" to USER_ID_2
                ),
                Date.from(Instant.parse("2019-08-27T13:40:20.00Z"))
        )
        activity1.id = ObjectId(ACITIVITY_ID_1)
        activityRepo.insert(activity1)

        val activity2 = MongoDatabase.MongoActivities(
                ObjectId(USER_ID_1),
                "challengeFinished",
                hashMapOf(
                        "challengerId" to USER_ID_2,
                        "triviaId" to CHALLENGE_TRIVIA_ID_1
                ),
                Date.from(Instant.parse("2019-08-28T12:40:20.00Z"))
        )
        activity2.id = ObjectId(ACITIVITY_ID_2)
        activityRepo.insert(activity2)


        val activity3 = MongoDatabase.MongoActivities(
                ObjectId(USER_ID_2),
                "followedUser",
                hashMapOf(
                        "unfollowedBy" to USER_ID_1
                ),
                Date.from(Instant.parse("2019-08-27T13:40:20.00Z"))
        )
        activity3.id = ObjectId(ACITIVITY_ID_3)
        activityRepo.insert(activity3)

        val activity4 = MongoDatabase.MongoActivities(
                ObjectId(USER_ID_1),
                "challengeAccepted",
                hashMapOf(
                        "challengerId" to USER_ID_2,
                        "triviaId" to CHALLENGE_TRIVIA_ID_2
                ),
                Date.from(Instant.parse("2019-08-28T13:40:20.00Z"))
        )
        activity4.id = ObjectId(ACITIVITY_ID_4)
        activityRepo.insert(activity4)
    }


}


