package com.example.myphone.ui.main.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.CartItem
import com.example.myphone.data.model.Product
import com.example.myphone.data.model.Coupon
import com.example.myphone.data.repository.CartRepository
import com.example.myphone.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    //tien tam tinh
    private val _tempPrice = MutableLiveData(0.0)
    val tempPrice: LiveData<Double> = _tempPrice

    // 2. Số tiền được giảm
    private val _discountAmount = MutableLiveData(0.0)
    val discountAmount: LiveData<Double> = _discountAmount

    // 3. Tổng tiền cuối cùng (Sau khi trừ giảm giá)
    private val _finalPrice = MutableLiveData(0.0)
    val finalPrice: LiveData<Double> = _finalPrice

    // 4. Thông báo kết quả check mã
    private val _couponMessage = MutableLiveData<Resource<String>>()
    val couponMessage: LiveData<Resource<String>> = _couponMessage

    private val db = FirebaseFirestore.getInstance()

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

    // LOGIC MÃ GIẢM GIÁ (MỚI)
    fun applyCoupon(code: String) {
        _couponMessage.value = Resource.Loading
        db.collection("coupons")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    _couponMessage.value = Resource.Error("Mã giảm giá không tồn tại!")
                    // Nếu mã sai thì reset giảm giá về 0
                    _discountAmount.value = 0.0
                    calculateTotalPrice(_cartItems.value ?: emptyList())
                } else {
                    val coupon = documents.documents[0].toObject(Coupon::class.java)
                    if (coupon != null) {
                        _discountAmount.value = coupon.value
                        _couponMessage.value = Resource.Success("Áp dụng mã thành công: -${coupon.value}")
                        calculateTotalPrice(_cartItems.value ?: emptyList())
                    }
                }
            }
            .addOnFailureListener {
                _couponMessage.value = Resource.Error("Lỗi hệ thống: ${it.message}")
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
        val items = _cartItems.value
        val totalFinal = _finalPrice.value // Lấy giá ĐÃ GIẢM để thanh toán

        if (items.isNullOrEmpty() || totalFinal == null) return

        CartRepository.checkoutOrder(items, totalFinal) { isSuccess ->
            if (isSuccess) {
                _discountAmount.value = 0.0 // Reset giảm giá sau khi mua xong
                loadCart()
            }
        }
    }

    fun resetAddStatus() {
        _addToCartSuccess.value = false
    }

    // Hàm tính tổng tiền nội bộ
    private fun calculateTotalPrice(list: List<CartItem>) {
        var tempTotal = 0.0
        for (item in list) {
            tempTotal += item.product.price * item.quantity
        }

        // 1. Cập nhật tạm tính
        _tempPrice.value = tempTotal

        // 2. Tính tiền cuối cùng = Tạm tính - Giảm giá
        val discount = _discountAmount.value ?: 0.0
        var final = tempTotal - discount
        if (final < 0) final = 0.0 // Không cho âm tiền

        // 3. Cập nhật giá chốt
        _finalPrice.value = final
    }
}