package com.example.myphone.ui.main.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myphone.data.model.CartItem
import com.example.myphone.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartList = listOf<CartItem>()

    fun setData(list: List<CartItem>) {
        this.cartList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.tvCartName.text = item.product.name
            binding.tvCartQuantity.text = "Số lượng: ${item.quantity}"
            // Tính tiền = Giá x Số lượng
            val totalItemPrice = item.product.price * item.quantity
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.tvCartPrice.text = formatter.format(totalItemPrice)
            // Ảnh Offline
            binding.ivCartImage.setImageResource(com.example.myphone.R.drawable.ip17prm)
            // Nút xóa
            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}