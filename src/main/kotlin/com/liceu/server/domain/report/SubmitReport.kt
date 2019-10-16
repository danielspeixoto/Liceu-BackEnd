package com.liceu.server.domain.report

import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.domain.util.slackIntegration.slackReport
import com.liceu.server.util.Logging
import java.lang.Exception


class SubmitReport(
        private val reportRepository: ReportBoundary.IRepository,
        private val reportWebhookURL: String,
        private val reportTagsAmount: Int,
        private val reportMessageLength: Int,
        private val reportParamsAmount: Int
): ReportBoundary.ISubmit {

    companion object {
        const val EVENT_NAME = "report_submission"
        val TAGS = listOf(INSERTION, REPORT)
    }

    override fun run(report: ReportSubmission): String {
        try {
            if(report.tags.isEmpty()){
                throw OverflowSizeException("No tags given")
            }
            if(report.tags.size > reportTagsAmount){
                throw OverflowSizeTagsException("Too many tags in report")
            }
            if(report.message.isEmpty()){
                throw OverflowSizeException("No message given")
            }
            if(report.message.length > reportMessageLength){
                throw OverflowSizeMessageException("Too many characters in message report")
            }
            if(report.params.isEmpty()){
                throw OverflowSizeException("No parameters given")
            }
            if(report.params.size > reportParamsAmount){
                throw OverflowSizeException("Too many parameters passed")
            }
            report.params.values.forEach {
                        if(it is String){
                            if(it.length > 100){
                                throw OverflowSizeMessageException("Too many characters")
                            }
                        }else if(!(it is Double) && !(it is Int)){
                            throw TypeMismatchException("Type not acceptable")
                        }
                    }
            val id = reportRepository.insert(ReportToInsert(
                    report.userId,
                    report.message,
                    report.tags,
                    report.params,
                    retrieveActualTimeStamp()
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to report.userId,
                    "reportMessage" to report.message,
                    "tagsAmount" to report.tags.size,
                    "paramsAmount" to report.params.size
            ))
            slackReport(report,reportWebhookURL)
            return id

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }

    }
}