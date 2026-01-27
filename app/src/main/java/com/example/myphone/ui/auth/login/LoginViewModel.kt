package com.example.myphone.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.repository.AuthRepository
import com.example.myphone.utils.Resource
class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Resource<Boolean>>()
    val loginResult: LiveData<Resource<Boolean>> = _loginResult
    fun login(email: String, pass: String) {
        // 1. Báo trạng thái đang tải (để hiện vòng xoay)
        _loginResult.value = Resource.Loading
        // 2. Gọi Repository xử lý đăng nhập
        AuthRepository.login(email, pass) { isSuccess, message ->
            if (isSuccess) {
                // Thành công
                _loginResult.value = Resource.Success(true)
            } else {
                // Thất bại (Kèm lời nhắn lỗi)
                _loginResult.value = Resource.Error(message ?: "Đăng nhập thất bại")
            }
        }
    }
}
