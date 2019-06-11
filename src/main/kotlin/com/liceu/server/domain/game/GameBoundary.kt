package com.liceu.server.domain.game

class GameBoundary {

    interface IRepository {
        fun insert(game: GameToInsert): String
    }

    interface ISubmit {
        fun run(game: GameSubmission): String
    }
}