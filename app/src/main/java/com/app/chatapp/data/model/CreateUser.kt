package com.app.chatapp.data.model

data class CreateUser(
    var displayName: String = "",
    var email: String = "",
    var password: String = "",
    var birthdate: String="",
    var phone: String=""
)