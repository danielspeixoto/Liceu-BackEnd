package com.liceu.server.domain.game

import com.liceu.server.domain.global.GAME
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.util.Logging
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class SubmitGame(
        val gameRepository: GameBoundary.IRepository
) : GameBoundary.ISubmit {

    companion object {
        const val EVENT_NAME = "game_submission"
        val TAGS = listOf(INSERTION, GAME)
    }

    override fun run(game: GameSubmission): String {
        try {
            val id = gameRepository.insert(GameToInsert(
                    game.userId,
                    game.answers,
                    Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant()),
                    game.timeSpent
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to game.userId,
                    "answersAmount" to game.answers.size
            ))
            return id
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }
}