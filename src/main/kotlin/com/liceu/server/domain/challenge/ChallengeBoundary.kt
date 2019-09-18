package com.liceu.server.domain.challenge


class ChallengeBoundary {

    interface IRepository{
        fun createChallenge(matchmaking: ChallengeToInsert): Challenge
        fun matchMaking(challengedId: String): Challenge?
        fun verifyDirectChallenges(challengedId: String): Challenge?
        fun updateAnswers(challengeId: String, isChallenger: Boolean,answers: List<String>, score: Int): Long
        fun findById(challengeId: String): Challenge
    }

    interface IGetChallenge {
        fun run(userId: String): Challenge
    }

    interface IUpdateAnswers{
        fun run(challengeId: String, player: String,answers: List<String>)
    }

    interface ICreateChallenge {
        fun run(challengerId: String,challengedId: String): Challenge
    }

    interface IAcceptDirectChallenge {
        fun run(challengeId: String, userId: String): Challenge
    }


}