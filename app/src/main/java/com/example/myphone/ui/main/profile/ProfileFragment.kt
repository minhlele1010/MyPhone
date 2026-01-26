package com.example.myphone.ui.main.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentProfileBinding
import com.example.myphone.ui.auth.AuthActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // 1. Hiển thị thông tin User (Tự động cập nhật khi sửa xong)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.fullName
                binding.tvUserEmail.text = user.email
                // Nếu bạn có TextView hiển thị SĐT hay Địa chỉ thì gán thêm ở đây
            }
        }

        // 2. Lắng nghe kết quả Cập nhật (Để báo cho người dùng biết)
        viewModel.updateStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus() // Reset để không hiện lại khi xoay màn hình
            } else if (isSuccess == false) {
                Toast.makeText(context, "Lỗi cập nhật, vui lòng thử lại!", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateStatus()
            }
        }

        // 3. Xử lý click các menu
        binding.tvOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }

        binding.tvEditProfile.setOnClickListener {
            // Thay Toast bằng hàm mở Dialog
            showEditDialog()
        }

        // 4. XỬ LÝ ĐĂNG XUẤT
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

    // Hàm hiển thị Hộp thoại nhập thông tin
    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null)

        val edtName = dialogView.findViewById<EditText>(R.id.edtFullName)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhoneNumber)
        val edtAddress = dialogView.findViewById<EditText>(R.id.edtAddress)

        // Ánh xạ thêm 2 nút bấm mới
        val btnSave = dialogView.findViewById<View>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancel)

        // Đổ dữ liệu cũ
        val currentUser = viewModel.user.value
        if (currentUser != null) {
            edtName.setText(currentUser.fullName)
            edtPhone.setText(currentUser.phoneNumber)
            edtAddress.setText(currentUser.address)
        }

        // Tạo Dialog
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView) // Chỉ set View, không set nút mặc định nữa

        val dialog = builder.create()

        // --- QUAN TRỌNG: Làm trong suốt nền mặc định để thấy được bo góc ---
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        // ------------------------------------------------------------------

        // Xử lý nút LƯU
        btnSave.setOnClickListener {
            val newName = edtName.text.toString().trim()
            val newPhone = edtPhone.text.toString().trim()
            val newAddress = edtAddress.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(context, "Tên không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateUser(newName, newPhone, newAddress)
            dialog.dismiss()
        }

        // Xử lý nút HỦY
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}