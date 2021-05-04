package com.example.randomchat.models

import java.io.Serializable

class Users : Serializable {
    var userName: String? = null
    var about: String? = null
    private var email: String? = null
    private var password: String? = null
    var userId: String? = null
    var lastMessage: String? = null
    var dp: String? = null
    private var timeStamp: Long? = null
    var phone: Long? = null

    constructor(
        userName: String?,
        about: String?,
        email: String?,
        password: String?,
        userId: String?,
        lastMessage: String?,
        dp: String?,
        timeStamp: Long?,
        phone: Long?,
    ) {
        this.userName = userName
        this.about = about
        this.email = email
        this.password = password
        this.userId = userId
        this.lastMessage = lastMessage
        this.dp = dp
        this.timeStamp = timeStamp
        this.phone = phone
    }

    constructor() {}
    constructor(userName: String?, email: String?, userId: String?, dp: String?) {
        this.userName = userName
        this.email = email
        this.userId = userId
        this.dp = dp
    }

    constructor(userName: String?, email: String?, password: String?) {
        this.userName = userName
        this.email = email
        this.password = password
    }

    constructor(userName: String?, phone: Long?) {
        this.userName = userName
        this.phone = phone
    }
}