package com.liceu.server.data.firebase

import com.liceu.server.domain.global.EmptyException
import com.liceu.server.domain.notification.NotificationBoundary

class FirebaseNotifications(val serverKey: String) : NotificationBoundary.INotifier {

    override fun send(fcmToken: String, title: String, message: String): Boolean {
        if (fcmToken == "") {
            throw EmptyException()
        }
        val response = khttp.post("https://fcm.googleapis.com/fcm/send",
                headers = mapOf(
                        "Authorization" to "key=$serverKey",
                        "Content-type" to "application/json"
                ),
                json = mapOf(
                "to" to fcmToken,
                        "notification" to mapOf(
                                "body" to message,
                                "title" to title
                        )
                )
        )
        return response.statusCode == 200 && response.jsonObject["success"] == 1
    }
}