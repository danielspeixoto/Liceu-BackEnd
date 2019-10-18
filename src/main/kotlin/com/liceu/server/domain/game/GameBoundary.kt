package com.liceu.server.domain.game


class GameBoundary {

    interface IRepository {
        fun insert(game: GameToInsert): String
        fun ranking(month: Int, year: Int, amount: Int, start: Int): List<Game>
    }

    interface ISubmit {
        fun run(game: GameSubmission): String
    }


    interface IGameRanking {

        @Throws(Error::class)
        fun run(month: Int, year: Int, amount: Int, start: Int): List<Game>

    }
}