package com.prejade.ktadmin.sys

import com.prejade.ktadmin.modules.sys.model.AddDep
import com.prejade.ktadmin.modules.sys.service.SysDepService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SysDepTest {
    @Autowired
    lateinit var depService: SysDepService

    @Test
    fun addDepartment() {
        val model = AddDep()
        model.name = "prejade"
        depService.add(model)
    }

}
