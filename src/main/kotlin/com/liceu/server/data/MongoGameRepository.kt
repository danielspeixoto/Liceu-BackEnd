package com.liceu.server.data

import com.liceu.server.data.util.converters.toGame
import com.liceu.server.domain.game.*
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.findDistinct
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

        private var gameList = arrayListOf<Game>()
//    private var lastMonthRequest = -1
//    private var lastTimeStampRequest = -1

    override fun insert(game: GameToInsert): String {
        val result = template.insert(MongoDatabase.MongoGame(
                ObjectId(game.userId),
                game.answers.map { MongoDatabase.MongoAnswer(
                        ObjectId(it.questionId),
                        it.correctAnswer,
                        it.selectedAnswer
                ) },
                game.submissionDate,
                game.timeSpent,
                game.score
        ))
        return result.id.toHexString()
    }

    override fun ranking(month: Int, year: Int, amount: Int): List<Game> {
        val startFunction = System.currentTimeMillis()
        val mFormat = DecimalFormat("00")
        val monthFormated = mFormat.format(month)
        val yearMonthObject = YearMonth.of(year, month);
        val daysInMonth = yearMonthObject.lengthOfMonth();

        val match = Aggregation.match(Criteria.where("submissionDate")
                .gte(Date.from(Instant.parse(year.toString()+"-"+monthFormated+"-01T00:00:00.00Z")))
                .lte(Date.from(Instant.parse(year.toString()+"-"+monthFormated+"-"+daysInMonth+"T00:00:00.00Z")))
                .and("score").ne(null)

        )
        val sortFields = Aggregation.sort(Sort.Direction.DESC, "score")
                .and(Sort.Direction.ASC,"timeSpent")
        val amountRetrieved = Aggregation.limit(amount.toLong()+10)
        val agg = Aggregation.newAggregation(match,sortFields,amountRetrieved)
        val resultList = template.aggregate(agg,MongoDatabase.GAME_COLLECTION, MongoDatabase.MongoGame::class.java).toList()

        gameList = arrayListOf()
        val userIdList = arrayListOf<ObjectId>()
        for(item in resultList){
            if(gameList.size >= amount){
                break
            }
            if(!userIdList.contains(item.userId)){
                gameList.add(toGame(item))
                userIdList.add(item.userId)
            }
        }
        val endFunction = System.currentTimeMillis()
        val duration = endFunction-startFunction
        Logging.info(
                "game_ranking_benchmark",
                listOf(GAME, RANKING, BENCHMARK),
                hashMapOf(
                        "amount" to amount,
                        "duration" to duration
                )
        )
        return gameList
    }

}