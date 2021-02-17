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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.util.ObjectUtils
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec


/**
 * @author tao
 *
 */
object FileUtils {
    private val SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator
    fun getExt(fileName: String): String {
        val splits = fileName.split(".")
        return splits.last()
    }

    /**
     * inputStream 转 File
     */
    @Throws(Exception::class)
    fun inputStreamToFile(ins: InputStream, name: String): File {
        val file = File(SYS_TEM_DIR + name)
        if (file.exists()) {
            return file
        }
        val os: OutputStream = FileOutputStream(file)
        var bytesRead: Int
        val len = 8192
        val buffer = ByteArray(len)
        while (ins.read(buffer, 0, len).also { bytesRead = it } != -1) {
            os.write(buffer, 0, bytesRead)
        }
        os.close()
        ins.close()
        return file
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
}

object ServletUtils {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    fun getIp(request: HttpServletRequest): String? {
        val unknown = "unknown"
        var ip = request.getHeader("x-forwarded-for")
        if (ip == null || ip.isEmpty() || unknown.equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || unknown.equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || unknown.equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        val comma = ","
        val localhost = "127.0.0.1"
        if (ObjectUtils.nullSafeEquals("0:0:0:0:0:0:0:1", ip)) ip = localhost

        if (ip!!.contains(comma)) {
            ip = ip.split(",").toTypedArray()[0]
        }
        if (localhost == ip) {
            try {
                ip = InetAddress.getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                logger.error(e.message, e)
            }
        }
        return ip
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
        if (ip == null) return null
        val isIpAddress = Util.isIpAddress(ip)
        if (isIpAddress) {
            try {
                val region = searcher!!.btreeSearch(ip)?.region ?: return null
                var address = region.replace("0|", "")
                val symbol = '|'
                if (address[address.length - 1] == symbol) {
                    address = address.substring(0, address.length - 1)
                }
                return if (address == "内网IP|内网IP") "内网IP" else address
            } catch (e: IOException) {
                logger.warn("ip2region parse error" + e.message)
            }
        }
        return null
    }

    init {
        val dbFile = "ip2region/ip2region.db"
        val name = "ip2region.db"
        try {
            config = DbConfig()
            searcher = DbSearcher(config, FileUtils.inputStreamToFile(ClassPathResource(dbFile).inputStream, name).path)
        } catch (e: DbMakerConfigException) {
            logger.warn("ip2region config init exception:" + e.message)
        } catch (e: FileNotFoundException) {
            logger.warn("ip2region file not found" + e.message)
        }
    }
}

object EncryptUtils {
    private const val STR_PARAM = "drgd@fd3"
    private var cipher: Cipher? = null
    private val IV = IvParameterSpec(STR_PARAM.toByteArray(StandardCharsets.UTF_8))

    @Throws(Exception::class)
    private fun getDesKeySpec(source: String?): DESKeySpec? {
        if (source == null || source.isEmpty()) {
            return null
        }
        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
        return DESKeySpec(STR_PARAM.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * 对称加密
     */
    @Throws(Exception::class)
    fun desEncrypt(source: String): String {
        val desKeySpec = getDesKeySpec(source)
        val keyFactory = SecretKeyFactory.getInstance("DES")
        val secretKey = keyFactory.generateSecret(desKeySpec)
        cipher!!.init(Cipher.ENCRYPT_MODE, secretKey, IV)
        return byte2hex(
            cipher!!.doFinal(source.toByteArray(StandardCharsets.UTF_8))
        ).toUpperCase()
    }

    /**
     * 对称解密
     */
    @Throws(Exception::class)
    fun desDecrypt(source: String): String {
        val src = hex2byte(source.toByteArray(StandardCharsets.UTF_8))
        val desKeySpec = getDesKeySpec(source)
        val keyFactory = SecretKeyFactory.getInstance("DES")
        val secretKey = keyFactory.generateSecret(desKeySpec)
        cipher!!.init(Cipher.DECRYPT_MODE, secretKey, IV)
        val retByte = cipher!!.doFinal(src)
        return String(retByte)
    }

    private fun byte2hex(inStr: ByteArray): String {
        var stmp: String
        val out = StringBuilder(inStr.size * 2)
        for (b in inStr) {
            stmp = Integer.toHexString(b.toInt() and 0xFF)
            if (stmp.length == 1) {
                // 如果是0至F的单位字符串，则添加0
                out.append("0").append(stmp)
            } else {
                out.append(stmp)
            }
        }
        return out.toString()
    }

    private fun hex2byte(b: ByteArray): ByteArray {
        val size = 2
        require(b.size % size == 0) { "长度不是偶数" }
        val b2 = ByteArray(b.size / 2)
        var n = 0
        while (n < b.size) {
            val item = String(b, n, 2)
            b2[n / 2] = item.toInt(16).toByte()
            n += size
        }
        return b2
    }
}

object ThrowableUtils {
    /**
     * 获取堆栈信息
     */
    fun getStackTrace(throwable: Throwable): String {
        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            throwable.printStackTrace(pw)
            return sw.toString()
        }
    }
}
