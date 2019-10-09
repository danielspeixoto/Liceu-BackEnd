package com.liceu.server.domain.report

import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.domain.util.slackIntegration.slackReport
import com.liceu.server.util.Logging
import java.lang.Exception


class SubmitReport(
        val reportRepository: ReportBoundary.IRepository
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
            if(report.tags.size > 5){
                throw OverflowSizeTagsException("Too many tags in report")
            }
            if(report.message.isNullOrEmpty()){
                throw OverflowSizeException("No message given")
            }
            if(report.message.length > 200){
                throw OverflowSizeMessageException("Too many characters in message report")
            }
            if(report.params.isEmpty()){
                throw OverflowSizeException("No parameters given")
            }
            if(report.params.size > 5){
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

            slackReport(report)

            val id = reportRepository.insert(ReportToInsert(
                    report.userId,
                    report.message,
                    report.tags,
                    report.params,
                    retrieveActualTimeStamp()
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to report.userId,
                    "reportMessag" to report.message,
                    "tagsAmount" to report.tags.size,
                    "paramsAmount" to report.params.size
            ))
            return id

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }

    }
}