package com.liceu.server.integration

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.GameRepository
import com.liceu.server.data.MongoGameRepository
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

class TestMongoGameRanking {

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
    fun ranking_sameMonth_returnsEqual(){
        val result = data.ranking(10,2019)
        val ids = result.map { it.id }
        val result2 = data.ranking(10,2019)
        val ids2 = result2.map { it.id }
        Truth.assertThat(ids.size).isGreaterThan(0)
        Truth.assertThat(ids).isEqualTo(ids2)
    }
    @Test
    fun ranking_differentMonth_returnsDifferent(){
        val result = data.ranking(9,2019)
        val ids = result.map { it.id }
        val result2 = data.ranking(10,2019)
        val ids2 = result2.map { it.id }
        Truth.assertThat(ids.size).isGreaterThan(0)
        Truth.assertThat(ids).isNotEqualTo(ids2)
    }

    @Test
    fun ranking_valid_returnsOrdered(){
        val result = data.ranking(10, 2019)
        val ids = result.map { it.id }
        Truth.assertThat(ids).containsExactly(testSetup.GAME_ID_5, testSetup.GAME_ID_3,testSetup.GAME_ID_1,testSetup.GAME_ID_2).inOrder()
    }

}