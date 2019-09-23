package com.liceu.server.domain.activities


class ActivityBoundary {
    interface IRepository{
        fun getActivitiesFromUser(userId: String, amount: Int, tags: List<String>): List<Activity>
        fun insertActivity(activityToInsert: ActivityToInsert): String
    }

    interface IGetActivitiesFromUser {
        fun run(userId: String,amount: Int, tags: List<String>): List<Activity>
    }

}