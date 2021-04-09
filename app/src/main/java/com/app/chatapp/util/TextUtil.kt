package com.app.chatapp.util

fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isTextValid(minLength: Int, text: String?): Boolean {
    if (text.isNullOrBlank() || text.length < minLength) {
        return false
    }
    return true
}


fun isconfirmpassword(password: String?,confirmpassword:String?): Boolean {
    if(!password.equals(confirmpassword))
    {
       return false
    }
    return true
}

