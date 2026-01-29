package com.example.myphone.ui.main.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast // Import Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myphone.R
import com.example.myphone.data.model.CartItem
import com.example.myphone.databinding.FragmentCartBinding
import com.example.myphone.ui.base.BaseFragment
import com.example.myphone.utils.Resource
import androidx.core.graphics.drawable.toDrawable


class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListener()
        observeData()
        viewModel.loadCart() //load data
    }

// recycle view
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter { itemToDelete -> showConfirmDeleteDialog(itemToDelete)
        }
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context)//xếp theo chiều dọc
        }
    }
    // Cài đặt các sự kiện bấm nút
    private fun setupClickListener() {
        // Sự kiện nút Áp dụng Coupon
        binding.btnApplyCoupon.setOnClickListener {
            val code = binding.etCouponCode.text.toString().trim()
            if (code.isNotEmpty()) {
                viewModel.applyCoupon(code)
            } else {
                Toast.makeText(context, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show()
            }
        }

        // Sự kiện nút Thanh toán
        binding.btnCheckout.setOnClickListener {
            val currentList = viewModel.cartItems.value
            if (currentList.isNullOrEmpty()) {
                Toast.makeText(context, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show()
            } else {
                showConfirmCheckoutDialog()
            }
        }
        // Sự kiện nút Tiếp tục mua sắm
        binding.btnGoShopping.setOnClickListener {
            parentFragmentManager.popBackStack()//quay lại fragment trước
        }
    }

    // Lắng nghe dữ liệu từ ViewModel
    @SuppressLint("SetTextI18n")
    private fun observeData() {
        // A. List sản phẩm
        viewModel.cartItems.observe(viewLifecycleOwner) { list ->
            cartAdapter.setData(list)
            val isEmpty = list.isEmpty()
            binding.layoutEmpty.isVisible = isEmpty
            binding.layoutContent.isVisible = !isEmpty
        }

        // B. Tạm tính
        viewModel.tempPrice.observe(viewLifecycleOwner) { temp ->
            binding.tvTempPrice.text = formatMoney(temp)
        }

        // C. Giảm giá
        viewModel.discountAmount.observe(viewLifecycleOwner) { discount ->
            if (discount > 0) {
                binding.layoutDiscount.isVisible = true
                binding.tvDiscountAmount.text = formatMoney(discount)
            } else {
                binding.layoutDiscount.isVisible = false
            }
        }

        // D. Tổng thanh toán cuối cùng
        viewModel.finalPrice.observe(viewLifecycleOwner) { final ->
            binding.tvTotalPrice.text = formatMoney(final)
        }

        // E. Thông báo kết quả nhập mã
        viewModel.couponMessage.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnApplyCoupon.text = "..."
                    binding.btnApplyCoupon.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnApplyCoupon.text = "Đã dùng"
                    binding.btnApplyCoupon.isEnabled = false
                    Toast.makeText(context, resource.data, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.btnApplyCoupon.text = "Áp dụng"
                    binding.btnApplyCoupon.isEnabled = true
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//DIALOG

    @SuppressLint("SetTextI18n")
    private fun showConfirmDeleteDialog(item: CartItem) {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)

        tvMessage.text = "Bạn có chắc muốn xóa '${item.product.name}' khỏi giỏ hàng?"
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnDelete.setOnClickListener {
            viewModel.removeFromCart(item)
            showSuccessDeleteDialog()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSuccessDeleteDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_delete, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.btnOKSuccessDelete).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showConfirmCheckoutDialog() {
        if (context == null) return
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_checkout, null)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            viewModel.checkout()
            dialog.dismiss()
            showSuccessCheckOutDialog()
        }
        dialog.show()
    }

    private fun showSuccessCheckOutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success_checkout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        dialogView.findViewById<Button>(R.id.btnOKSuccessCheckOut).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}