package com.example.myphone.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.text.NumberFormat
import java.util.Locale

// Class cha nhận vào 1 cái khuôn đúc Binding (Inflate)
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    // 1. Quản lý Binding (Tự động)
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding
            ?: throw IllegalStateException("Binding chỉ được gọi khi View đang tồn tại")

    // 2. Tự động tạo View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    // 3. Tự động hủy View (Tránh rò rỉ bộ nhớ)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //ham format tien
    protected fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return formatter.format(amount)
    }
}