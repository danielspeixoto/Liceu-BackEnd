package com.liceu.server.domain.util.activitiesInsertion

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.util.TimeStamp


    fun activityInsertion(activityRepository: ActivityBoundary.IRepository,user: String,type: String, params: HashMap<String,Any>) {

        activityRepository.insertActivity(ActivityToInsert(
                user,
                type,
                params,
                TimeStamp.retrieveActualTimeStamp()
        ))

    }

