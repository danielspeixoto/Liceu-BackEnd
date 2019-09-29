package com.liceu.server.domain.notification

class NotificationBoundary {
    interface INotifier {
        fun send(fcmToken: String, notification: Notification): Boolean
    }
}