package com.prejade.ktadmin.modules.sys.enumes

enum class LeaveStatus(private var nameValue: String) {
    ENTRY("在职"),
    LEAVE("离职");

    fun getNameValue(): String {
        return nameValue
    }
}
