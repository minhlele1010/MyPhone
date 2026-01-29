package com.example.myphone.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myphone.data.model.Product
import com.example.myphone.data.repository.ProductRepository


class HomeViewModel : ViewModel() {

    // LiveData để UI lắng nghe và hiển thị
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    // Biến lưu trữ danh sách gốc (Backup)
    private var originalList = listOf<Product>()

    init {
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        ProductRepository.getAllProducts { list ->
            // 1. Lưu lại bản gốc
            originalList = list
            // 2. Cập nhật lên UI
            _products.value = list
        }
    }

//tim kiem
    fun searchProduct(query: String) {
        // Nếu ô tìm kiếm trống -> Trả lại danh sách gốc đầy đủ
        if (query.isBlank()) {
            _products.value = originalList
            return
        }

        // Nếu có chữ -> Lọc danh sách gốc
        val filteredList = originalList.filter { product ->
            // Cách 1: Tìm đơn giản (Có chứa chuỗi là được, không phân biệt hoa thường)
            product.name.contains(query, ignoreCase = true)
        }

        // Cập nhật danh sách đã lọc lên UI
        _products.value = filteredList
    }

    //loc san pham
    fun sortProducts(type: SortType) {
        val currentList = _products.value ?: return // Lấy danh sách hiện tại

        // Tạo danh sách mới đã sắp xếp (Sorted List)
        val sortedList = when (type) {
            SortType.PRICE_ASC -> currentList.sortedBy { it.price }
            SortType.PRICE_DESC -> currentList.sortedByDescending { it.price }
            SortType.NAME_AZ -> currentList.sortedBy { it.name }
        }

        // Cập nhật lại LiveData để UI tự đổi
        _products.value = sortedList
    }
}
enum class SortType {
    PRICE_ASC,  // Giá thấp -> cao
    PRICE_DESC, // Giá cao -> thấp
    NAME_AZ     // Tên A -> Z
}