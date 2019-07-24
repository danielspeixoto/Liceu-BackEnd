package com.liceu.server.integration

import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.ChallengeRepository
import com.liceu.server.data.MongoChallengeRepository
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
class TestMongoChallengeRepositoryIntegration {

    @Autowired
    lateinit var data: MongoChallengeRepository
    @Autowired
    lateinit var repo: ChallengeRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

}