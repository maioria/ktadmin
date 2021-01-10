package com.prejade.ktadmin.modules.sys.convert

import com.prejade.ktadmin.common.BaseConvert
import com.prejade.ktadmin.modules.sys.entity.SysDep
import com.prejade.ktadmin.modules.sys.model.AddDep
import com.prejade.ktadmin.modules.sys.model.DepTree
import com.prejade.ktadmin.modules.sys.model.SysDepModel
import com.prejade.ktadmin.modules.sys.service.SysDepService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class SysDepConvert : BaseConvert<SysDep, SysDepModel, AddDep>() {
    @Autowired
    @Lazy
    private lateinit var sysDepService: SysDepService

    override fun castModel(entity: SysDep): SysDepModel {
        val model = SysDepModel()
        model.id = entity.id
        model.parentId = entity.parent?.id
        model.parentName = entity.parent?.name
        model.name = entity.name
        model.status = entity.status.name
        return model
    }

    override fun copyProperties(ori: SysDep, tar: SysDep) {
        ori.name = tar.name
        ori.parent = tar.parent
    }

    override fun castEntity(model: AddDep): SysDep {
        val entity = SysDep()
        entity.name = model.name
        if (model.parentId != null) entity.parent = sysDepService.get(model.parentId!!)
        return entity
    }

    fun castTree(sysDeps: List<SysDep>): List<DepTree> {
        val treeModels: MutableList<DepTree> = mutableListOf()
        val map = hashMapOf<Int, DepTree>()
        var treeModel: DepTree
        var parent: SysDep?
        for (entity in sysDeps) {
            treeModel = DepTree()
            treeModel.id = entity.id
            treeModel.status = entity.status.name
            treeModel.statusName = entity.status.nameValue
            treeModel.name = entity.name
            treeModel.parentId = entity.parent?.id
            treeModel.parentName = entity.parent?.name
            treeModel.createTime = entity.createTime
            parent = entity.parent
            map[entity.id!!] = treeModel
            if (parent != null && map[parent.id] != null) {
                map[parent.id]!!.addChildren(treeModel)
            } else {
                treeModels.add(treeModel)
            }
        }
        return treeModels
    }
}
