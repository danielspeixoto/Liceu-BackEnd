package com.liceu.server.domain.trivia

class TriviaBoundary {

    interface IRepository{
        fun insert(triviaQuestion: TriviaQuestionToInsert): String
    }

    interface ISubmit{
        fun run(triviaQuestion: TriviaQuestionSubmission): String
    }

}