package com.example.myphone.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.User
import com.example.myphone.data.repository.UserRepository
import com.example.myphone.data.repository.AuthRepository
class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> = _updateStatus

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        UserRepository.getCurrentUser { fetchedUser ->
            if (fetchedUser != null) {
                _user.value = fetchedUser!!
            }
        }
    }

    fun updateUser(name: String, phone: String, address: String) {
        UserRepository.updateUserInfo(name, phone, address) { isSuccess ->
            if (isSuccess) {
                // Cập nhật UI ngay lập tức
                val currentUser = _user.value
                _user.value = currentUser?.copy(
                    fullName = name,
                    phoneNumber = phone,
                    address = address
                )
                _updateStatus.value = true
            } else {
                _updateStatus.value = false
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }
    fun logout() {
        AuthRepository.logout()
    }
}