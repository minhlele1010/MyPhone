package com.example.myphone.data.repository

import com.example.myphone.data.model.CartItem
import com.example.myphone.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myphone.data.model.Order

object CartRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 1. Thêm vào giỏ
    fun addToCart(product: Product, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onComplete(false)
            return
        }
        val cartRef = db.collection("users").document(uid)
            .collection("cart").document(product.id)
        cartRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentQuantity = document.getLong("quantity")?.toInt() ?: 0
                cartRef.update("quantity", currentQuantity + 1)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            } else {
                val newItem = CartItem(product, 1)
                cartRef.set(newItem)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
        }.addOnFailureListener { onComplete(false) }
    }
    // 2. Lấy danh sách (Giữ nguyên nhưng nhớ logic callback)
    fun getCartItems(onResult: (List<CartItem>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(emptyList())
            return
        }
        db.collection("users").document(uid).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(CartItem::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // 3. Xóa 1 sản phẩm
    fun removeFromCart(item: CartItem, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        // Xóa document dựa theo Product ID
        db.collection("users").document(uid)
            .collection("cart").document(item.product.id)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 4. Xóa sạch giỏ hàng - Thanh toán
    fun clearCart(onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val collectionRef = db.collection("users").document(uid).collection("cart")
        // Firestore không có hàm "xóa cả collection" nên phải lấy hết ra rồi xóa từng cái
        collectionRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch() // Dùng Batch để xóa 1 thể cho nhanh
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    fun checkoutOrder(
        currentItems: List<CartItem>,
        totalPrice: Double,
        onComplete: (Boolean) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        // A. Tạo tham chiếu cho đơn hàng mới
        val ordersRef = db.collection("users").document(uid).collection("orders").document()
        // B. Tạo đối tượng Order
        val newOrder = Order(
            id = ordersRef.id,
            items = currentItems,
            totalPrice = totalPrice,
            dateOrder = System.currentTimeMillis(),
        )
        // C. Dùng BATCH để thực hiện cùng lúc: Ghi Order + Xóa Cart
        db.runBatch { batch ->
            // 1. Ghi đơn hàng vào bảng 'orders'
            batch.set(ordersRef, newOrder)
            // 2. Xóa từng món trong bảng 'cart'
            val cartCollection = db.collection("users").document(uid).collection("cart")
            for (item in currentItems) {
                // Xóa document dựa trên ID sản phẩm
                val itemRef = cartCollection.document(item.product.id)
                batch.delete(itemRef)
            }
        }.addOnSuccessListener {
            onComplete(true) // Thành công rực rỡ
        }.addOnFailureListener {
            onComplete(false) // Lỗi
        }
    }

    // 5. Lấy danh sách lịch sử đơn hàng (Dùng để hiển thị màn hình Lịch sử sau này)
    fun getOrderHistory(onResult: (List<Order>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(emptyList())
            return
        }
        db.collection("users").document(uid).collection("orders")
            .orderBy(
                "dateOrder",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            ) // Sắp xếp mới nhất lên đầu
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Order::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}