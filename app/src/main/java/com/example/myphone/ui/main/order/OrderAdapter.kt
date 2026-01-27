package com.example.myphone.ui.main.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myphone.data.model.Order
import com.example.myphone.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orderList = listOf<Order>()

    fun setData(newList: List<Order>) {
        this.orderList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

     class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            // 1. Mã đơn hàng (Lấy 6 ký tự cuối viết hoa cho gọn)
            val shortId = if (order.id.length > 6) order.id.takeLast(6).uppercase() else order.id
            binding.tvOrderId.text = "Đơn: #$shortId"

            // 2. Format ngày tháng (Long -> String)
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
            binding.tvOrderDate.text = sdf.format(Date(order.dateOrder))

            // 3. Format tiền tệ
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.tvTotalPrice.text = formatter.format(order.totalPrice)

            // 4. Trạng thái
//            binding.tvOrderStatus.text = order.status

            // 5. Tạo chuỗi tóm tắt sản phẩm: "iPhone 15 (x1), Samsung S24 (x2)"
            val summary = order.items.joinToString(separator = ", ") { item ->
                "${item.product.name} (x${item.quantity})"
            }
            binding.tvOrderItems.text = summary
        }
    }
}