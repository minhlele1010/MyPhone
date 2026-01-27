package com.example.myphone.ui.auth.register

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentRegisterBinding
import com.example.myphone.utils.Resource
import com.example.myphone.ui.base.BaseFragment
import com.example.myphone.ui.main.MainActivity

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnRegister?.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                viewModel.register(fullName, email, password)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
            binding.tvGoToLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnRegister.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnRegister.isEnabled = true
                    showSuccessRegisterDialog()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnRegister.isEnabled = true
                    showFailRegisterDialog()
                }
            }
        }
    }
    //DIALOG
    private fun showFailRegisterDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fail_register, null)
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(dialogView)
        builder.setCancelable(false) //không cho bấm ra ngoài để tắt

        val dialog = builder.create()

        // 3. Làm trong suốt background mặc định của Dialog để thấy bo góc của CardView
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 4. Xử lý sự kiện nút OK
        val btnOk = dialogView.findViewById<Button>(R.id.btnOKFailRegister)
        btnOk.setOnClickListener {
            dialog.dismiss() // Đóng dialog
        }
        dialog.show()
    }
    private fun showSuccessRegisterDialog() {
        // 1. Inflate layout từ file XML bạn đã tạo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_register, null)
        // 2. Tạo Dialog Builder
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false) // Không cho bấm ra ngoài để tắt

        val dialog = builder.create()

        // 3. Làm trong suốt background mặc định của Dialog để thấy bo góc của CardView
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 4. Xử lý sự kiện nút OK
        val btnOk = dialogView.findViewById<Button>(R.id.btnOKSuccessRegister)
        btnOk.setOnClickListener {
            dialog.dismiss() // Đóng dialog
        }
        dialog.show()
    }
}
