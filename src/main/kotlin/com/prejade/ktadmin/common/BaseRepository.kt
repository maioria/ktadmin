package com.prejade.ktadmin.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import javax.persistence.EntityManager

@NoRepositoryBean
interface BaseRepository<T, ID> : JpaRepository<T, ID>

abstract class BaseRepositoryImpl<T> {
    abstract fun getEntityManager(): EntityManager

    inline fun <reified T> findByHql(selSql: String): List<T> {
        val result = mutableListOf<T>()
        for (o in getEntityManager().createQuery(selSql).resultList) {
            if (o is T) result.add(o)
            else throw Exception("错误的类型")
        }
        return result
    }

    /**
     * limit操作现在先写到接口中
     */
    inline fun <reified T> findPageDataByHql(selSql: String, countSql: String, pageable: Pageable): Page<T> {
        val content = mutableListOf<T>()
        val query = getEntityManager().createQuery(selSql)
        query.maxResults = pageable.pageSize
        query.firstResult = pageable.offset.toInt()
        for (o in query.resultList) {
            if (o is T) content.add(o)
            else throw Exception("错误的类型")
        }
        return PageImpl(content, pageable, getEntityManager().createQuery(countSql).singleResult as Long)
    }

    inline fun <reified T> findPageDataBySql(selSql: String, countSql: String, pageable: Pageable): Page<T> {
        val content = mutableListOf<T>()
        val query = getEntityManager().createNativeQuery(selSql)
        for (o in query.resultList) {
            if (o is T) content.add(o)
            else throw Exception("错误的类型")
        }
        return PageImpl(content, pageable, getEntityManager().createNativeQuery(countSql).singleResult as Long)
    }
}
