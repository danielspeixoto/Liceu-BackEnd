package com.liceu.server.presentation.v2

import com.liceu.server.domain.game.GameBoundary
import com.liceu.server.domain.global.*
import com.liceu.server.presentation.util.converters.GameConverters.toGameResponse
import com.liceu.server.presentation.util.converters.GameResponse
import com.liceu.server.presentation.util.handleException
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/v2/ranking")
class RankingController(
        @Autowired val ranking: GameBoundary.IGameRanking
) {

    @Autowired
    lateinit var netUtils: NetworkUtils


    @GetMapping(produces=["application/json;charset=UTF-8"])
    fun get(
            @RequestParam(value = "month", defaultValue = "") month: Int,
            @RequestParam(value = "year", defaultValue = "") year: Int,
            @RequestParam(value = "amount", defaultValue = "0") amount: Int,
            @RequestParam(value = "start", defaultValue = "0") start: Int,
            request: HttpServletRequest

    ): ResponseEntity<List<GameResponse>> {
        val eventName = "ranking_get"
        val eventTags = listOf(CONTROLLER, NETWORK, GAME ,RANKING ,RETRIEVAL)
        val networkData = netUtils.networkData(request)

        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))
        return try {
            val result = ranking.run(month, year , amount, start)
            ResponseEntity(result.map { toGameResponse(it) }, HttpStatus.OK)
        } catch (e: Exception) {
            handleException(e, eventName, eventTags, networkData +
                    ("month" to month) +
                    ("year" to year) +
                    ("amount" to amount)
            )
        }
    }


}


