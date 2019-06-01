package com.liceu.server.util

import net.logstash.logback.marker.Markers.append
import org.slf4j.LoggerFactory
import java.lang.Error

object Logging {

    const val INFO = "info"
    const val DEBUG = "debug"
    const val WARN = "warn"
    const val ERROR = "error"

    val logger = LoggerFactory.getLogger(this.javaClass)!!
    private fun stack(): Pair<List<String>, Int> {
        val stacktrace = Thread.currentThread().stackTrace
        val e = stacktrace[3]
        val methodName = e.methodName
        val className = e.className
        val lineNumber = e.lineNumber
        return listOf(className, methodName) to lineNumber
    }

    fun info(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.info(null,
                append("event", eventName),
                append("data", data),
                append("tags", tags),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second)
        )
    }

    fun debug(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.debug(null,
                append("event", eventName),
                append("data", data),
                append("tags", tags),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second)
        )
    }

    fun warn(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.warn(null,
                append("event", eventName),
                append("data", data),
                append("tags", tags),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second)
        )
    }

    fun error(eventName: String, tags: List<String> = listOf(), error: Exception, data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
//        TODO: Only when in DEV
        error.printStackTrace()
        logger.error(null,
                append("event", eventName + "_error"),
                append("data", data),
                append("tags", tags + listOf("error")),
                append("error", error),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second)
        )
    }
}