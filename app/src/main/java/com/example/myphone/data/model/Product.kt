package com.example.myphone.data.model

import java.io.Serializable // 1. Phải có dòng import này

data class Product(
    val id: String = "",          // Phải có giá trị mặc định = ""
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val description: String = ""


) : Serializable
