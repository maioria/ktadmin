package com.prejade.ktadmin.modules.sys.model


class SysPermissionModel {
    var id: Int? = null
    lateinit var name: String
    lateinit var label: String
    lateinit var type: String
    lateinit var typeName: String
    var parentId: Int? = null
    var parentName: String? = null
}

class AddPermission {
    var id: Int? = null
    lateinit var name: String
    lateinit var label: String
    lateinit var type: String
    var parentName: String? = null
}

class PermissionTree {
    var id: Int? = null
    var name: String? = null
    var label: String? = null
    var type: String? = null
    var typeName: String? = null
    var parentName: String? = null
    var children: MutableList<PermissionTree> = mutableListOf()
    fun addChildren(treeModel: PermissionTree) {
        this.children.add(treeModel)
    }
}
