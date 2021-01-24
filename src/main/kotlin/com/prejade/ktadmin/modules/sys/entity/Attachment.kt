package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.common.DateUtils
import com.prejade.ktadmin.common.Status
import com.prejade.ktadmin.modules.sys.enumes.FileType
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "attachment")
class Attachment() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
    var name: String? = null
    var initName: String? = null
    var path: String? = null
    var module: String? = null
    var objectId: String? = null
    var size: Long? = null
    var fileMd5: String? = null
    var ext: String? = null
    var createTime: Date? = null

    @Enumerated(EnumType.STRING)
    var status: Status = Status.NORMAL

    @Enumerated(EnumType.STRING)
    private val type: FileType? = null

    init {
        this.createTime = DateUtils.getCurrentTime()
    }
}
