package com.app.chatapp.data.model

import com.app.chatapp.data.db.entity.Chat
import com.app.chatapp.data.db.entity.UserInfo

data class ChatWithUserInfo(
    var mChat: Chat,
    var mUserInfo: UserInfo
)
