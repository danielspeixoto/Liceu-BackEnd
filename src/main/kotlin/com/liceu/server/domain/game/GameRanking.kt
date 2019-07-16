package com.liceu.server.domain.game

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.lang.Exception

class GameRanking(val repo: GameBoundary.IRepository, val maxResults: Int): GameBoundary.IGameRanking {


    companion object {
        const val EVENT_NAME = "game_ranking"
        val TAGS = listOf(RETRIEVAL, GAME, RANKING)
    }

    override fun run(month: Int, year: Int, amount: Int): List<Game> {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS, TAGS, hashMapOf(
                    "action" to EVENT_NAME,
                    "value" to amount
            ))
        }

        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    MAX_RESULTS_OVERFLOW,
                    TAGS + listOf(OVERFLOW),
                    hashMapOf(
                            "action" to EVENT_NAME,
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        try {
            val games = repo.ranking(month, year, finalAmount)
            Logging.info(
                    EVENT_NAME,
                    TAGS,
                    hashMapOf(
                            "amount" to finalAmount,
                            "month" to month,
                            "year" to year,
                            "retrieved" to games.size
                    )
            )
            return games
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw Exception()
        }
    }


}