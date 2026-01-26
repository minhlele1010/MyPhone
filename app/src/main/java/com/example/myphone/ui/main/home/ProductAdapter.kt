package com.example.myphone.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myphone.data.model.Product
import com.example.myphone.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale
import com.bumptech.glide.Glide

class ProductAdapter(
    private val onDetailClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    private var productList = listOf<Product>()

    fun setData(list: List<Product>) {
        this.productList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            // Format tiền Việt Nam
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            binding.tvProductPrice.text = formatter.format(product.price)
            Glide.with(binding.root.context)
                .load(product.imageUrl) // Lấy link ảnh từ Model
                .into(binding.ivProductImage) // Đổ vào ImageView
            // Sự kiện click
            binding.ivProductImage.setOnClickListener { onDetailClick(product) }
            binding.btnInfo.setOnClickListener { onDetailClick(product) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(product) }
        }
    }
}