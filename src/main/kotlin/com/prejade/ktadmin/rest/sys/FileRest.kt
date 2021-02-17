package com.prejade.ktadmin.rest.sys

import com.google.gson.JsonObject
import com.prejade.ktadmin.modules.sys.service.AttachmentService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

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
