package com.example.myphone.ui.main.home

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher // <--- Import để bắt sự kiện gõ phím
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myphone.R
import com.example.myphone.databinding.FragmentHomeBinding
import com.example.myphone.ui.main.cart.CartViewModel
import com.example.myphone.ui.base.BaseFragment
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    // ViewModel 2: Quản lý giỏ hàng
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeData()
    }

 //tim kiem
    private fun setupSearch() {
        // A. Lắng nghe sự kiện gõ phím
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()

                // Gọi ViewModel để lọc danh sách
                homeViewModel.searchProduct(query)
            }
        })
    }


    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onDetailClick = { product ->
                val bundle = Bundle().apply { putSerializable("product_data", product) }
                findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
            },
            onAddToCartClick = { product ->
                cartViewModel.addToCart(product)
            }
        )

        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }
    // --- 3. QUAN SÁT DỮ LIỆU (Giữ nguyên) ---
    private fun observeData() {
        // Lắng nghe danh sách (Lúc này list trả về đã được lọc nếu đang tìm kiếm)
        homeViewModel.products.observe(viewLifecycleOwner) { list ->
            productAdapter.setData(list)
        }
        cartViewModel.addToCartSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                showSuccessDialog()
                cartViewModel.resetAddStatus()
            }
        }
    }

    // --- 4. HÀM TIỆN ÍCH ---

    private fun showSuccessDialog() {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart_success, null)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnOk?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}