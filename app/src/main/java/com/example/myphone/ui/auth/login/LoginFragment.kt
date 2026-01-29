package com.example.myphone.ui.auth.login

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentLoginBinding
import com.example.myphone.ui.base.BaseFragment
import com.example.myphone.ui.main.MainActivity
import com.example.myphone.utils.Resource
import androidx.core.graphics.drawable.toDrawable

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel : LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true//hien vong xoay
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    showSuccessLoginDialog()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    showFailLoginDialog()
                }
            }
        }
    }

//gán sự kiện các nút
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()//trim loại bỏ khoảng trắng đầu cuôi
            val password = binding.etPassword.text.toString().trim()
            //check email pass không được để trống
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập email và mật khẩu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //chuyen sang dki
        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    ///Dialog
    private fun showFailLoginDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fail_login, null)
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(dialogView)
        builder.setCancelable(false) //không cho bấm ra ngoài để tắt

        val dialog = builder.create()

        // 3. Làm trong suốt background mặc định của Dialog để thấy bo góc của CardView
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        // 4. Xử lý sự kiện nút OK
        val btnOk = dialogView.findViewById<Button>(R.id.btnOKFailLogin)
        btnOk.setOnClickListener {
            dialog.dismiss() // Đóng dialog
        }
        dialog.show()
    }
    private fun showSuccessLoginDialog() {
        // 1. Inflate layout từ file XML bạn đã tạo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_login, null)
        // 2. Tạo Dialog Builder
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false) // Không cho bấm ra ngoài để tắt

        val dialog = builder.create()

        // 3. Làm trong suốt background mặc định của Dialog để thấy bo góc của CardView
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        // 4. Xử lý sự kiện nút OK
        val btnOk = dialogView.findViewById<Button>(R.id.btnOKSuccessLogin)
        btnOk.setOnClickListener {
            dialog.dismiss() // Đóng dialog
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        dialog.show()
    }
}
