package com.liceu.server.domain.game

import com.liceu.server.domain.global.GAME
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.util.Logging

class SubmitGame(
        val gameRepository: GameBoundary.IRepository
) : GameBoundary.ISubmit {

    companion object {
        const val EVENT_NAME = "game_submission"
        val TAGS = listOf(INSERTION, GAME)
    }

    override fun run(game: GameSubmission): String {
        try {
            var score = 0
            for(i in 0..(game.answers.size-1)){
                if(game.answers[i].selectedAnswer == game.answers[i].correctAnswer){
                    score++
                }
            }
            val id = gameRepository.insert(GameToInsert(
                    game.userId,
                    game.answers,
                    retrieveActualTimeStamp(),
                    game.timeSpent,
                    score
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