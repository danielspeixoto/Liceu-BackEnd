package com.liceu.server.domain.util.slackIntegration

import com.liceu.server.domain.report.ReportSubmission
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.concurrent.thread

fun slackReport(report: ReportSubmission,reportWebhookURL: String){
    var normalizedUrl = URLDecoder.decode( reportWebhookURL, "UTF-8" );
    thread(start = true, name = "reportsThread"){
        khttp.post(normalizedUrl,
                headers = mapOf(
                        "Content-type" to "application/json"
                ),
                json = slackMessageFormatter(report)
        )
    }
}





