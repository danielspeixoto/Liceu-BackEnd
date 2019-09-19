package com.liceu.server.domain.util.user

import com.liceu.server.util.Logging

fun logsUserAuthentication(EVENT_NAME: String, TAGS: List<String>,userId:String,timeBefore: Long){
    Logging.info(EVENT_NAME, TAGS, hashMapOf(
            "time" to System.currentTimeMillis() - timeBefore,
            "userId" to userId
    ))
}