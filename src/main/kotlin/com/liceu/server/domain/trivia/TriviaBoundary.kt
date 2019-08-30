package com.liceu.server.domain.trivia


class TriviaBoundary {

    interface IRepository{
        fun insert(triviaQuestion: TriviaQuestionToInsert): String
        fun randomQuestions(tags: List<String>, amount: Int): List<TriviaQuestion>
        fun updateListOfComments(questionId: String,userId: String,author: String,comment: String): Long
        fun getTriviaById(questionId: String): TriviaQuestion
    }

    interface ISubmit{
        fun run(triviaQuestion: TriviaQuestionSubmission): String
    }

    interface IRandomQuestions{
        fun run(tags: List<String>,amount: Int): List<TriviaQuestion>
    }

    interface IUpdateListOfComments{
        fun run(questionId: String,userId: String,comment: String)
    }

}