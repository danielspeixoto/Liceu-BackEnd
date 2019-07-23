package com.liceu.server.domain.trivia

class TriviaBoundary {

    interface IRepository{
        fun insert(triviaQuestion: TriviaQuestionToInsert): String
        //fun randomQuestions(amount: Int): List<TriviaQuestion>
    }

    interface ISubmit{
        fun run(triviaQuestion: TriviaQuestionSubmission): String
    }

//    interface IChallenge{
//        fun run()
//    }

}