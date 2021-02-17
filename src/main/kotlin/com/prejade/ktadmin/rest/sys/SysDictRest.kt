package com.prejade.ktadmin.rest.sys

import com.prejade.ktadmin.common.HttpResult
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("sys/dict")
class SysDictRest {
    @RequestMapping("{tableName}/{field}")
    fun value(@PathVariable tableName: String, @PathVariable field: String): HttpResult {
        return HttpResult.ok()
    }
}
