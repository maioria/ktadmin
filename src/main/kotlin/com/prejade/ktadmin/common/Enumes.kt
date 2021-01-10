package com.prejade.ktadmin.common

enum class Status : BaseEnum {
    DELETE("删除"),
    NORMAL("正常"),
    DISABLED("失效");

    override var nameValue: String
    override var meaning: String? = null

    constructor(nameValue: String) {
        this.nameValue = nameValue
    }

    constructor(nameValue: String, meaning: String?) {
        this.nameValue = nameValue
        this.meaning = meaning
    }
}

enum class ApproveStatus : BaseEnum {
    NOT("未审批"),
    APPROVED("审批通过"),
    NOT_APPROVED("审批不通过");

    override var nameValue: String
    override var meaning: String? = null

    constructor(nameValue: String) {
        this.nameValue = nameValue
    }

    constructor(nameValue: String, meaning: String?) {
        this.nameValue = nameValue
        this.meaning = meaning
    }
}

enum class Gender : BaseEnum {
    NOT("未知"),
    MALE("男"),
    FEMALE("女");

    override var nameValue: String
    override var meaning: String? = null

    constructor(nameValue: String) {
        this.nameValue = nameValue
    }

    constructor(nameValue: String, meaning: String?) {
        this.nameValue = nameValue
        this.meaning = meaning
    }
}
