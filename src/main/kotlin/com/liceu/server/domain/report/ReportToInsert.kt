package com.liceu.server.domain.report

import java.util.*

data class ReportToInsert(
        val userId: String,
        val message: String,
        val tags: List<String>,
        val params: HashMap<String,Any>,
        val submissionDate: Date
)

data class ReportSubmission(
        val userId: String,
        val message: String,
        val tags: List<String>,
        val params: HashMap<String,Any>
)


data class Report(
        val userId: String,
        val message: String,
        val tags: List<String>,
        val params: HashMap<String,Any>,
        val submissionDate: Date
)