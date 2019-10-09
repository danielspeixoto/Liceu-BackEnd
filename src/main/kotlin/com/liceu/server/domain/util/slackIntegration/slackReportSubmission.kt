package com.liceu.server.domain.util.slackIntegration

import com.liceu.server.domain.report.ReportSubmission
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import kotlin.concurrent.thread

fun slackReport(report: ReportSubmission){
    thread(start = true, name = "reportsThread"){
        khttp.post("https://hooks.slack.com/services/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",
                headers = mapOf(
                        "Content-type" to "application/json"
                ),
                json = slackMessageFormatter(report)
        )
    }
}





