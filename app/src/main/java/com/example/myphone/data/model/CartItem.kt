package com.example.myphone.data.model

data class CartItem(
    val product: Product = Product(),
    var quantity: Int = 0// Số lượng mua
)
