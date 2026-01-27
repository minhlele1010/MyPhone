package com.example.myphone.ui.main.home

import android.app.AlertDialog
import android.graphics.Color

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myphone.R
import com.example.myphone.databinding.FragmentHomeBinding
import com.example.myphone.ui.main.cart.CartViewModel
import com.example.myphone.ui.base.BaseFragment
import android.widget.PopupWindow
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()
    // ViewModel 2: Quản lý giỏ hàng
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupFilter()
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


    //lọc sản phẩm
    private fun setupFilter() {
        binding.btnFilter.setOnClickListener { view ->
            showSortMenu(view)
        }
    }


    private fun showSortMenu(anchorView: View) {

        val popupView = layoutInflater.inflate(R.layout.layout_popup_sort,null)
        // 2. Tạo PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT, // Chiều rộng
            ViewGroup.LayoutParams.WRAP_CONTENT, // Chiều cao
            true // Focusable (Bấm ra ngoài tự tắt)
        )

        // Set background trong suốt để thấy bo góc của CardView
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.elevation = 10f

        // 3. Bắt sự kiện click cho từng món
        popupView.findViewById<TextView>(R.id.tvPriceAsc).setOnClickListener {
            homeViewModel.sortProducts(SortType.PRICE_ASC)
            popupWindow.dismiss() // Tắt menu sau khi chọn
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
    // --- 3. QUAN SÁT DỮ LIỆU  ---
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


//dialog
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