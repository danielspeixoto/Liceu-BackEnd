package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.activities.Activity
import java.util.*
import kotlin.collections.HashMap


data class ActivityResponse (
        val id: String,
        val userId: String,
        val type: String,
        val params: HashMap<String,Any>,
        val submissionDate: Date
)

fun toActivityResponse(activity: Activity): ActivityResponse{
    return ActivityResponse(
            activity.id,
            activity.userId,
            activity.type,
            activity.params,
            activity.submissionDate
    )
}