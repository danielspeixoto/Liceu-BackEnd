package com.liceu.server.domain.notification

open class Notification(val title: String, val body: String, val action: String = "", val data: Map<String, Any> = hashMapOf())

class StartChallengeNotification(title: String, body: String) : Notification(title, body, "start_challenge")
class ENEMTrainingNotification(title: String, body: String) : Notification(title, body, "enem_training")
class ENEMTournamentNotification(title: String, body: String) : Notification(title, body, "enem_tournament")
class GoToWebPageNotification(title: String, body: String, url: String) : Notification(title, body, "webpage", mapOf(
        "url" to url
))

class AnswerChallengeNotification(title: String, body: String, challengeId: String, challengerId: String) :
        Notification(title, body, "answer_challenge", mapOf(
                "challengeId" to challengeId,
                "challengerId" to challengerId
        ))