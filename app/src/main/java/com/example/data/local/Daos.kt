package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phone = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailOrPhone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'ADMIN' LIMIT 1")
    suspend fun getAdminUser(): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'ADMIN' LIMIT 1")
    fun getAdminUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY createdAt DESC")
    fun getAllCustomersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY createdAt DESC")
    suspend fun getAllCustomers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<CategoryEntity>)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND isFeatured = 1 ORDER BY id DESC")
    fun getFeaturedProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND isBestseller = 1 ORDER BY id DESC")
    fun getBestsellerProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND discountPercentage >= 15 ORDER BY discountPercentage DESC")
    fun getDealsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND category = :category ORDER BY id DESC")
    fun getProductsByCategoryFlow(category: String): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products 
        WHERE isActive = 1 
        AND (name LIKE '%' || :query || '%' 
             OR brand LIKE '%' || :query || '%' 
             OR category LIKE '%' || :query || '%' 
             OR sku LIKE '%' || :query || '%'
             OR description LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchProductsFlow(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): ProductEntity?

    @Query("SELECT * FROM products WHERE category = :category AND id != :excludeId AND isActive = 1 LIMIT 6")
    suspend fun getRelatedProducts(category: String, excludeId: Long): List<ProductEntity>

    @Query("SELECT * FROM products WHERE stockQuantity <= lowStockThreshold ORDER BY stockQuantity ASC")
    fun getLowStockProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity <= lowStockThreshold")
    fun getLowStockCountFlow(): Flow<Int>

    @Query("SELECT * FROM products")
    suspend fun getAllProductsList(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = :newStock, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateStock(productId: Long, newStock: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET isActive = :isActive, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateActiveStatus(productId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItemsFlow(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getCartItems(userId: Long): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItem(userId: Long, productId: Long): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteCartItemByProduct(userId: Long, productId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartForUser(userId: Long)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, id DESC")
    fun getAddressesForUserFlow(userId: Long): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, id DESC")
    suspend fun getAddressesForUser(userId: Long): List<AddressEntity>

    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(userId: Long): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: Long)

    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultFlags(userId: Long)

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: Long)
}

@Dao
interface DeliverySlotDao {
    @Query("SELECT * FROM delivery_slots ORDER BY id ASC")
    fun getAllSlotsFlow(): Flow<List<DeliverySlotEntity>>

    @Query("SELECT * FROM delivery_slots WHERE isActive = 1 ORDER BY id ASC")
    fun getActiveSlotsFlow(): Flow<List<DeliverySlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: DeliverySlotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSlots(slots: List<DeliverySlotEntity>)

    @Update
    suspend fun updateSlot(slot: DeliverySlotEntity)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersForUserFlow(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsFlow(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items")
    suspend fun getAllOrderItems(): List<OrderItemEntity>

    @Query("SELECT * FROM orders")
    suspend fun getAllOrdersList(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId")
    suspend fun getOrdersListForUser(userId: Long): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = 'Pending'")
    fun getPendingOrdersCountFlow(): Flow<Int>
}

@Dao
interface StoreSettingsDao {
    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<StoreSettingsEntity?>

    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): StoreSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: StoreSettingsEntity)

    @Update
    suspend fun updateSettings(settings: StoreSettingsEntity)
}

@Dao
interface CsvImportLogDao {
    @Query("SELECT * FROM csv_import_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<CsvImportLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CsvImportLogEntity): Long
}
