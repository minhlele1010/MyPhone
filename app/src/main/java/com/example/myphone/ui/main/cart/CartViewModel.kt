package com.example.myphone.ui.main.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.CartItem
import com.example.myphone.data.model.Product
import com.example.myphone.data.repository.CartRepository

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private val _addToCartSuccess = MutableLiveData<Boolean>()
    val addToCartSuccess: LiveData<Boolean> = _addToCartSuccess

    // --- CÁC HÀM XỬ LÝ ---

    // 1. Load dữ liệu (Đã sửa lỗi No value passed for parameter 'onResult')
    fun loadCart() {
        // Gọi repository và CHỜ kết quả trả về trong { ... }
        CartRepository.getCartItems { list ->
            // Khi có danh sách, cập nhật LiveData
            _cartItems.value = list

            // Tính tổng tiền ngay tại đây (Thay vì gọi repo)
            calculateTotalPrice(list)
        }
    }

    // 2. Thêm vào giỏ
    fun addToCart(product: Product) {
        CartRepository.addToCart(product) { isSuccess ->
            if (isSuccess) {
                _addToCartSuccess.value = true
                // Load lại giỏ hàng ngầm để cập nhật số lượng nếu đang ở màn Cart
                loadCart()
            }
        }
    }

    // 3. Xóa sản phẩm (Đã sửa lỗi Unresolved reference)
    fun removeFromCart(item: CartItem) {
        CartRepository.removeFromCart(item) { isSuccess ->
            if (isSuccess) {
                // Xóa xong thì load lại danh sách mới
                loadCart()
            }
        }
    }

    // 4. Thanh toán / Xóa sạch (Đã sửa lỗi Unresolved reference)
    fun checkout() {
        // Lấy dữ liệu hiện tại trong LiveData để gửi đi
        val items = _cartItems.value
        val total = _totalPrice.value

        if (items.isNullOrEmpty() || total == null) return

        // Gọi hàm checkoutOrder thay vì clearCart
        CartRepository.checkoutOrder(items, total) { isSuccess ->
            if (isSuccess) {
                // Sau khi chuyển sang lịch sử thành công -> Load lại giỏ hàng (sẽ rỗng)
                loadCart()
            }
        }
    }

    fun resetAddStatus() {
        _addToCartSuccess.value = false
    }

    // Hàm tính tổng tiền nội bộ (Không cần gọi Repo)
    private fun calculateTotalPrice(list: List<CartItem>) {
        var total = 0.0
        for (item in list) {
            total += item.product.price * item.quantity
        }
        _totalPrice.value = total
    }
}