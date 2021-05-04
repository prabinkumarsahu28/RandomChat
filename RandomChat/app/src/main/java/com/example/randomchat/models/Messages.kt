package com.example.randomchat.models

class Messages {
    var messageId: String? = null
    var message: String? = null
    var uId: String? = null
    var timeStamp: Long? = null
    var imgUrl: String? = null

    constructor(messageId: String?, message: String?, uId: String?, timeStamp: Long?, imgUrl: String?) {
        this.messageId = messageId
        this.message = message
        this.uId = uId
        this.timeStamp = timeStamp
        this.imgUrl = imgUrl
    }

    constructor(message: String?, uId: String?) {
        this.message = message
        this.uId = uId
    }

    constructor(message: String?, uId: String?, imgUrl: String?) {
        this.message = message
        this.uId = uId
        this.imgUrl = imgUrl
    }

    constructor() {}
}