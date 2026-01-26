package com.example.myphone.ui.main.order

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myphone.R
import com.example.myphone.databinding.FragmentOrderHistoryBinding
import com.example.myphone.ui.main.order.OrderAdapter   // Import Adapter (Nếu khác gói)
import com.example.myphone.ui.main.order.OrderViewModel // Import ViewModel (Nếu khác gói)

class OrderHistoryFragment : Fragment(R.layout.fragment_order_history) {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    // 1. Khai báo ViewModel để lấy dữ liệu từ Firebase
    private val viewModel: OrderViewModel by viewModels()

    // 2. Khai báo Adapter để hiển thị danh sách
    private val orderAdapter = OrderAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderHistoryBinding.bind(view)

        // Setup RecyclerView
        setupRecyclerView()

        // 3. Gọi ViewModel đi lấy dữ liệu lịch sử
        viewModel.loadHistory()

        // 4. Lắng nghe dữ liệu trả về
        observeData()

        // Xử lý nút Back
        binding.btnBack.setOnClickListener {
            // Cách dùng NavController (Khuyên dùng nếu bạn đang dùng Navigation Component)
            parentFragmentManager.popBackStack()

            // Hoặc cách cũ của bạn (cũng ok):
            // requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
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

            // (Tùy chọn) Logic hiển thị màn hình trống nếu chưa mua gì
            /*
            if (list.isEmpty()) {
                binding.rvOrders.isVisible = false
                // binding.tvEmptyMessage.isVisible = true // Nếu bạn có TextView báo trống
            } else {
                binding.rvOrders.isVisible = true
            }
            */
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}