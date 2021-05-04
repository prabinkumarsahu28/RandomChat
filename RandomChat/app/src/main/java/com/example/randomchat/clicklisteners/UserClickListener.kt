package com.example.randomchat.clicklisteners

import com.example.randomchat.models.Users

interface UserClickListener {
    fun onUserClicked(user: Users)
}