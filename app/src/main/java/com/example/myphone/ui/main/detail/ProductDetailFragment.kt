package com.example.myphone.ui.main.detail

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels // Import cái này
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myphone.R
import com.example.myphone.data.model.Product
import com.example.myphone.databinding.FragmentProductDetailBinding
import com.example.myphone.ui.main.cart.CartViewModel // Import ViewModel Cart
import com.example.myphone.ui.base.BaseFragment
import androidx.core.graphics.drawable.toDrawable

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    // 1. Khai báo ViewModel để xử lý thêm giỏ hàng
    private val cartViewModel: CartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.getSerializable("product_data") as? Product

        if (product != null) {
            setupViews(product)
        } else {
            Toast.makeText(context, "Lỗi không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        // 2. Lắng nghe kết quả thêm giỏ hàng
        observeData()
    }

    private fun setupViews(product: Product) {
        binding.tvProductName.text = product.name
        binding.tvProductDesc.text = product.description

        binding.tvProductPrice.text = formatMoney(product.price)
        Glide.with(binding.root.context)
            .load(product.imageUrl) // Lấy link ảnh từ Model
            .into(binding.ivProductImage) // Đổ vào ImageView
        binding.btnAddToCart.setOnClickListener {
            cartViewModel.addToCart(product)
        }
    }
    // 4. Hàm lắng nghe dữ liệu từ ViewModel
    private fun observeData() {
        cartViewModel.addToCartSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                showSuccessDialog()
                cartViewModel.resetAddStatus()
            }
        }
    }

    private fun showSuccessDialog() {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart_success, null)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}