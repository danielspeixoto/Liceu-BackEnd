package com.liceu.server.domain.trivia

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import org.intellij.lang.annotations.Language

class SubmitTriviaQuestion(
        val TriviaRepository: TriviaBoundary.IRepository
): TriviaBoundary.ISubmit {


    companion object {
        const val EVENT_NAME = "trivia_question_submission"
        val TAGS = listOf(INSERTION, TRIVIA, QUESTION)
    }

    override fun run(triviaQuestion: TriviaQuestionSubmission): String {

        try {
            val prohibitedWords = listOf(
                    "porra", "caralho", "merda", "desgraça", "fdp", "filha da puta",
                    "filha da mãe", "vagabunda", "vadia","foda-se", "fudido", "vai se fuder",
                    "fodido"
            )
            if (triviaQuestion.question.isBlank()){
                throw OverflowSizeException("No question given")
            }
            if(triviaQuestion.question.length < 20) {
                throw UnderflowSizeException("Question is too short")
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
            if(triviaQuestion.tags.isEmpty()){
                throw OverflowSizeTagsException("No tags given in trivia question")
            }
            if(triviaQuestion.tags.size > 5){
                throw OverflowSizeTagsException("Too many tags in trivia question")
            }
            triviaQuestion.tags.forEach {
                if(it is String){
                    if(it.length > 100){
                        throw OverflowSizeException("Too many characters in tags")
                    }
                }else{
                    throw TypeMismatchException("Wrong type in trivia tags")
                }
            }

            prohibitedWords.forEach {
                if(triviaQuestion.question.toLowerCase().contains(it)){
                    throw InputValidationException("trivia question has prohibited words")
                }
            }

            val id = TriviaRepository.insert(TriviaQuestionToInsert(
                    triviaQuestion.userId,
                    triviaQuestion.question,
                    triviaQuestion.correctAnswer,
                    triviaQuestion.wrongAnswer,
                    triviaQuestion.tags
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "triviaQuestionUserId" to triviaQuestion.userId,
                    "triviaQuestion" to triviaQuestion.question,
                    "triviaQuestionCorrectAnswer" to triviaQuestion.correctAnswer,
                    "triviaQuestionWrongAnswer" to triviaQuestion.wrongAnswer,
                    "triviaQuestionTags" to triviaQuestion.tags,
                    "triviaQuestionTagsSize" to triviaQuestion.tags.size,
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