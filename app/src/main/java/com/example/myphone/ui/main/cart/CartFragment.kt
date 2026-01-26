package com.example.myphone.ui.main.cart

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView // Import thêm TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myphone.R
import com.example.myphone.data.model.CartItem // Import CartItem
import com.example.myphone.databinding.FragmentCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment(R.layout.fragment_cart) {

    // ... (Các biến giữ nguyên)
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)
        setupRecyclerView()
        observeData()
        viewModel.loadCart()

        // Sự kiện nút Thanh toán (Giữ nguyên)
        binding.btnCheckout.setOnClickListener {
            val currentList = viewModel.cartItems.value
            if (currentList.isNullOrEmpty()) {
                Toast.makeText(context, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show()
            } else {
                showConfirmCheckoutDialog()
            }
        }

        binding.btnGoShopping.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        // --- SỬA ĐOẠN NÀY ---
        cartAdapter = CartAdapter { itemToDelete ->
            // Thay vì xóa luôn, ta hiện Dialog hỏi trước
            showConfirmDeleteDialog(itemToDelete)
        }

        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    //dialog xóa
    private fun showConfirmDeleteDialog(item: CartItem) {
        if (context == null) return

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)

        // Cập nhật nội dung cho cụ thể (Ví dụ: Bạn có muốn xóa iPhone 15...?)
        tvMessage.text = "Bạn có chắc muốn xóa '${item.product.name}' khỏi giỏ hàng?"

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // Gọi ViewModel để xóa thật
            viewModel.removeFromCart(item)

            Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    //dialog thanh toán
    private fun showConfirmCheckoutDialog() {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_checkout, null)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            viewModel.checkout()
            dialog.dismiss()
            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    // ... (Các hàm observeData giữ nguyên)
    private fun observeData() {
        viewModel.cartItems.observe(viewLifecycleOwner) { list ->
            cartAdapter.setData(list)
            val isEmpty = list.isEmpty()
            binding.layoutEmpty.isVisible = isEmpty
            binding.layoutContent.isVisible = !isEmpty
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.tvTotalPrice.text = formatter.format(total)
        }
    }
}