package com.prejade.ktadmin.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

abstract class BaseService<T, ID> {
    protected open val logger: Logger = LoggerFactory.getLogger(javaClass)
    abstract fun getRepository(): JpaRepository<T, ID>
    fun save(entity: T) {
        getRepository().save(entity!!)
    }

    open fun get(id: ID): T {
        return getRepository().getOne(id!!)
    }

    open fun findByIdOrNull(id: ID): T? {
        return getRepository().findByIdOrNull(id)
    }

    open fun del(id: ID) {
        getRepository().deleteById(id!!)
    }
}
