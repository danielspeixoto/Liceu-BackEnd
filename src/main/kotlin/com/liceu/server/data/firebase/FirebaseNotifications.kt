package com.liceu.server.data.firebase

import com.liceu.server.domain.notification.NotificationBoundary

class FirebaseNotifications : NotificationBoundary.INotifier {

    override fun send(fcmToken: String, title: String, message: String): Boolean {
        return false
    }
}