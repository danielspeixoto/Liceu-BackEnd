package com.liceu.server.domain.challenge


class ChallengeBoundary {

    interface IRepository{
        fun createChallenge(matchmaking: ChallengeToInsert): String
        fun matchMaking(challengedId: String): Challenge?
        fun findById(id: String): Challenge?
    }

    interface IGetChallenge {
        fun run(userId: String): Challenge
    }


}