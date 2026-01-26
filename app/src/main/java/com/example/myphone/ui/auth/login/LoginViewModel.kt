package com.example.myphone.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.repository.AuthRepository

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, pass: String) {
        // 1. Báo trạng thái đang tải (để hiện vòng xoay)
        _loginResult.value = LoginResult.Loading
        // 2. Gọi Repository xử lý đăng nhập
        AuthRepository.login(email, pass) { isSuccess, message ->
            if (isSuccess) {
                // Thành công
                _loginResult.value = LoginResult.Success
            } else {
                // Thất bại (Kèm lời nhắn lỗi)
                _loginResult.value = LoginResult.Error(message ?: "Đăng nhập thất bại")
            }
        }
    }
}

// ▼▼▼ PHẦN QUAN TRỌNG: ĐỊNH NGHĨA CÁC TRẠNG THÁI ▼▼▼
sealed class LoginResult {
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}