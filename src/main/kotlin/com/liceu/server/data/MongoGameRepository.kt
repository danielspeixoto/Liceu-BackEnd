package com.liceu.server.data

import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game
import com.liceu.server.domain.game.GameToInsert
import com.liceu.server.domain.game.GameBoundary
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.text.DecimalFormat
import java.time.YearMonth


@Repository
class MongoGameRepository(
        val template: MongoTemplate
): GameBoundary.IRepository {
    private var lastRequest = arrayListOf<Game>()
    private var lastMonthRequest = -1

    override fun insert(game: GameToInsert): String {
        val result = template.insert(MongoDatabase.MongoGame(
                ObjectId(game.userId),
                game.answers.map { MongoDatabase.MongoAnswer(
                        ObjectId(it.questionId),
                        it.correctAnswer,
                        it.selectedAnswer
                ) },
                game.submissionDate,
                game.timeSpent
        ))
        return result.id.toHexString()
    }

    override fun ranking(month: Int, year: Int): List<Game> {
        val mFormat = DecimalFormat("00")
        val monthFormated = mFormat.format(month)
        val yearMonthObject = YearMonth.of(year, month);
        val daysInMonth = yearMonthObject.lengthOfMonth();

        if(month == lastMonthRequest){
            return lastRequest
        }
        val match = Aggregation.match(Criteria.where("submissionDate")
                .gte(Date.from(Instant.parse(year.toString()+"-"+monthFormated+"-01T00:00:00.00Z")))
                .lte(Date.from(Instant.parse(year.toString()+"-"+monthFormated+"-"+daysInMonth+"T00:00:00.00Z")))
        )
        val agg = Aggregation.newAggregation(match)
        val resultList = template.aggregate(agg,MongoDatabase.GAME_COLLECTION, MongoDatabase.MongoGame::class.java).toList()
        lastMonthRequest = month
        Collections.sort(resultList)

        lastRequest = arrayListOf()
        val userIdList = arrayListOf<ObjectId>()
        for(item in resultList){
            if(lastRequest.size >= 20){
                break
            }
            if(!userIdList.contains(item.userId)){
                lastRequest.add(toGame(item))
                userIdList.add(item.userId)
            }
        }
        //resultList -> ordenado -> pegar os 20 primeiros usuarios diferentes e salvar no lastRequest

        return lastRequest
    }



    fun toGame(doc: MongoDatabase.MongoGame): Game {
        return Game(
                doc.id.toHexString(),
                doc.userId.toHexString(),
                doc.answers.map { Answer(
                        it.questionId.toHexString(),
                        it.correctAnswer,
                        it.selectedAnswer
                ) },
                doc.submissionDate,
                doc.timeSpent
        )
    }
}