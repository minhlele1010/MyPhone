package com.example.myphone.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.Product
import com.example.myphone.data.repository.ProductRepository

class HomeViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    init {
        loadDataFromFirebase()
    }
    private fun loadDataFromFirebase() {
        // Gọi Repository và chờ kết quả trả về
        ProductRepository.getAllProducts { list ->
            // Khi có dữ liệu thật từ Firebase trả về, ta cập nhật lên giao diện
            _products.value = list
        }
    }
}