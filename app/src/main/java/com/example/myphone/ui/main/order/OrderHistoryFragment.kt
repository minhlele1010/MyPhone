package com.example.myphone.ui.main.order

import android.os.Bundle
import android.view.View


import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myphone.databinding.FragmentOrderHistoryBinding
import com.example.myphone.ui.base.BaseFragment
class OrderHistoryFragment : BaseFragment<FragmentOrderHistoryBinding>(FragmentOrderHistoryBinding::inflate) {
    // 1. Khai báo ViewModel để lấy dữ liệu từ Firebase
    private val viewModel: OrderViewModel by viewModels()
    // 2. Khai báo Adapter để hiển thị danh sách
    private val orderAdapter = OrderAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
        setupRecyclerView()
        // 3. Gọi ViewModel đi lấy dữ liệu lịch sử
        viewModel.loadHistory()
        // 4. Lắng nghe dữ liệu trả về
        observeData()
        // Xử lý nút Back
    }
    private fun setupRecyclerView(){
        binding.rvOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true) // Tối ưu hiệu năng
        }
    }
    private fun observeData() {
        viewModel.orders.observe(viewLifecycleOwner) { list ->
            // Đổ dữ liệu vào Adapter
            orderAdapter.setData(list)
        }
    }
}