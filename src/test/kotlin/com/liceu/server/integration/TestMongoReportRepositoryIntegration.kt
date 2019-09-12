package com.liceu.server.integration

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoReportRepository
import com.liceu.server.data.ReportRepository
import com.liceu.server.data.util.converters.toReport
import com.liceu.server.domain.report.ReportToInsert
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
class TestMongoReportRepositoryIntegration {

    @Autowired
    lateinit var data: MongoReportRepository
    @Autowired
    lateinit var reportRepo: ReportRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insert_Valid_CanBeRetrieved() {

        val id = data.insert(ReportToInsert(
                testSetup.USER_ID_1,
                "esse e um teste ahahahahah",
                listOf(
                        "teste1",
                        "teste2",
                        "teste3haha"
                        ),
                hashMapOf(
                    "oioi" to 2,
                    "lets go" to "iha"
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z"))
        ))

        val report = toReport(reportRepo.findById(id).get())

        Truth.assertThat(report.userId).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(report.message).isEqualTo("esse e um teste ahahahahah")
        Truth.assertThat(report.tags[0]).isEqualTo("teste1")
        Truth.assertThat(report.tags[1]).isEqualTo("teste2")
        Truth.assertThat(report.tags[2]).isEqualTo("teste3haha")
        Truth.assertThat(report.params["oioi"]).isEqualTo(2)
        Truth.assertThat(report.params["lets go"]).isEqualTo("iha")
    }
}