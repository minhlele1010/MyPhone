package com.example.myphone.data.repository

import com.example.myphone.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Hàm lấy thông tin (Giữ nguyên)
    fun getCurrentUser(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(null)
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Hàm cập nhật (SỬA LẠI CHO AN TOÀN)
    fun updateUserInfo(name: String, phone: String, address: String, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "fullName" to name,
            "phoneNumber" to phone,
            "address" to address
        )

        db.collection("users").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

}