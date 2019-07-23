package com.liceu.server.domain.report


class ReportBoundary {

    interface IRepository{
        fun insert(report: ReportToInsert): String
    }

    interface ISubmit{
        fun run(report: ReportSubmission): String
    }

}