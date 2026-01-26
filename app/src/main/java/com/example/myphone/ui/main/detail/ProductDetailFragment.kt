package com.example.myphone.ui.main.detail

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Import cái này
import androidx.navigation.fragment.findNavController
import com.example.myphone.R
import com.example.myphone.data.model.Product
import com.example.myphone.databinding.FragmentProductDetailBinding
import com.example.myphone.ui.main.cart.CartViewModel // Import ViewModel Cart
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    // 1. Khai báo ViewModel để xử lý thêm giỏ hàng
    private val cartViewModel: CartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)

        val product = arguments?.getSerializable("product_data") as? Product

        if (product != null) {
            setupViews(product)
        } else {
            Toast.makeText(context, "Lỗi không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 2. Lắng nghe kết quả thêm giỏ hàng
        observeData()
    }

    private fun setupViews(product: Product) {
        binding.tvProductName.text = product.name
        binding.tvProductDesc.text = product.description

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvProductPrice.text = formatter.format(product.price)

        // Tạm thời set cứng ảnh (sau này mở Glide ra)
        binding.ivProductImage.setImageResource(R.drawable.ip15prm)

        // 3. Xử lý nút Thêm vào giỏ (SỬA LẠI THEO MVVM)
        binding.btnAddToCart.setOnClickListener {
            // Gọi ViewModel xử lý, không gọi Repository trực tiếp
            cartViewModel.addToCart(product)
        }
    }

    // 4. Hàm lắng nghe dữ liệu từ ViewModel
    private fun observeData() {
        cartViewModel.addToCartSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                // Hiện Dialog đẹp
                showSuccessDialog()

                // Reset trạng thái để lần sau bấm tiếp được
                cartViewModel.resetAddStatus()
            }
        }
    }

    // 5. Copy hàm hiển thị Dialog từ Home sang đây
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