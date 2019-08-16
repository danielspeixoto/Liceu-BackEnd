package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoGameRepository
import com.liceu.server.data.GameRepository
import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game
import com.liceu.server.domain.game.GameToInsert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestMongoGameRepositoryIntegration {

    @Autowired
    lateinit var data: MongoGameRepository
    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insert_Valid_CanBeRetrieved() {
        val now = Date.from(Instant.now())

        val id = data.insert(GameToInsert(
                testSetup.USER_ID_1,
                listOf(
                        Answer(
                                testSetup.QUESTION_ID_1,
                                1,
                                2
                        ),
                        Answer(
                                testSetup.QUESTION_ID_2,
                                1,
                                1
                        )
                ),
                now,
                10,
                1
        ))

        val game = data.toGame(gameRepo.findById(id).get())

        assertThat(game).isEqualTo(Game(
                id,
                testSetup.USER_ID_1,
                listOf(
                        Answer(
                                testSetup.QUESTION_ID_1,
                                1,
                                2
                        ),
                        Answer(
                                testSetup.QUESTION_ID_2,
                                1,
                                1
                        )
                ),
                now,
                10,
                1
        ))
    }
}