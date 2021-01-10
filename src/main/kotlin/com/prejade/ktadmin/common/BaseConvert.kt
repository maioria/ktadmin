package com.prejade.ktadmin.common

import org.springframework.data.domain.Page

abstract class BaseConvert<E, M, A> {
    abstract fun castModel(entity: E): M
    abstract fun copyProperties(ori: E, tar: E)
    open fun castModel(entityList: List<E>): List<M> {
        val result = mutableListOf<M>()
        for (entity in entityList) {
            result.add(castModel(entity))
        }
        return result
    }

    abstract fun castEntity(model: A): E

    fun castModel(entityPage: Page<E>): PageModel<M> {
        val result = PageModel<M>()
        result.pageNo = entityPage.number + 1
        result.pageSize = entityPage.size
        result.total = entityPage.totalElements
        result.data = castModel(entityPage.content)
        return result
    }

    fun <T> castPageModel(page: Page<E>, cast: CastModelExtractor<E, T>): PageModel<T> {
        val pageModel = PageModel<T>()
        pageModel.total = page.totalElements
        pageModel.pageNo = page.number
        pageModel.pageSize = page.size
        val list = mutableListOf<T>()
        for (item in page.content) {
            list.add(cast.cast(item))
        }
        pageModel.data = list
        return pageModel
    }
}

interface CastModelExtractor<E, T> {
    fun cast(e: E): T
}
