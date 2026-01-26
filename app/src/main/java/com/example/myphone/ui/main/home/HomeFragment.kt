package com.example.myphone.ui.main.home

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myphone.R
import com.example.myphone.databinding.FragmentHomeBinding
import com.example.myphone.ui.main.cart.CartViewModel // Import ViewModel của Cart

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // ViewModel 1: Quản lý hiển thị danh sách sản phẩm (Search, Filter...)
    private val homeViewModel: HomeViewModel by viewModels()
    // ViewModel 2: Quản lý thêm vào giỏ hàng (MỚI THÊM)
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            // Hành động 1: Xem chi tiết
            onDetailClick = { product ->
                val bundle = Bundle().apply {
                    putSerializable("product_data", product)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_productDetailFragment,
                    bundle
                )
            },
            // Hành động 2: Thêm vào giỏ (ĐÃ SỬA THEO MVVM)
            onAddToCartClick = { product ->
                // Gọi ViewModel xử lý, không gọi Repository trực tiếp nữa
                cartViewModel.addToCart(product)
            }
        )

        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun observeData() {
        // 1. Lắng nghe danh sách sản phẩm để hiển thị
        homeViewModel.products.observe(viewLifecycleOwner) { list ->
            productAdapter.setData(list)
        }

        // 2. Lắng nghe kết quả thêm giỏ hàng (MỚI THÊM)
        cartViewModel.addToCartSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                // Hiện Dialog đẹp
                showSuccessDialog()

                // Reset trạng thái để chờ lần bấm tiếp theo
                cartViewModel.resetAddStatus()
            }
        }
    }

    // --- HÀM HIỂN THỊ DIALOG THÀNH CÔNG (Giữ nguyên) ---
    private fun showSuccessDialog() {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart_success, null)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}