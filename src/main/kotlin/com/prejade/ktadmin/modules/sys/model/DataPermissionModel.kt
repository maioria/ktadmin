package com.prejade.ktadmin.modules.sys.model

/**
 * users一定不为空，因为最低的权限也是包括自己的
 *
 */
class DataPermissionModel {
    var all: Boolean = false
    var users: MutableSet<Int> = mutableSetOf()
    var deps: MutableSet<Int> = mutableSetOf()
}
