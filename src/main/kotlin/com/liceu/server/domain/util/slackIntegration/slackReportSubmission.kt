package com.liceu.server.domain.util.slackIntegration

import com.liceu.server.domain.report.ReportSubmission
import kotlin.concurrent.thread

fun slackReport(report: ReportSubmission,reportWebhookURL: String){
    thread(start = true, name = "reportsThread"){
        khttp.post(reportWebhookURL,
                headers = mapOf(
                        "Content-type" to "application/json"
                ),
                json = slackMessageFormatter(report)
        )
    }
}





