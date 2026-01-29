package com.example.myphone.ui.main.profile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button // Import Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentProfileBinding
import com.example.myphone.ui.auth.AuthActivity
import com.example.myphone.ui.base.BaseFragment // 1. Kế thừa BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
        observeData()
    }

    // 2. Tách hàm xử lý sự kiện
    private fun setupEvents() {
        // Mở lịch sử đơn hàng
        binding.tvOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }
        // Mở dialog sửa thông tin
        binding.tvEditProfile.setOnClickListener {
            showEditDialog()
        }

        // Đăng xuất
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            // Chuyển màn hình và xóa stack để không back lại được
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // 3. Tách hàm lắng nghe dữ liệu
    private fun observeData() {
        // A. Hiển thị thông tin User
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.fullName
                binding.tvUserEmail.text = user.email
                // binding.tvPhone.text = user.phoneNumber // Nếu có view hiển thị sđt
            }
        }

        // B. Lắng nghe trạng thái cập nhật
        viewModel.updateStatus.observe(viewLifecycleOwner) { isSuccess ->
            when (isSuccess) {
                true -> {
                    showSuccessEditProfile()
                    viewModel.resetUpdateStatus()
                }
                false -> {
                    Toast.makeText(requireContext(), "Lỗi cập nhật vui lòng thử lại!", Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateStatus()
                }
                else -> { /* Null - Không làm gì */ }
            }
        }
    }

    // 4. Hàm Dialog giữ nguyên logic nhưng sửa lại context cho an toàn
    private fun showEditDialog() {
        // Kiểm tra context null an toàn
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null)
        // Ánh xạ View
        val edtName = dialogView.findViewById<EditText>(R.id.edtFullName)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneNumber)
        val edtAddress = dialogView.findViewById<EditText>(R.id.edtAddress)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave) // Dùng Button cho chuẩn
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // Đổ dữ liệu `cũ
        val currentUser = viewModel.user.value
        if (currentUser != null) {
            edtName.setText(currentUser.fullName)
            edtPhone.setText(currentUser.phoneNumber)
            edtAddress.setText(currentUser.address)
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val dialog = builder.create()
        // Làm trong suốt background dialog
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Xử lý Lưu
        btnSave.setOnClickListener {
            val newName = edtName.text.toString().trim()
            val newPhone = edtPhone.text.toString().trim()
            val newAddress = edtAddress.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Tên không được để trống!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateUser(newName, newPhone, newAddress)
                dialog.dismiss()
            }
        }
        // Xử lý Hủy
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //dialog
    private fun showSuccessEditProfile() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_editprofile, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.btnOKSuccessEditProfile).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}