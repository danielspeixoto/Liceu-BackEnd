package com.liceu.server.domain.util.slackIntegration

import com.liceu.server.domain.report.ReportSubmission
import com.restfb.json.Json
import com.restfb.json.JsonObject
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject

fun slackMessageFormatter(report: ReportSubmission): JSONObject{
    val json = JSONObject()
    val attachments = JSONArray()
    val tagsFields = JSONArray()
    val paramsFields = JSONArray()

    json["text"] = report.message+" outside";
    json["username"] = report.userId;
    report.tags.forEach {
        var eachField = hashMapOf(
                "value" to it,
                "short" to false
        )
        tagsFields.add(eachField)
    }

    report.params.forEach {
        var eachField = hashMapOf(
                "value" to it,
                "short" to false
        )
        paramsFields.add(eachField)
    }

    var tagsContainer = hashMapOf(
            "color" to "#439FE0",
            "author_name" to "Tags",
            "fields" to tagsFields
    )
    var paramsContainer = hashMapOf(
            "color" to "#439FE0",
            "author_name" to "Parameters",
            "fields" to paramsFields
    )
    attachments.add(tagsContainer)
    attachments.add(paramsContainer)
    json["attachments"] = attachments

    return json

}