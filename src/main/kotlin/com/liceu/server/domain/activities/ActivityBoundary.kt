package com.liceu.server.domain.activities


class ActivityBoundary {
    interface IRepository{
        fun getActivitiesFromUser(userId: String, amount: Int): List<Activity>
        fun insertActivity(activityToInsert: ActivityToInsert): String
    }

    interface IInsertActivity {
        fun run (activitySubmission: ActivitySubmission): String
    }
    interface IGetActivitiesFromUser {
        fun run(userId: String,amount: Int): List<Activity>
    }

}