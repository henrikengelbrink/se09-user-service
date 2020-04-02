package se09.user.service.utils

import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GlobalLogger {

    private val log: Logger = LoggerFactory.getLogger("jsonLogger")

    fun info(jsonLog: Map<String, Any>) {
        val result = Klaxon().toJsonString(jsonLog)
        log.info(result)
    }

    fun error(jsonLog: Map<String, Any>) {
        val result = Klaxon().toJsonString(jsonLog)
        log.error(result)
    }

}