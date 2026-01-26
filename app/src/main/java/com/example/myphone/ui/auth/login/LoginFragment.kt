package com.example.myphone.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentLoginBinding
import com.example.myphone.ui.main.MainActivity

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        setupClickListeners()
        observeViewModel()
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

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnLogin.isEnabled = false
                }

                is LoginResult.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT)
                        .show()
                    //dang nhap thanh cong chuyen sang main activity
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                is LoginResult.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Đăng nhập thất bại", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
