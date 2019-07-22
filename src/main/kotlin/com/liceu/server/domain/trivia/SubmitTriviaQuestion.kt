package com.liceu.server.domain.trivia

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class SubmitTriviaQuestion(
        val TriviaRepository: TriviaBoundary.IRepository
): TriviaBoundary.ISubmit {


    companion object {
        const val EVENT_NAME = "trivia_question_submission"
        val TAGS = listOf(INSERTION, TRIVIA, QUESTION)
    }

    override fun run(triviaQuestion: TriviaQuestionSubmission): String {

        try {

            if (triviaQuestion.question.isEmpty()){
                throw OverflowSizeException("No question given")
            }
            if (triviaQuestion.question.length > 300){
                throw OverflowSizeException("Too many characters in question")
            }
            if(triviaQuestion.correctAnswer.length > 200){
                throw OverflowSizeException("Too many characters in correct answer")
            }
            if(triviaQuestion.wrongAnswer.length > 200){
                throw OverflowSizeException("Too many characters in wrong answer")
            }
            val id = TriviaRepository.insert(TriviaQuestionToInsert(
                    triviaQuestion.userId,
                    triviaQuestion.question,
                    triviaQuestion.correctAnswer,
                    triviaQuestion.wrongAnswer
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "triviaQuestionUserId" to triviaQuestion.userId,
                    "triviaQuestion" to triviaQuestion.question,
                    "triviaQuestionCorrectAnswer" to triviaQuestion.correctAnswer,
                    "triviaQuestionWrongAnswer" to triviaQuestion.wrongAnswer,
                    "triviaQuestionBodySize" to triviaQuestion.question.length
                    ))
            return id
        }
        catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }
}