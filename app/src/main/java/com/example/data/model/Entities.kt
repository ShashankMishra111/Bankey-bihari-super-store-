package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String = "CUSTOMER", // "CUSTOMER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val description: String = "",
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val brand: String,
    val category: String,
    val subcategory: String = "",
    val description: String = "",
    val weight: String, // e.g. "1 kg", "500 g", "5 L"
    val unit: String = "pkt", // "kg", "g", "L", "ml", "pkt", "piece"
    val sellingPrice: Double,
    val mrp: Double,
    val discountPercentage: Int = 0,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 10,
    val mainImage: String = "",
    val additionalImages: String = "", // Comma separated
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
    val cartItemId: Long,
    val userId: Long,
    val quantity: Int,
    val product: ProductEntity
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val phone: String,
    val houseFlat: String,
    val street: String,
    val area: String = "Jai Vihar",
    val landmark: String = "",
    val city: String = "Najafgarh, New Delhi",
    val state: String = "Delhi",
    val pinCode: String = "110043",
    val addressType: String = "Home",
    val isDefault: Boolean = false
)

@Entity(tableName = "delivery_slots")
data class DeliverySlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slotName: String, // "Morning Slot", "Evening Slot", "Express Slot"
    val timeWindow: String, // "7:00 AM - 12:00 PM", "5:00 PM - 9:00 PM"
    val fee: Double = 0.0,
    val isActive: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String, // e.g. "BBS-2026-1001"
    val userId: Long,
    val customerName: String,
    val customerPhone: String,
    val subtotal: Double,
    val discountAmount: Double,
    val deliveryFee: Double,
    val totalSavings: Double,
    val grandTotal: Double,
    val paymentMethod: String, // "Cash on Delivery", "UPI", "Card"
    val paymentStatus: String = "Pending", // "Paid", "Pending (Cash on Delivery)"
    val deliverySlot: String,
    val deliveryAddress: String,
    val orderStatus: String, // "Pending", "Confirmed", "Preparing", "Out for Delivery", "Delivered", "Cancelled"
    val totalItems: Int,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val productId: Long,
    val productName: String,
    val brand: String,
    val weight: String,
    val price: Double,
    val mrp: Double,
    val quantity: Int,
    val itemTotal: Double
)

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val storeName: String = "Bankey Bihari Super Store",
    val storeAddress: String = "Jai Vihar, Najafgarh, New Delhi, Delhi 110043, India",
    val storePhone: String = "+91 98765 43210",
    val storeEmail: String = "contact@bankeybihari.com",
    val deliveryFee: Double = 30.0,
    val freeDeliveryThreshold: Double = 499.0,
    val minOrderValue: Double = 150.0,
    val isStoreOpen: Boolean = true,
    val closedMessage: String = "Store is currently closed for maintenance. Ordering will resume shortly.",
    val estimatedDeliveryTime: String = "30-45 mins",
    val currencySymbol: String = "₹"
)

@Entity(tableName = "csv_import_logs")
data class CsvImportLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filename: String,
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val totalRows: Int,
    val validRows: Int,
    val invalidRows: Int,
    val status: String, // "SUCCESS", "PARTIAL", "FAILED"
    val summary: String
)

data class CustomerWithStats(
    val user: UserEntity,
    val totalOrders: Int,
    val totalSpent: Double,
    val lastOrderDate: Long?
)

data class SalesAnalytics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val avgOrderValue: Double,
    val totalUnitsSold: Int,
    val pendingOrdersCount: Int,
    val lowStockCount: Int,
    val topProducts: List<TopProductStat>,
    val categorySales: List<CategorySalesStat>
)

data class TopProductStat(
    val productName: String,
    val unitsSold: Int,
    val revenue: Double
)

data class CategorySalesStat(
    val categoryName: String,
    val unitsSold: Int,
    val revenue: Double
)

data class CsvRowValidationResult(
    val rowNumber: Int,
    val sku: String,
    val name: String,
    val category: String,
    val price: Double?,
    val mrp: Double?,
    val stock: Int?,
    val isValid: Boolean,
    val errors: List<String>,
    val parsedProduct: ProductEntity?
)
