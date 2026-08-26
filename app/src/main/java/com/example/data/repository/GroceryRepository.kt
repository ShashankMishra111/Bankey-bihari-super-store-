package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GroceryRepository(
    private val db: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val addressDao = db.addressDao()
    private val deliverySlotDao = db.deliverySlotDao()
    private val orderDao = db.orderDao()
    private val storeSettingsDao = db.storeSettingsDao()
    private val csvImportLogDao = db.csvImportLogDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    suspend fun initializeDatabaseIfNeeded() = withContext(ioDispatcher) {
        // Seed users
        val users = userDao.getAllCustomers()
        if (users.isEmpty()) {
            DatabaseSeeder.getDefaultUsers().forEach { userDao.insertUser(it) }
        }

        // Default login to Customer (Sundeep Mishra) for instant smooth demo
        if (_currentUser.value == null) {
            val customer = userDao.getUserById(2) ?: userDao.getUserById(1)
            _currentUser.value = customer
        }

        // Seed categories
        val categories = categoryDao.getAllCategories()
        if (categories.isEmpty()) {
            categoryDao.insertAllCategories(DatabaseSeeder.getDefaultCategories())
        }

        // Seed products
        val products = productDao.getAllProductsList()
        if (products.isEmpty()) {
            productDao.insertAllProducts(DatabaseSeeder.getDefaultProducts())
        }

        // Seed delivery slots
        val slots = deliverySlotDao.getAllSlotsFlow().first()
        if (slots.isEmpty()) {
            deliverySlotDao.insertAllSlots(DatabaseSeeder.getDefaultDeliverySlots())
        }

        // Seed store settings
        val settings = storeSettingsDao.getSettings()
        if (settings == null) {
            storeSettingsDao.insertSettings(StoreSettingsEntity())
        }

        // Seed default address for user 2 if none
        val addresses = addressDao.getAddressesForUser(2)
        if (addresses.isEmpty()) {
            addressDao.insertAddress(DatabaseSeeder.getDefaultAddress(2))
        }
    }

    // --- AUTHENTICATION ---
    suspend fun login(emailOrPhone: String, password: String): Result<UserEntity> = withContext(ioDispatcher) {
        val user = userDao.getUserByEmailOrPhone(emailOrPhone.trim())
        if (user == null) {
            return@withContext Result.failure(Exception("Account not found with this email/phone"))
        }
        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("Incorrect password. Please try again."))
        }
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun checkUserExists(email: String, phone: String): Boolean = withContext(ioDispatcher) {
        val existing = userDao.getUserByEmailOrPhone(email.trim()) ?: userDao.getUserByEmailOrPhone(phone.trim())
        existing != null
    }

    suspend fun getUserByPhoneOrEmail(query: String): UserEntity? = withContext(ioDispatcher) {
        userDao.getUserByEmailOrPhone(query.trim())
    }

    suspend fun loginOrRegisterWithVerifiedPhone(
        phone: String,
        name: String = "Verified Customer"
    ): Result<UserEntity> = withContext(ioDispatcher) {
        val cleanPhone = phone.trim()
        val existing = userDao.getUserByEmailOrPhone(cleanPhone)
        if (existing != null) {
            _currentUser.value = existing
            return@withContext Result.success(existing)
        }
        // If not registered yet, create verified account
        val cleanEmail = "user_${cleanPhone.takeLast(4)}@bankeybihari.in"
        val newUser = UserEntity(
            name = name.ifBlank { "Customer (${cleanPhone.takeLast(4)})" },
            email = cleanEmail,
            phone = cleanPhone,
            passwordHash = "otp_verified",
            role = "CUSTOMER"
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created
        Result.success(created)
    }

    suspend fun register(name: String, email: String, phone: String, password: String, role: String = "CUSTOMER"): Result<UserEntity> = withContext(ioDispatcher) {
        val existing = userDao.getUserByEmailOrPhone(email.trim()) ?: userDao.getUserByEmailOrPhone(phone.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("User already exists with this email or phone"))
        }
        val newUser = UserEntity(
            name = name.trim(),
            email = email.trim(),
            phone = phone.trim(),
            passwordHash = password,
            role = role
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)
        _currentUser.value = created
        Result.success(created)
    }

    fun getAdminUserFlow(): Flow<UserEntity?> = userDao.getAdminUserFlow()

    suspend fun updateAdminCredentials(
        name: String,
        email: String,
        phone: String,
        newPassword: String
    ): Result<UserEntity> = withContext(ioDispatcher) {
        val currentAdmin = userDao.getAdminUser() ?: userDao.getUserById(1) ?: UserEntity(
            id = 1,
            name = "Store Admin",
            email = "admin@bankeybihari.com",
            phone = "9876543210",
            passwordHash = "admin123",
            role = "ADMIN"
        )

        val updatedAdmin = currentAdmin.copy(
            name = name.trim().ifBlank { currentAdmin.name },
            email = email.trim().ifBlank { currentAdmin.email },
            phone = phone.trim().ifBlank { currentAdmin.phone },
            passwordHash = newPassword.trim().ifBlank { currentAdmin.passwordHash }
        )

        userDao.updateUser(updatedAdmin)
        if (_currentUser.value?.role == "ADMIN") {
            _currentUser.value = updatedAdmin
        }
        Result.success(updatedAdmin)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun switchToDemoUser(role: String) = withContext(ioDispatcher) {
        val user = if (role == "ADMIN") {
            userDao.getUserById(1) ?: UserEntity(1, "Store Admin", "admin@bankeybihari.com", "9876543210", "admin123", "ADMIN")
        } else {
            userDao.getUserById(2) ?: UserEntity(2, "Sundeep Mishra", "sundeepmishra3330@gmail.com", "9811223344", "customer123", "CUSTOMER")
        }
        _currentUser.value = user
    }

    // --- CATEGORIES & PRODUCTS ---
    fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getActiveCategoriesFlow()
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategoriesFlow()
    fun getActiveProducts(): Flow<List<ProductEntity>> = productDao.getActiveProductsFlow()
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProductsFlow()
    fun getFeaturedProducts(): Flow<List<ProductEntity>> = productDao.getFeaturedProductsFlow()
    fun getBestsellerProducts(): Flow<List<ProductEntity>> = productDao.getBestsellerProductsFlow()
    fun getDeals(): Flow<List<ProductEntity>> = productDao.getDealsFlow()
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategoryFlow(category)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProductsFlow(query)
    fun getProductByIdFlow(id: Long): Flow<ProductEntity?> = productDao.getProductByIdFlow(id)
    suspend fun getProductById(id: Long): ProductEntity? = withContext(ioDispatcher) { productDao.getProductById(id) }
    suspend fun getRelatedProducts(category: String, excludeId: Long): List<ProductEntity> = withContext(ioDispatcher) {
        productDao.getRelatedProducts(category, excludeId)
    }
    fun getLowStockProducts(): Flow<List<ProductEntity>> = productDao.getLowStockProductsFlow()
    fun getLowStockCount(): Flow<Int> = productDao.getLowStockCountFlow()

    suspend fun saveProduct(product: ProductEntity): Long = withContext(ioDispatcher) {
        if (product.id == 0L) {
            productDao.insertProduct(product)
        } else {
            productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
            product.id
        }
    }

    suspend fun updateProductStock(productId: Long, newStock: Int) = withContext(ioDispatcher) {
        productDao.updateStock(productId, newStock)
    }

    suspend fun toggleProductActive(productId: Long, currentStatus: Boolean) = withContext(ioDispatcher) {
        productDao.updateActiveStatus(productId, !currentStatus)
    }

    // --- CART ---
    fun getCartForCurrentUser(): Flow<List<CartItemWithProduct>> {
        return currentUser.flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                combine(
                    cartDao.getCartItemsFlow(user.id),
                    productDao.getAllProductsFlow()
                ) { cartItems, products ->
                    val productMap = products.associateBy { it.id }
                    cartItems.mapNotNull { item ->
                        productMap[item.productId]?.let { prod ->
                            CartItemWithProduct(
                                cartItemId = item.id,
                                userId = item.userId,
                                quantity = item.quantity,
                                product = prod
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun addToCart(productId: Long, quantityToAdd: Int = 1): Result<Unit> = withContext(ioDispatcher) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Please login to add items to cart"))
        val product = productDao.getProductById(productId) ?: return@withContext Result.failure(Exception("Product not found"))

        if (product.stockQuantity <= 0) {
            return@withContext Result.failure(Exception("${product.name} is currently out of stock"))
        }

        val existingItem = cartDao.getCartItem(user.id, productId)
        val newQuantity = (existingItem?.quantity ?: 0) + quantityToAdd

        if (newQuantity > product.stockQuantity) {
            return@withContext Result.failure(Exception("Only ${product.stockQuantity} items available in stock"))
        }

        if (existingItem != null) {
            cartDao.updateQuantity(existingItem.id, newQuantity)
        } else {
            cartDao.insertCartItem(
                CartItemEntity(
                    userId = user.id,
                    productId = productId,
                    quantity = newQuantity
                )
            )
        }
        Result.success(Unit)
    }

    suspend fun updateCartQuantity(productId: Long, newQuantity: Int): Result<Unit> = withContext(ioDispatcher) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val product = productDao.getProductById(productId) ?: return@withContext Result.failure(Exception("Product not found"))

        if (newQuantity <= 0) {
            cartDao.deleteCartItemByProduct(user.id, productId)
            return@withContext Result.success(Unit)
        }

        if (newQuantity > product.stockQuantity) {
            return@withContext Result.failure(Exception("Cannot exceed available stock (${product.stockQuantity})"))
        }

        val existingItem = cartDao.getCartItem(user.id, productId)
        if (existingItem != null) {
            cartDao.updateQuantity(existingItem.id, newQuantity)
        } else {
            cartDao.insertCartItem(CartItemEntity(userId = user.id, productId = productId, quantity = newQuantity))
        }
        Result.success(Unit)
    }

    suspend fun removeFromCart(cartItemId: Long) = withContext(ioDispatcher) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() = withContext(ioDispatcher) {
        _currentUser.value?.let { cartDao.clearCartForUser(it.id) }
    }

    // --- ADDRESSES ---
    fun getAddresses(): Flow<List<AddressEntity>> {
        return currentUser.flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else addressDao.getAddressesForUserFlow(user.id)
        }
    }

    suspend fun saveAddress(address: AddressEntity): Long = withContext(ioDispatcher) {
        val user = _currentUser.value ?: return@withContext -1L
        if (address.isDefault) {
            addressDao.clearDefaultFlags(user.id)
        }
        val addressWithUser = address.copy(userId = user.id)
        if (address.id == 0L) {
            addressDao.insertAddress(addressWithUser)
        } else {
            addressDao.updateAddress(addressWithUser)
            address.id
        }
    }

    suspend fun deleteAddress(addressId: Long) = withContext(ioDispatcher) {
        addressDao.deleteAddress(addressId)
    }

    suspend fun setDefaultAddress(addressId: Long) = withContext(ioDispatcher) {
        val user = _currentUser.value ?: return@withContext
        addressDao.clearDefaultFlags(user.id)
        addressDao.setDefaultAddress(addressId)
    }

    // --- STORE SETTINGS & DELIVERY SLOTS ---
    fun getStoreSettings(): Flow<StoreSettingsEntity?> = storeSettingsDao.getSettingsFlow()
    suspend fun updateStoreSettings(settings: StoreSettingsEntity) = withContext(ioDispatcher) {
        storeSettingsDao.updateSettings(settings)
    }
    fun getDeliverySlots(): Flow<List<DeliverySlotEntity>> = deliverySlotDao.getActiveSlotsFlow()
    fun getAllDeliverySlots(): Flow<List<DeliverySlotEntity>> = deliverySlotDao.getAllSlotsFlow()
    suspend fun updateDeliverySlot(slot: DeliverySlotEntity) = withContext(ioDispatcher) {
        deliverySlotDao.updateSlot(slot)
    }

    // --- ORDERS & CHECKOUT ---
    suspend fun placeOrder(
        address: AddressEntity,
        slot: DeliverySlotEntity,
        paymentMethod: String,
        notes: String = ""
    ): Result<String> = withContext(ioDispatcher) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Please login to place order"))
        val settings = storeSettingsDao.getSettings() ?: StoreSettingsEntity()

        if (!settings.isStoreOpen) {
            return@withContext Result.failure(Exception(settings.closedMessage))
        }

        val cartItems = cartDao.getCartItems(user.id)
        if (cartItems.isEmpty()) {
            return@withContext Result.failure(Exception("Your cart is empty"))
        }

        // Fetch products and verify stock
        val products = productDao.getAllProductsList().associateBy { it.id }
        var subtotal = 0.0
        var totalMrp = 0.0
        var totalItemsCount = 0

        val orderItemsToInsert = mutableListOf<OrderItemEntity>()

        for (item in cartItems) {
            val product = products[item.productId]
                ?: return@withContext Result.failure(Exception("Product no longer available"))

            if (product.stockQuantity < item.quantity) {
                return@withContext Result.failure(Exception("Insufficient stock for ${product.name} (Available: ${product.stockQuantity})"))
            }

            val itemTotal = product.sellingPrice * item.quantity
            subtotal += itemTotal
            totalMrp += (product.mrp * item.quantity)
            totalItemsCount += item.quantity
        }

        if (subtotal < settings.minOrderValue) {
            return@withContext Result.failure(Exception("Minimum order value is ₹${settings.minOrderValue.toInt()}"))
        }

        val discountAmount = (totalMrp - subtotal).coerceAtLeast(0.0)
        val deliveryFee = if (subtotal >= settings.freeDeliveryThreshold) 0.0 else settings.deliveryFee + slot.fee
        val grandTotal = subtotal + deliveryFee
        val totalSavings = discountAmount

        // Generate unique human-readable Order ID
        val randomDigits = (1000..9999).random()
        val orderId = "BBS-${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}-$randomDigits"

        val formattedAddress = "${address.name}, ${address.phone}\n${address.houseFlat}, ${address.street}, ${address.area}\n${address.landmark.let { if (it.isNotBlank()) "Near $it, " else "" }}${address.city}, ${address.state} - ${address.pinCode}"

        val order = OrderEntity(
            orderId = orderId,
            userId = user.id,
            customerName = user.name,
            customerPhone = address.phone.ifBlank { user.phone },
            subtotal = subtotal,
            discountAmount = discountAmount,
            deliveryFee = deliveryFee,
            totalSavings = totalSavings,
            grandTotal = grandTotal,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "Cash on Delivery") "Pending (COD)" else "Paid",
            deliverySlot = "${slot.slotName} (${slot.timeWindow})",
            deliveryAddress = formattedAddress,
            orderStatus = "Confirmed",
            totalItems = totalItemsCount,
            notes = notes
        )

        for (item in cartItems) {
            val product = products[item.productId]!!
            orderItemsToInsert.add(
                OrderItemEntity(
                    orderId = orderId,
                    productId = product.id,
                    productName = product.name,
                    brand = product.brand,
                    weight = product.weight,
                    price = product.sellingPrice,
                    mrp = product.mrp,
                    quantity = item.quantity,
                    itemTotal = product.sellingPrice * item.quantity
                )
            )
            // Reduce stock
            val updatedStock = (product.stockQuantity - item.quantity).coerceAtLeast(0)
            productDao.updateStock(product.id, updatedStock)
        }

        // Insert Order and Order Items
        orderDao.insertOrder(order)
        orderDao.insertOrderItems(orderItemsToInsert)

        // Clear user cart
        cartDao.clearCartForUser(user.id)

        Result.success(orderId)
    }

    fun getOrdersForCurrentUser(): Flow<List<OrderEntity>> {
        return currentUser.flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else orderDao.getOrdersForUserFlow(user.id)
        }
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrdersFlow()
    fun getPendingOrdersCount(): Flow<Int> = orderDao.getPendingOrdersCountFlow()
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?> = orderDao.getOrderByIdFlow(orderId)
    fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>> = orderDao.getOrderItemsFlow(orderId)

    suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(ioDispatcher) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    // --- ADMIN CUSTOMERS & ANALYTICS ---
    fun getAllCustomersWithStats(): Flow<List<CustomerWithStats>> {
        return combine(
            userDao.getAllCustomersFlow(),
            orderDao.getAllOrdersFlow()
        ) { customers, orders ->
            val ordersByUserId = orders.groupBy { it.userId }
            customers.map { user ->
                val userOrders = ordersByUserId[user.id] ?: emptyList()
                val totalSpent = userOrders.filter { it.orderStatus != "Cancelled" }.sumOf { it.grandTotal }
                val lastOrder = userOrders.maxByOrNull { it.createdAt }
                CustomerWithStats(
                    user = user,
                    totalOrders = userOrders.size,
                    totalSpent = totalSpent,
                    lastOrderDate = lastOrder?.createdAt
                )
            }
        }
    }

    suspend fun getSalesAnalytics(timeframe: String): SalesAnalytics = withContext(ioDispatcher) {
        val allOrders = orderDao.getAllOrdersList()
        val allOrderItems = orderDao.getAllOrderItems()
        val lowStockCount = productDao.getLowStockCountFlow().first()

        val calendar = Calendar.getInstance()
        val cutoffTimestamp = when (timeframe) {
            "Today" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            "Last 7 Days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.timeInMillis
            }
            else -> 0L
        }

        val filteredOrders = allOrders.filter { it.createdAt >= cutoffTimestamp && it.orderStatus != "Cancelled" }
        val pendingOrders = allOrders.count { it.orderStatus == "Pending" || it.orderStatus == "Confirmed" }
        val orderIds = filteredOrders.map { it.orderId }.toSet()
        val filteredItems = allOrderItems.filter { it.orderId in orderIds }

        val totalRevenue = filteredOrders.sumOf { it.grandTotal }
        val totalOrders = filteredOrders.size
        val avgOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0
        val totalUnits = filteredItems.sumOf { it.quantity }

        val topProducts = filteredItems
            .groupBy { it.productName }
            .map { (name, items) ->
                TopProductStat(
                    productName = name,
                    unitsSold = items.sumOf { it.quantity },
                    revenue = items.sumOf { it.itemTotal }
                )
            }
            .sortedByDescending { it.revenue }
            .take(5)

        // Group by product brand/category estimation
        val categorySales = filteredItems
            .groupBy { it.brand }
            .map { (brand, items) ->
                CategorySalesStat(
                    categoryName = brand.ifBlank { "Grocery" },
                    unitsSold = items.sumOf { it.quantity },
                    revenue = items.sumOf { it.itemTotal }
                )
            }
            .sortedByDescending { it.revenue }
            .take(5)

        SalesAnalytics(
            totalRevenue = totalRevenue,
            totalOrders = totalOrders,
            avgOrderValue = avgOrderValue,
            totalUnitsSold = totalUnits,
            pendingOrdersCount = pendingOrders,
            lowStockCount = lowStockCount,
            topProducts = topProducts,
            categorySales = categorySales
        )
    }

    // --- CSV BULK IMPORT ENGINE ---
    fun getAllCsvImportLogs(): Flow<List<CsvImportLogEntity>> = csvImportLogDao.getAllLogsFlow()

    suspend fun validateCsvContent(csvString: String): List<CsvRowValidationResult> = withContext(ioDispatcher) {
        val categories = categoryDao.getAllCategories().map { it.name.trim().lowercase() }.toSet()
        val existingProducts = productDao.getAllProductsList()
        val existingSkus = existingProducts.map { it.sku.trim().uppercase() }.toSet()

        val lines = csvString.trim().lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return@withContext emptyList()

        val seenSkusInThisFile = mutableSetOf<String>()
        val results = mutableListOf<CsvRowValidationResult>()

        // Check header
        val dataLines = if (lines.first().contains("SKU", ignoreCase = true) && lines.first().contains("Name", ignoreCase = true)) {
            lines.drop(1)
        } else {
            lines
        }

        for ((index, line) in dataLines.withIndex()) {
            val rowNum = index + 1
            val cols = line.split(",").map { it.trim().removeSurrounding("\"") }
            val errors = mutableListOf<String>()

            if (cols.size < 8) {
                results.add(
                    CsvRowValidationResult(
                        rowNumber = rowNum,
                        sku = cols.getOrNull(0) ?: "Unknown",
                        name = cols.getOrNull(1) ?: "Unknown",
                        category = cols.getOrNull(3) ?: "",
                        price = null,
                        mrp = null,
                        stock = null,
                        isValid = false,
                        errors = listOf("Row has insufficient columns (expected at least 8, found ${cols.size})"),
                        parsedProduct = null
                    )
                )
                continue
            }

            val sku = cols.getOrElse(0) { "" }.trim()
            val name = cols.getOrElse(1) { "" }.trim()
            val brand = cols.getOrElse(2) { "" }.trim()
            val category = cols.getOrElse(3) { "" }.trim()
            val subcategory = cols.getOrElse(4) { "" }.trim()
            val priceStr = cols.getOrElse(5) { "" }.trim()
            val mrpStr = cols.getOrElse(6) { "" }.trim()
            val stockStr = cols.getOrElse(7) { "" }.trim()
            val discountStr = cols.getOrElse(8) { "0" }.trim()
            val description = cols.getOrElse(9) { "" }.trim()
            val weight = cols.getOrElse(10) { "1 unit" }.trim()
            val unit = cols.getOrElse(11) { "pkt" }.trim()
            val imageUrl = cols.getOrElse(12) { "" }.trim()

            if (sku.isBlank()) errors.add("SKU is required")
            val upperSku = sku.uppercase()
            if (seenSkusInThisFile.contains(upperSku)) {
                errors.add("Duplicate SKU '$sku' in CSV")
            } else {
                seenSkusInThisFile.add(upperSku)
            }

            if (name.isBlank()) errors.add("Product name is required")
            if (category.isBlank()) {
                errors.add("Category is required")
            } else if (!categories.contains(category.lowercase())) {
                errors.add("Category '$category' not recognized")
            }

            val price = priceStr.toDoubleOrNull()
            if (price == null || price <= 0.0) {
                errors.add("Price must be a valid positive number")
            }

            val mrp = mrpStr.toDoubleOrNull()
            if (mrp == null || mrp <= 0.0) {
                errors.add("MRP must be a valid positive number")
            } else if (price != null && mrp < price) {
                errors.add("MRP ($mrp) cannot be less than Selling Price ($price)")
            }

            val stock = stockStr.toIntOrNull()
            if (stock == null || stock < 0) {
                errors.add("Stock must be a non-negative whole integer")
            }

            val discount = discountStr.toIntOrNull() ?: if (price != null && mrp != null && mrp > 0) {
                (((mrp - price) / mrp) * 100).toInt()
            } else 0

            val isValid = errors.isEmpty()
            val parsedProduct = if (isValid) {
                val existing = existingProducts.firstOrNull { it.sku.equals(sku, ignoreCase = true) }
                ProductEntity(
                    id = existing?.id ?: 0L,
                    sku = sku,
                    name = name,
                    brand = brand.ifBlank { "Bankey Bihari" },
                    category = category,
                    subcategory = subcategory,
                    description = description.ifBlank { "$name from $brand, fresh quality guaranteed." },
                    weight = weight,
                    unit = unit,
                    sellingPrice = price ?: 0.0,
                    mrp = mrp ?: 0.0,
                    discountPercentage = discount,
                    stockQuantity = stock ?: 0,
                    lowStockThreshold = 10,
                    mainImage = imageUrl.ifBlank { "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80" },
                    additionalImages = "",
                    isActive = true,
                    isFeatured = false,
                    isBestseller = false
                )
            } else null

            results.add(
                CsvRowValidationResult(
                    rowNumber = rowNum,
                    sku = sku,
                    name = name,
                    category = category,
                    price = price,
                    mrp = mrp,
                    stock = stock,
                    isValid = isValid,
                    errors = errors,
                    parsedProduct = parsedProduct
                )
            )
        }

        results
    }

    suspend fun executeCsvImport(filename: String, validationResults: List<CsvRowValidationResult>): CsvImportLogEntity = withContext(ioDispatcher) {
        val validRows = validationResults.filter { it.isValid && it.parsedProduct != null }
        val invalidRows = validationResults.filter { !it.isValid }

        if (validRows.isNotEmpty()) {
            val productsToSave = validRows.map { it.parsedProduct!! }
            productDao.insertAllProducts(productsToSave)
        }

        val status = when {
            invalidRows.isEmpty() -> "SUCCESS"
            validRows.isNotEmpty() -> "PARTIAL"
            else -> "FAILED"
        }

        val log = CsvImportLogEntity(
            filename = filename,
            totalRows = validationResults.size,
            validRows = validRows.size,
            invalidRows = invalidRows.size,
            status = status,
            summary = "Imported ${validRows.size} products successfully. ${invalidRows.size} rows failed validation."
        )

        csvImportLogDao.insertLog(log)
        log
    }
}
