package com.example.myphone.data.model

data class Order(
    val id: String = "",
    val items: List<CartItem> = emptyList(), // Lưu lại list hàng đã mua
    val totalPrice: Double = 0.0,            // Lưu tổng tiền lúc mua
    val dateOrder: Long = System.currentTimeMillis(), // Lưu thời gian mua
)