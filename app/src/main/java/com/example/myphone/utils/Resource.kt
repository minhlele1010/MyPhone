package com.example.myphone.utils

sealed class Resource<out T> {
    // Trạng thái đang tải (Không cần dữ liệu)
    object Loading : Resource<Nothing>()
    // Trạng thái thành công (Chứa dữ liệu kiểu T)
    data class Success<out T>(val data: T) : Resource<T>()
    // Trạng thái lỗi (Chứa thông báo lỗi)
    data class Error(val message: String) : Resource<Nothing>()
}