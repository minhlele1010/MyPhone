package com.example.myphone.ui.main.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myphone.R
import com.example.myphone.databinding.FragmentHomeBinding
import com.example.myphone.ui.base.BaseFragment
import com.example.myphone.ui.main.cart.CartViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // ViewModel 1: Quản lý danh sách sản phẩm, tìm kiếm, lọc
    private val homeViewModel: HomeViewModel by viewModels()

    // ViewModel 2: Quản lý thêm vào giỏ hàng
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupEvents() // <--- Đã gom Search và Filter vào đây
        observeData()
    }

    // 1. Cài đặt RecyclerView
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

    // Các sự kiện
    private fun setupEvents() {
        //  Lọc sản phâ
        binding.btnFilter.setOnClickListener { view ->
            showSortMenu(view)
        }
        // tìm kiếm
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                homeViewModel.searchProduct(query)
            }
        })
    }

    // Popup Menu Lọc
    private fun showSortMenu(anchorView: View) {
        if (context == null) return

        val popupView = layoutInflater.inflate(R.layout.layout_popup_sort, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set background trong suốt và đổ bóng
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.elevation = 10f

        // Xử lý click từng dòng trong menu
        popupView.findViewById<TextView>(R.id.tvPriceAsc).setOnClickListener {
            homeViewModel.sortProducts(SortType.PRICE_ASC)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.tvPriceDesc).setOnClickListener {
            homeViewModel.sortProducts(SortType.PRICE_DESC)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.tvNameAz).setOnClickListener {
            homeViewModel.sortProducts(SortType.NAME_AZ)
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    // 4. Lắng nghe dữ liệu từ ViewModel
    private fun observeData() {
        // A. List sản phẩm (Đã search/filter)
        homeViewModel.products.observe(viewLifecycleOwner) { list ->
            productAdapter.setData(list)
        }

        // B. Kết quả thêm vào giỏ hàng
        cartViewModel.addToCartSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                showSuccessDialog()
                cartViewModel.resetAddStatus()
            }
        }
    }

    // 5. Dialog thông báo thêm giỏ hàng thành công
    private fun showSuccessDialog() {
        if (context == null) return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart_success, null)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnOk?.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}