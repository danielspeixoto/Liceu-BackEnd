package com.liceu.server.domain.game

import java.time.Year
import java.util.*

class GameBoundary {

    interface IRepository {
        fun insert(game: GameToInsert): String
        fun ranking(month: Int, year: Int): List<Game>
    }

    interface ISubmit {
        fun run(game: GameSubmission): String
    }
}