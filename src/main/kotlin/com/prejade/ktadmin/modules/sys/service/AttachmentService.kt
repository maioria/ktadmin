package com.prejade.ktadmin.modules.sys.service

import com.prejade.ktadmin.ConfigConstant
import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.common.FileUtils
import com.prejade.ktadmin.modules.sys.dao.AttachmentRepository
import com.prejade.ktadmin.modules.sys.entity.Attachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.servlet.http.HttpServletResponse


@Service
@Transactional
class AttachmentService(val repository: AttachmentRepository, val constant: ConfigConstant) :
    BaseService<Attachment, Int>() {
    fun preview(name: String, response: HttpServletResponse) {
        response.contentType = "image/jpeg"
        val attachment = getByName(name)
        val path = constant.uploadPath + File.separator + attachment.path
        val file = File(path)
        val inputStream = FileInputStream(file)
        val os = response.outputStream
        val b = ByteArray(1024)
        while (inputStream.read(b) != -1) {
            os.write(b)
        }
        inputStream.close()
        os.flush()
        os.close()
    }

    fun getByName(name: String): Attachment {
        return repository.getByName(name)
    }

    override fun getRepository(): JpaRepository<Attachment, Int> {
        return repository
    }

    fun getFilePath(attachment: Attachment): String {
        return constant.uploadPath + File.separator + attachment.module + File.separator + attachment.name
    }

    fun saveFile(file: MultipartFile, module: String = "temp", objectId: String? = null): Attachment {
        val createTime = Date()
        val fileName = file.originalFilename
        val ext = FileUtils.getExt(fileName!!)
        val name = DigestUtils.md5DigestAsHex(("$createTime-$fileName").toByteArray()) + "." + ext
        val attachment = Attachment()
        attachment.name = name
        attachment.ext = ext
        attachment.fileMd5 = DigestUtils.md5DigestAsHex(file.bytes)
        attachment.initName = fileName
        attachment.path = module
        attachment.size = file.size
        attachment.module = module
        attachment.objectId = objectId

        save(attachment)

        val dest = File(constant.uploadPath + File.separator + module + File.separator + name)
        dest.mkdirs()
        file.transferTo(dest)

        return attachment
    }
}
