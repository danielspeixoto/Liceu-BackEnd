package com.liceu.server.domain.challenge


class ChallengeBoundary {

    interface IRepository{
        fun createChallenge(matchmaking: ChallengeToInsert): String
        fun updateChallenge(): Challenge
    }



}