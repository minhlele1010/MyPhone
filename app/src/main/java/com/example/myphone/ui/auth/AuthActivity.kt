package com.example.myphone.ui.auth

import android.content.Intent // <--- QUAN TRỌNG: Để dùng được lệnh chuyển màn hình
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myphone.R // <--- QUAN TRỌNG: Để máy hiểu R.layout... là file giao diện của bạn
import com.example.myphone.data.repository.AuthRepository
import com.example.myphone.ui.main.MainActivity

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // KIỂM TRA ĐĂNG NHẬP
        // Nếu đã có User rồi thì chuyển thẳng sang MainActivity luôn
        if (AuthRepository.getCurrentUser() != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Đóng Activity này lại để user không bấm Back quay về được
            return
        }
        setContentView(R.layout.activity_auth)
    }
}