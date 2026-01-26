package com.example.myphone.data.repository

import com.example.myphone.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance() // Thêm dòng này

    fun getCurrentUser() = firebaseAuth.currentUser

    // 1. ĐĂNG KÝ (Sửa: Thêm tham số fullName và logic lưu Firestore)
    fun register(
        email: String,
        pass: String,
        fullName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        // A. Tạo tài khoản Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    if (uid != null) {
                        // B. TẠO DỮ LIỆU USER BÊN FIRESTORE
                        val newUser = User(
                            id = uid,
                            email = email,
                            fullName = fullName, // Lưu tên người dùng nhập
                            phoneNumber = "",
                            address = ""
                        )

                        db.collection("users").document(uid)
                            .set(newUser) // Lưu xuống database
                            .addOnSuccessListener {
                                onResult(true, null) // Thành công cả 2 bước
                            }
                            .addOnFailureListener { e ->
                                onResult(true, "Đăng ký được nhưng lỗi lưu tên: ${e.message}")
                            }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // 2. ĐĂNG NHẬP (Giữ nguyên)
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // 3. ĐĂNG XUẤT (Giữ nguyên)
    fun logout() {
        firebaseAuth.signOut()
    }
}