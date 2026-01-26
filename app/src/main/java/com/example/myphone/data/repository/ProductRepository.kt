package com.example.myphone.data.repository

import com.example.myphone.R
import com.example.myphone.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
object ProductRepository {
    private val db  = FirebaseFirestore.getInstance()
    fun getAllProducts(onResult: (List<Product>) -> Unit) {

        // Truy cập vào bảng (collection) tên là "products"
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                // Nếu thành công:
                val list = mutableListOf<Product>()

                for (document in documents) {
                    // Biến từng dòng dữ liệu trên mạng thành class Product
                    val product = document.toObject(Product::class.java)
                    list.add(product)
                }

                // Trả danh sách về cho ViewModel qua callback
                onResult(list)
            }
            .addOnFailureListener { exception ->
                // Nếu thất bại (mất mạng...): Trả về danh sách rỗng
                onResult(emptyList())
            }
    }
}