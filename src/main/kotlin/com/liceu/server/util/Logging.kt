package com.liceu.server.util

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object Logging {

    const val INFO = "info"
    const val DEBUG = "debug"
    const val WARN = "warn"
    const val ERROR = "error"

    private fun log(level: String, eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {

    }

    fun info(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        log(INFO, eventName, tags, data)
    }

    fun debug(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        log(DEBUG, eventName, tags, data)
    }

    fun warn(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        log(WARN, eventName, tags, data)
    }

    fun error(eventName: String, tags: List<String> = listOf(), data: HashMap<String, Any> = hashMapOf()) {
        log(ERROR, eventName, tags, data)
    }
}