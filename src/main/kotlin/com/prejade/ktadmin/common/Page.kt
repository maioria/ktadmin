package com.prejade.ktadmin.common

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object PageBuilder {
    fun of(pageNo: Int?, pageSize: Int?): Pageable {
        return of(pageNo, pageSize, "id", true)
    }

    fun of(pageNo: Int?, pageSize: Int?, orderBy: String, desc: Boolean): Pageable {
        val sortDesk: Sort.Direction = if (desc) Sort.Direction.DESC
        else Sort.Direction.ASC
        return of(pageNo, pageSize, Sort.by(sortDesk, orderBy))
    }

    fun of(pageNo: Int?, pageSize: Int?, sort: Sort): Pageable {
        return PageRequest.of((pageNo ?: 1) - 1, pageSize ?: 10, sort)
    }
}

class PageModel<T> {
    var total: Long? = null
    var pageNo: Int? = null
    var pageSize: Int? = null
    var data: List<T>? = null

    @JsonIgnore
    fun getFrom(): Int? {
        return if (pageSize == null || pageNo == null) {
            null
        } else (pageNo!! - 1) * pageSize!!
    }
}
