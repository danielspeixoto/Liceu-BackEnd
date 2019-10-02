package com.liceu.server.domain.util.activitiesInsertion

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import kotlin.concurrent.thread


fun activityInsertion(activityRepository: ActivityBoundary.IRepository,user: String,type: String, params: HashMap<String,Any>) {
    thread(start = true, name = "activitiesThread") {
        activityRepository.insertActivity(ActivityToInsert(
                user,
                type,
                params,
                retrieveActualTimeStamp()
        ))
    }
}

