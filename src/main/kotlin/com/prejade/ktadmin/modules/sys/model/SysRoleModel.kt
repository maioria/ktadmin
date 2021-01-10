package com.prejade.ktadmin.modules.sys.model


class SysRoleModel {
    var id: Int? = null
    var name: String? = null
    var label: String? = null
    var permissions: List<String> = arrayListOf()
    var dataPermissionType: String? = null
    var dataPermissionTypeName: String? = null
    var depDataPermissions: List<Int> = arrayListOf()
}

class AddRole {
    var id: Int? = null
    lateinit var name: String
    lateinit var label: String
    var permissions: String? = null
    lateinit var dataPermissionType: String
    var depDataPermissions: String? = null
}

data class RoleDataPermissionTypeModel(var label: String, var name: String)
