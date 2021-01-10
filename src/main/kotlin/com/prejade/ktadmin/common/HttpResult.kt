package com.prejade.ktadmin.common


class HttpResult {
    var code = 200
    var msg: String? = null
    var data: Any? = null

    companion object {

        fun error(msg: String): HttpResult {
            return error(500, msg)
        }

        @JvmOverloads
        fun error(code: Int = 500, msg: String = "未知异常，请联系管理员"): HttpResult {
            val r = HttpResult()
            r.code = code
            r.msg = msg
            return r
        }

        fun ok(msg: String): HttpResult {
            val r = HttpResult()
            r.data = msg
            return r
        }

        fun ok(data: Any?): HttpResult {
            val r = HttpResult()
            r.data = data
            return r
        }

        fun ok(): HttpResult {
            return HttpResult()
        }
    }
}
