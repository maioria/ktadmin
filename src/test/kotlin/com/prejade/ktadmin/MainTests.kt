package com.prejade.ktadmin

import com.prejade.ktadmin.modules.main.service.MainService
import com.prejade.ktadmin.modules.sys.model.AddUser
import com.prejade.ktadmin.modules.sys.service.SysUserService
import com.prejade.ktadmin.sys.InitDataComponent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MainTests {
    @Autowired
    lateinit var mainService: MainService

    @Autowired
    lateinit var userService: SysUserService

    @Autowired
    lateinit var initDataComponent: InitDataComponent

    @Test
    fun initData() {
        initDataComponent.initData()
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun addUser() {
        val model = AddUser()
        model.username = "admin"
        model.depId = 1
        userService.add(model)
    }

    @Test
    fun resetPassword() {
        mainService.resetPassword(1, "123456")
    }
}
