package com.prejade.ktadmin.rest.main

import com.google.gson.JsonObject
import com.prejade.ktadmin.ConfigConstant
import com.prejade.ktadmin.common.BaseService
import com.prejade.ktadmin.common.DateUtils
import com.prejade.ktadmin.common.FileUtils
import com.prejade.ktadmin.common.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.DigestUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.persistence.*
import javax.servlet.http.HttpServletResponse


/**
 * @author tao
 *
 */
@RestController
@RequestMapping("file")
class UploadController(val attachmentService: AttachmentService) {
    @GetMapping("/{name}")
    fun preview(
        @PathVariable name: String,
        response: HttpServletResponse
    ) {
        attachmentService.preview(name, response)
    }

    @PostMapping("/upload")
    fun upload(@RequestParam("file") file: MultipartFile): String {
        if (file.isEmpty) {
            throw Exception("上传失败，请选择文件")
        }
        val attachment = attachmentService.saveFile(file)
        val data = JsonObject()
        data.addProperty("id", attachment.id)
        data.addProperty("path", attachment.path)
        return data.toString()
    }
}

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

    fun saveFile(file: MultipartFile, module: String = "temp", objectId: String? = null): Attachment {
        val createTime = Date()
        val fileName = file.originalFilename
        val ext = FileUtils.getExt(fileName!!)
        val name = DigestUtils.md5DigestAsHex(("$createTime-$fileName").toByteArray()) + "." + ext
        val attachment = Attachment()
        val relativePath = module + File.separator + name
        val path = constant.uploadPath + File.separator + relativePath
        attachment.name = name
        attachment.ext = ext
        attachment.fileMd5 = DigestUtils.md5DigestAsHex(file.bytes)
        attachment.initName = fileName
        attachment.path = relativePath
        attachment.size = file.size
        attachment.module = module
        attachment.objectId = objectId

        save(attachment)

        val dest = File(path)
        dest.mkdirs()
        file.transferTo(dest)

        return attachment
    }
}

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

@Repository
interface AttachmentRepository : JpaRepository<Attachment, Int> {
    fun getByName(initName: String): Attachment
}

enum class FileType(private val nameValue: String) {
    GENERAL("普通文件"), IMAGE("图片"), VIDEO("视频");
}
