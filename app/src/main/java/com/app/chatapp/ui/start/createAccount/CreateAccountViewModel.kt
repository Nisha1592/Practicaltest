package com.app.chatapp.ui.start.createAccount

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.chatapp.App
import com.app.chatapp.data.Event
import com.app.chatapp.data.EventObserver
import com.app.chatapp.data.db.repository.AuthRepository
import com.app.chatapp.data.db.repository.DatabaseRepository
import com.app.chatapp.data.model.CreateUser
import com.app.chatapp.data.Result
import com.app.chatapp.data.db.entity.User
import com.app.chatapp.data.db.repository.StorageRepository
import com.app.chatapp.ui.DefaultViewModel
import com.app.chatapp.util.*

import com.google.firebase.auth.FirebaseUser
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CreateAccountViewModel : DefaultViewModel() {

    private val dbRepository = DatabaseRepository()
    private val authRepository = AuthRepository()
    private val mIsCreatedEvent = MutableLiveData<Event<FirebaseUser>>()
    private val storageRepository = StorageRepository()

    val isCreatedEvent: LiveData<Event<FirebaseUser>> = mIsCreatedEvent
    val displayNameText = MutableLiveData<String>() // Two way
    val emailText = MutableLiveData<String>() // Two way
    val passwordText = MutableLiveData<String>()
    val confirmpasswordText = MutableLiveData<String>()// Two way
    val phoneText = MutableLiveData<String>()
    val dobText = MutableLiveData<String>()
    val isCreatingAccount = MutableLiveData<Boolean>()
    var imageuri = MutableLiveData<Uri>()
    val bbytearray= MutableLiveData<ByteArray>()

    private val _editImageEvent = MutableLiveData<Event<Unit>>()
    val editImageEvent: LiveData<Event<Unit>> = _editImageEvent

    private val _editdobEditext = MutableLiveData<Event<Unit>>()
    val editdobEditext: LiveData<Event<Unit>> = _editdobEditext


    var userid:String =""


    private fun createAccount() {
        isCreatingAccount.value = true
        val createUser =
            CreateUser(displayNameText.value!!, emailText.value!!, passwordText.value!!, dobText.value!!, phoneText.value!!)

        authRepository.createUser(createUser) { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {
                mIsCreatedEvent.value = Event(result.data!!)
                dbRepository.updateNewUser(User().apply {
                    info.id = result.data.uid
                    userid=result.data.uid
                    info.displayName = createUser.displayName
                    info.phone = createUser.phone
                    info.birthdate=createUser.birthdate
                    bbytearray.value?.let { changeUserImage(it) }

                })
            }

            if (result is Result.Success || result is Result.Error) isCreatingAccount.value = false

        }
    }

    fun createAccountPressed() {
        if (!isTextValid(2, displayNameText.value)) {
            mSnackBarText.value = Event("Display name is too short")
            return
        }

        if (!isEmailValid(emailText.value.toString())) {
            mSnackBarText.value = Event("Invalid email format")
            return
        }
        if (!isTextValid(6, passwordText.value)) {
            mSnackBarText.value = Event("Password is too short")
            return
        }

        if (!isTextValid(2, dobText.value)) {
            mSnackBarText.value = Event("Enter Date Of Birth")
            return
        }

        if (!isconfirmpassword(passwordText.value, confirmpasswordText.value)) {
            mSnackBarText.value = Event("Password does not match")
            return
        }

        createAccount()



    }

    fun changeUserImage(byteArray: ByteArray) {
        isCreatingAccount.value = true
        storageRepository.updateUserProfileImage(userid, byteArray) { result: Result<Uri> ->
            onResult(null, result)
            if (result is Result.Success) {

                dbRepository.updateUserProfileImageUrl(userid, result.data.toString())


                if (result is Result.Success || result is Result.Error) isCreatingAccount.value = false


            }
        }

    }



    fun changeUserImagePressed() {
        _editImageEvent.value = Event(Unit)
    }

    fun openDatePicker() {
        _editdobEditext.value = Event(Unit)
    }




}