package com.example.myphone.ui.auth.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                viewModel.register(fullName, email, password)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResult.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnRegister.isEnabled = false
                }
                is RegisterResult.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is RegisterResult.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
