package com.example.randomchat.models

import java.util.*

class UserStatus {
    var name: String? = null
    var dp: String? = null
    var lastUpdated: Long = 0
    var statuses: List<Status>? = null

    constructor() {}
    constructor(
        name: String?,
        profileImage: String?,
        lastUpdated: Long,
        statuses: ArrayList<Status>?
    ) {
        this.name = name
        this.dp = profileImage
        this.lastUpdated = lastUpdated
        this.statuses = statuses
    }

    constructor(
        name: String?,
        profileImage: String?,
        lastUpdated: Long,
    ) {
        this.name = name
        this.dp = profileImage
        this.lastUpdated = lastUpdated
    }
}