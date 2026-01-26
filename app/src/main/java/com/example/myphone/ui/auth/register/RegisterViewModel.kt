package com.example.myphone.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.repository.AuthRepository
import com.example.myphone.utils.Resource
class RegisterViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<Resource<Boolean>>()
    val registerResult: LiveData<Resource<Boolean>> = _registerResult
    fun register(fullName: String, email: String, pass: String) {
        _registerResult.value = Resource.Loading

        // Đã sửa: Truyền fullName vào đây
        AuthRepository.register(email, pass, fullName) { isSuccess, message ->
            if (isSuccess) {
                _registerResult.value = Resource.Success(true)
            } else {
                _registerResult.value = Resource.Error(message ?: "Đăng ký thất bại")
            }
        }
    }
}
