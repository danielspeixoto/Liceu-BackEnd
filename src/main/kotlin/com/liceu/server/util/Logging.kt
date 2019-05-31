package com.liceu.server.util

import net.logstash.logback.marker.Markers.append
import org.slf4j.LoggerFactory

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
                append("caller_method", stack.first[1]),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second),
                append("event", eventName),
                append("tags", tags),
                append("data", data)
        )
    }

    fun debug(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.debug(null,
                append("caller_method", stack.first[1]),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second),
                append("event", eventName),
                append("tags", tags),
                append("data", data)
        )
    }

    fun warn(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.warn(null,
                append("caller_method", stack.first[1]),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second),
                append("event", eventName),
                append("tags", tags),
                append("data", data)
        )
    }

    fun error(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        val stack = stack()
        logger.error(null,
                append("caller_method", stack.first[1]),
                append("caller_class", stack.first[0]),
                append("line_number", stack.second),
                append("event", eventName),
                append("tags", tags),
                append("data", data)
        )
    }
}