package com.liceu.server.data

import com.liceu.server.domain.report.Report
import com.liceu.server.domain.report.ReportBoundary
import com.liceu.server.domain.report.ReportToInsert
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository


class MongoReportRepository(
        val template: MongoTemplate
) : ReportBoundary.IRepository {

    override fun insert(report: ReportToInsert): String {
        val result = template.insert(MongoDatabase.MongoReport(
                ObjectId(report.userId),
                report.message,
                report.tags,
                report.params,
                report.submissionDate
        ))
        return result.id.toHexString()
    }

}