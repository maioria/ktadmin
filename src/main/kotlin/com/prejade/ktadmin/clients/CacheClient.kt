package com.prejade.ktadmin.clients

import java.util.concurrent.ConcurrentHashMap

interface CacheClient {
    fun set(key: String, value: String) {
        this.set(key, value, 1000 * 60 * 60 * 24)
    }

    /**
     * 单位 毫秒
     */
    fun set(key: String, value: String, expire: Long)

    fun get(key: String): String?

    fun del(key: String)

    fun getKeys(): Set<String>

    fun getKeysByPrefix(prefix: String): Set<String>

    fun empty()
}

/**
 * 基于内存的实现，不可以使用多例负载
 * 如果设置了时间，过期后资源并不会释放，只有查询下才行
 *
 * 如果需要负载或者更多功能的，请使用redis实现
 */
class BaseCacheClient : CacheClient {
    private val map: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    override fun set(key: String, value: String, expire: Long) {
        map[key] = value
        map[getHoldTimeKey(key)] = (System.currentTimeMillis() + expire).toString()
    }

    override fun getKeys(): Set<String> {
        return map.keys
    }

    override fun getKeysByPrefix(prefix: String): Set<String> {
        val result = mutableSetOf<String>()
        for (key in getKeys().filter { it.startsWith(prefix) && map[it] != null }) {
            result.add(key)
        }
        return result
    }

    override fun get(key: String): String? {
        if (checkCacheName(key)) {
            return map[key];
        }
        return null
    }

    override fun del(key: String) {
        remove(key)
    }

    override fun empty() {
        map.clear()
    }

    /**
     * 删除某个缓存
     * @param cacheName
     */
    private fun remove(cacheName: String) {
        map.remove(cacheName)
        map.remove(getHoldTimeKey(cacheName))
    }

    /**
     * 检查缓存对象是否存在，
     * 若不存在，则返回false
     * 若存在，检查其是否已过有效期，如果已经过了则删除该缓存并返回false
     * @param cacheName
     * @return
     */
    private fun checkCacheName(cacheName: String): Boolean {
        val cacheHoldTime = map[getHoldTimeKey(cacheName)]?.toLong()
        if (cacheHoldTime == null || cacheHoldTime == 0L) {
            return false
        }
        if (cacheHoldTime < System.currentTimeMillis()) {
            remove(cacheName)
            return false
        }
        return true
    }

    private fun getHoldTimeKey(key: String): String {
        return "_${key}_HoldTime"
    }

}
