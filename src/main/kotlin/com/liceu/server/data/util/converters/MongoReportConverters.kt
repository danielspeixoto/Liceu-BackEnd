package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.report.Report


fun toReport(answer: MongoDatabase.MongoReport): Report {
    return Report(
            answer.userId.toHexString(),
            answer.message,
            answer.tags,
            answer.params,
            answer.submissionDate
    )
}