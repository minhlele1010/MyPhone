package com.example.myphone.ui.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.Order
import com.example.myphone.data.repository.CartRepository

class OrderViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    // Hàm gọi Repository lấy dữ liệu
    fun loadHistory() {
        CartRepository.getOrderHistory { list ->
            _orders.value = list
        }
    }
}