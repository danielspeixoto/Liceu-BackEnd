package com.liceu.server.presentation.v2

import com.liceu.server.domain.report.ReportBoundary
import com.liceu.server.domain.report.ReportSubmission
import com.liceu.server.domain.global.CONTROLLER
import com.liceu.server.domain.global.NETWORK
import com.liceu.server.domain.global.REPORT
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.ClassCastException
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException


@RestController
@RequestMapping("/v2/report")
class ReportController (
        @Autowired val submit: ReportBoundary.ISubmit
) {
    @Autowired
    lateinit var netUtils: NetworkUtils

    @PostMapping
    fun submit(
            @RequestBody body: HashMap<String, Any>,
            request: HttpServletRequest
    ): ResponseEntity<HashMap<String, Any>> {
        val eventName = "report_post"
        val eventTags = listOf(CONTROLLER, NETWORK, REPORT)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))

        return try{
            val userReq = body["userId"] as String? ?: throw ValidationException()
            val messageReq = body["message"] as String? ?: throw ValidationException()
            val tagsReq = body["tags"] as List<String>
            val paramsReq = body["params"] as HashMap<String,Any>

            val id = submit.run(ReportSubmission(
                    userReq,
                    messageReq,
                    tagsReq,
                    paramsReq
            ))
            ResponseEntity(hashMapOf<String,Any>(
                    "id" to id
            ), HttpStatus.OK)
        }catch(e: Exception){
            when(e) {
                is ValidationException, is ClassCastException -> {
                    Logging.error(
                            eventName,
                            eventTags,
                            e, data = networkData
                    )
                    ResponseEntity(HttpStatus.BAD_REQUEST)
                }
                else -> {
                    Logging.error(
                            eventName,
                            eventTags,
                            e, data = networkData
                    )
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        }

    }

}