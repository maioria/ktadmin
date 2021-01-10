package com.prejade.ktadmin.modules.sys.enumes

enum class RoleDataPermissionType(private var nameValue: String) {
    ALL("所有数据"),
    DEPARTMENT("部门下数据"),
    DEPARTMENTS("部门及子部门数据"),
    SETTING("自定义权限"),
    USER("普通用户权限");

    fun getNameValue(): String {
        return nameValue
    }
}
