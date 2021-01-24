package com.prejade.ktadmin.modules.sys.dao

import com.prejade.ktadmin.modules.sys.entity.Attachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository : JpaRepository<Attachment, Int> {
    fun getByName(initName: String): Attachment
}
