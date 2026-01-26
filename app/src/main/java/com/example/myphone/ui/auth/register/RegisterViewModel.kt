package com.example.myphone.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(fullName: String, email: String, pass: String) {
        _registerResult.value = RegisterResult.Loading

        // Đã sửa: Truyền fullName vào đây
        AuthRepository.register(email, pass, fullName) { isSuccess, message ->
            if (isSuccess) {
                _registerResult.value = RegisterResult.Success
            } else {
                _registerResult.value = RegisterResult.Error(message ?: "Đăng ký thất bại")
            }
        }
    }
}

sealed class RegisterResult {
    object Loading : RegisterResult()
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}