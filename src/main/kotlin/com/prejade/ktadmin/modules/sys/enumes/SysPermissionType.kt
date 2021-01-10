package com.prejade.ktadmin.modules.sys.enumes

enum class SysPermissionType(private var nameValue: String) {
    MENU("菜单"),
    PERMISSION("权限");

    fun getNameValue(): String {
        return nameValue
    }
}
