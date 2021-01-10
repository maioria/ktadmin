package com.prejade.ktadmin.modules.sys.model

import java.util.*

class SysDepModel {
    var id: Int? = null
    var name: String? = null
    var status: String? = null
    var createTime: Date? = null
    var parentId: Int? = null
    var parentName: String? = null
}

class DepTree {
    var id: Int? = null
    var name: String? = null
    var children: MutableList<DepTree> = mutableListOf()
    var parentId: Int? = null
    var parentName: String? = null
    var status: String? = null
    var statusName: String? = null
    var createTime: Date? = null
    fun addChildren(treeModel: DepTree) {
        this.children.add(treeModel)
    }
}

class AddDep {
    lateinit var name: String
    var parentId: Int? = null
}
