package com.liceu.server.domain.activities

import java.util.*
import kotlin.collections.HashMap

data class Activity (
    val id: String,
    val userId: String,
    val type: String,
    val params: HashMap<String,Any>,
    val submissionDate: Date
)

data class ActivitySubmission (
        val userId: String,
        val type: String,
        val params: HashMap<String,Any>
)

data class ActivityToInsert (
        val userId: String,
        val type: String,
        val params: HashMap<String,Any>,
        val submissionDate: Date
)

