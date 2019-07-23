package com.liceu.server.domain.trivia

class TriviaBoundary {

    interface IRepository{
        fun insert(triviaQuestion: TriviaQuestionToInsert): String
        fun randomQuestions(tags: List<String>, amount: Int): List<TriviaQuestion>
    }

    interface ISubmit{
        fun run(triviaQuestion: TriviaQuestionSubmission): String
    }

    interface IRandomQuestions{
        fun run(tags: List<String>,amount: Int): List<TriviaQuestion>
    }

}