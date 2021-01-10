package com.prejade.ktadmin.common

import com.google.gson.JsonObject
import eu.bitwalker.useragentutils.Browser
import eu.bitwalker.useragentutils.UserAgent
import org.lionsoul.ip2region.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

import java.io.FileNotFoundException

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource


/**
 * @author tao
 *
 */
object FileUtils {
    fun getExt(fileName: String): String {
        val splits = fileName.split(".")
        return splits.last()
    }
}

object StringUtils {
    fun isEmpty(arg: String?): Boolean {
        return arg == null || arg.trim().isEmpty()
    }

    fun castIntList(arg: String): List<Int> {
        val result = mutableListOf<Int>()
        for (item in arg.split(",")) {
            result.add(item.toInt())
        }
        return result
    }
}

object DateUtils {
    fun getCurrentTime(): Date {
        return Date()
    }

    fun getCurrentTimestamp(): Timestamp {
        return Timestamp(getCurrentTime().time)
    }
}

object ServletUtils {
    fun getIp(request: HttpServletRequest): String? {
        return request.getHeader("x-forwarded-for") ?: request.remoteAddr
    }

    fun getBrowser(request: HttpServletRequest): String? {
        val userAgent: UserAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"))
        val browser: Browser = userAgent.browser
        return browser.getName()
    }

    fun getHttpServletRequest(): HttpServletRequest {
        return (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()) as ServletRequestAttributes).request
    }

    fun outputJson(response: HttpServletResponse, status: Int, code: Int, msg: String) {
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.status = status
        val out = response.writer
        val jb = JsonObject()
        jb.addProperty("code", code)
        jb.addProperty("msg", msg)
        out.println(jb.toString())
        out.flush()
        out.close()
    }

    fun outputJson(response: HttpServletResponse, code: Int, msg: String) {
        outputJson(response, 200, code, msg)
    }
}

object Ip2Region {
    private var config: DbConfig? = null
    private var searcher: DbSearcher? = null
    private val logger: Logger = LoggerFactory.getLogger(Ip2Region::class.java.name)

    fun parseIp(ip: String?): String? {
        val isIpAddress = Util.isIpAddress(ip)
        if (isIpAddress) {
            try {
                return searcher!!.btreeSearch(ip)?.region
            } catch (e: IOException) {
                logger.warn("ip2region parse error" + e.message)
            }
        }
        return null
    }

    init {
        val dbfile = "classpath:ip2region/ip2region.db"
        try {
            config = DbConfig()
            searcher = DbSearcher(config, ClassPathResource(dbfile).path)
        } catch (e: DbMakerConfigException) {
            logger.warn("ip2region config init exception:" + e.message)
        } catch (e: FileNotFoundException) {
            logger.warn("ip2region file not found" + e.message)
        }
    }
}
