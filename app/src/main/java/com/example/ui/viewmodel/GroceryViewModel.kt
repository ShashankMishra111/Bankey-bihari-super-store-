package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.model.*
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    POPULARITY("Popularity"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    DISCOUNT_HIGH("Highest Discount"),
    NEWEST("Newest First")
}

data class SearchFilterState(
    val query: String = "",
    val selectedCategory: String? = null,
    val selectedBrand: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minDiscount: Int? = null,
    val inStockOnly: Boolean = false,
    val sortOption: SortOption = SortOption.POPULARITY
)

data class CartSummary(
    val items: List<CartItemWithProduct> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalSavings: Double = 0.0,
    val grandTotal: Double = 0.0,
    val totalItemCount: Int = 0,
    val qualifiesForFreeDelivery: Boolean = false,
    val amountNeededForFreeDelivery: Double = 0.0
)

class GroceryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = GroceryRepository(db)

    // UI Message / Toast / Snackbar State
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Current User & Auth
    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    // Store Settings
    val storeSettings: StateFlow<StoreSettingsEntity?> = repository.getStoreSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Categories
    val categories: StateFlow<List<CategoryEntity>> = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Products
    val activeProducts: StateFlow<List<ProductEntity>> = repository.getActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.getFeaturedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dealsProducts: StateFlow<List<ProductEntity>> = repository.getDeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestsellerProducts: StateFlow<List<ProductEntity>> = repository.getBestsellerProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = repository.getLowStockCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingOrdersCount: StateFlow<Int> = repository.getPendingOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Cart & Summary
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.getCartForCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartSummary: StateFlow<CartSummary> = combine(
        cartItems,
        storeSettings
    ) { items, settings ->
        val effectiveSettings = settings ?: StoreSettingsEntity()
        var subtotal = 0.0
        var totalMrp = 0.0
        var totalCount = 0

        for (item in items) {
            subtotal += item.product.sellingPrice * item.quantity
            totalMrp += item.product.mrp * item.quantity
            totalCount += item.quantity
        }

        val discount = (totalMrp - subtotal).coerceAtLeast(0.0)
        val freeDelivery = subtotal >= effectiveSettings.freeDeliveryThreshold
        val deliveryFee = if (items.isEmpty() || freeDelivery) 0.0 else effectiveSettings.deliveryFee
        val grandTotal = if (items.isEmpty()) 0.0 else subtotal + deliveryFee
        val neededForFree = (effectiveSettings.freeDeliveryThreshold - subtotal).coerceAtLeast(0.0)

        CartSummary(
            items = items,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            totalSavings = discount,
            grandTotal = grandTotal,
            totalItemCount = totalCount,
            qualifiesForFreeDelivery = freeDelivery,
            amountNeededForFreeDelivery = neededForFree
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

    // Addresses & Delivery Slots
    val addresses: StateFlow<List<AddressEntity>> = repository.getAddresses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliverySlots: StateFlow<List<DeliverySlotEntity>> = repository.getDeliverySlots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeliverySlots: StateFlow<List<DeliverySlotEntity>> = repository.getAllDeliverySlots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders
    val customerOrders: StateFlow<List<OrderEntity>> = repository.getOrdersForCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Customers
    val customersWithStats: StateFlow<List<CustomerWithStats>> = repository.getAllCustomersWithStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CSV Logs
    val csvImportLogs: StateFlow<List<CsvImportLogEntity>> = repository.getAllCsvImportLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter State
    private val _searchFilter = MutableStateFlow(SearchFilterState())
    val searchFilter: StateFlow<SearchFilterState> = _searchFilter.asStateFlow()

    // Filtered search results
    val searchResults: StateFlow<List<ProductEntity>> = combine(
        activeProducts,
        searchFilter
    ) { products, filter ->
        var list = products

        if (filter.query.isNotBlank()) {
            val q = filter.query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

        if (!filter.selectedCategory.isNullOrBlank()) {
            list = list.filter { it.category.equals(filter.selectedCategory, ignoreCase = true) }
        }

        if (!filter.selectedBrand.isNullOrBlank()) {
            list = list.filter { it.brand.equals(filter.selectedBrand, ignoreCase = true) }
        }

        if (filter.minPrice != null) {
            list = list.filter { it.sellingPrice >= filter.minPrice }
        }
        if (filter.maxPrice != null) {
            list = list.filter { it.sellingPrice <= filter.maxPrice }
        }

        if (filter.minDiscount != null) {
            list = list.filter { it.discountPercentage >= filter.minDiscount }
        }

        if (filter.inStockOnly) {
            list = list.filter { it.stockQuantity > 0 }
        }

        when (filter.sortOption) {
            SortOption.POPULARITY -> list.sortedWith(compareByDescending<ProductEntity> { it.isBestseller }.thenByDescending { it.isFeatured })
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.sellingPrice }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.sellingPrice }
            SortOption.DISCOUNT_HIGH -> list.sortedByDescending { it.discountPercentage }
            SortOption.NEWEST -> list.sortedByDescending { it.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Analytics State
    private val _analyticsTimeframe = MutableStateFlow("Today")
    val analyticsTimeframe: StateFlow<String> = _analyticsTimeframe.asStateFlow()

    private val _salesAnalytics = MutableStateFlow<SalesAnalytics?>(null)
    val salesAnalytics: StateFlow<SalesAnalytics?> = _salesAnalytics.asStateFlow()

    // CSV Import State
    private val _csvInputText = MutableStateFlow(DatabaseSeeder.getSampleCsvContent())
    val csvInputText: StateFlow<String> = _csvInputText.asStateFlow()

    private val _csvValidationResults = MutableStateFlow<List<CsvRowValidationResult>?>(null)
    val csvValidationResults: StateFlow<List<CsvRowValidationResult>?> = _csvValidationResults.asStateFlow()

    private val _isImportingCsv = MutableStateFlow(false)
    val isImportingCsv: StateFlow<Boolean> = _isImportingCsv.asStateFlow()

    // Checkout Flow States
    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    private val _lastPlacedOrderId = MutableStateFlow<String?>(null)
    val lastPlacedOrderId: StateFlow<String?> = _lastPlacedOrderId.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            loadAnalytics("Today")
        }
    }

    // Verification & OTP States
    private val _otpCode = MutableStateFlow<String?>(null)
    val otpCode: StateFlow<String?> = _otpCode.asStateFlow()

    private val _otpTimerSeconds = MutableStateFlow(0)
    val otpTimerSeconds: StateFlow<Int> = _otpTimerSeconds.asStateFlow()

    private val _otpBanner = MutableStateFlow<String?>(null)
    val otpBanner: StateFlow<String?> = _otpBanner.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    fun dismissOtpBanner() {
        _otpBanner.value = null
    }

    private fun generate6DigitOtp(): String {
        return (100000..999999).random().toString()
    }

    private fun startOtpCountdown(seconds: Int = 30) {
        timerJob?.cancel()
        _otpTimerSeconds.value = seconds
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _otpTimerSeconds.value = i
                kotlinx.coroutines.delay(1000)
            }
            _otpTimerSeconds.value = 0
        }
    }

    fun requestSignupVerification(
        name: String,
        email: String,
        phone: String,
        pass: String,
        onOtpSent: (generatedOtp: String) -> Unit
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            // Check if user already exists
            val exists = repository.checkUserExists(email, phone)
            if (exists) {
                _isAuthLoading.value = false
                showMessage("An account with this email or phone already exists. Please Sign In.")
                return@launch
            }

            val otp = generate6DigitOtp()
            _otpCode.value = otp
            startOtpCountdown(30)
            _otpBanner.value = "Bankey Bihari OTP: $otp for +91 $phone"
            _isAuthLoading.value = false
            showMessage("Verification OTP sent to +91 $phone & $email")
            onOtpSent(otp)
        }
    }

    fun verifyAndCompleteSignup(
        name: String,
        email: String,
        phone: String,
        pass: String,
        enteredOtp: String,
        onSuccess: () -> Unit
    ) {
        if (enteredOtp.trim() != _otpCode.value) {
            showMessage("Invalid OTP code. Please enter the correct 6-digit code.")
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.register(name, email, phone, pass, role = "CUSTOMER")
            _isAuthLoading.value = false
            result.onSuccess {
                _otpCode.value = null
                _otpBanner.value = null
                timerJob?.cancel()
                showMessage("Account verified & created! Welcome, ${it.name}")
                onSuccess()
            }.onFailure {
                showMessage(it.message ?: "Registration failed")
            }
        }
    }

    fun requestLoginOtp(
        phoneOrEmail: String,
        onOtpSent: (generatedOtp: String) -> Unit
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val otp = generate6DigitOtp()
            _otpCode.value = otp
            startOtpCountdown(30)
            _otpBanner.value = "Bankey Bihari Login OTP: $otp for $phoneOrEmail"
            _isAuthLoading.value = false
            showMessage("Login verification code sent to $phoneOrEmail")
            onOtpSent(otp)
        }
    }

    fun verifyAndCompletePhoneLogin(
        phoneOrEmail: String,
        enteredOtp: String,
        onSuccess: () -> Unit
    ) {
        if (enteredOtp.trim() != _otpCode.value) {
            showMessage("Invalid OTP code. Please enter the correct 6-digit code.")
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.loginOrRegisterWithVerifiedPhone(phoneOrEmail)
            _isAuthLoading.value = false
            result.onSuccess {
                _otpCode.value = null
                _otpBanner.value = null
                timerJob?.cancel()
                showMessage("Verified & logged in as ${it.name}!")
                onSuccess()
            }.onFailure {
                showMessage(it.message ?: "Login verification failed")
            }
        }
    }

    fun resendOtp(recipient: String) {
        val otp = generate6DigitOtp()
        _otpCode.value = otp
        startOtpCountdown(30)
        _otpBanner.value = "New Bankey Bihari OTP: $otp for $recipient"
        showMessage("New verification code sent to $recipient")
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // Auth actions
    fun login(emailOrPhone: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.login(emailOrPhone, pass)
            result.onSuccess {
                showMessage("Welcome back, ${it.name}!")
                onSuccess()
            }.onFailure {
                showMessage(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, role: String = "CUSTOMER", onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.register(name, email, phone, pass, role)
            result.onSuccess {
                showMessage("Account created! Welcome, ${it.name}")
                onSuccess()
            }.onFailure {
                showMessage(it.message ?: "Registration failed")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        repository.logout()
        showMessage("Logged out")
        onLoggedOut()
    }

    fun switchDemoRole(role: String) {
        viewModelScope.launch {
            repository.switchToDemoUser(role)
            val user = repository.currentUser.value
            showMessage("Switched role to ${user?.role} (${user?.name})")
        }
    }

    // Cart actions
    fun addToCart(productId: Long, qty: Int = 1) {
        viewModelScope.launch {
            val result = repository.addToCart(productId, qty)
            result.onSuccess {
                showMessage("Added to cart")
            }.onFailure {
                showMessage(it.message ?: "Could not add to cart")
            }
        }
    }

    fun updateCartQuantity(productId: Long, newQty: Int) {
        viewModelScope.launch {
            val result = repository.updateCartQuantity(productId, newQty)
            result.onFailure {
                showMessage(it.message ?: "Cannot update quantity")
            }
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
            showMessage("Item removed from cart")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Search & Filter
    fun updateSearchQuery(query: String) {
        _searchFilter.value = _searchFilter.value.copy(query = query)
    }

    fun selectCategoryFilter(category: String?) {
        _searchFilter.value = _searchFilter.value.copy(selectedCategory = category)
    }

    fun selectBrandFilter(brand: String?) {
        _searchFilter.value = _searchFilter.value.copy(selectedBrand = brand)
    }

    fun setSortOption(option: SortOption) {
        _searchFilter.value = _searchFilter.value.copy(sortOption = option)
    }

    fun toggleInStockOnly() {
        val current = _searchFilter.value.inStockOnly
        _searchFilter.value = _searchFilter.value.copy(inStockOnly = !current)
    }

    fun resetFilters() {
        _searchFilter.value = SearchFilterState()
    }

    // Address actions
    fun saveAddress(address: AddressEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveAddress(address)
            showMessage("Address saved")
            onComplete()
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            repository.deleteAddress(addressId)
            showMessage("Address deleted")
        }
    }

    fun setDefaultAddress(addressId: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(addressId)
            showMessage("Default address updated")
        }
    }

    // Checkout & Order Placement
    fun placeOrder(
        address: AddressEntity,
        slot: DeliverySlotEntity,
        paymentMethod: String,
        notes: String = "",
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isPlacingOrder.value = true
            val result = repository.placeOrder(address, slot, paymentMethod, notes)
            _isPlacingOrder.value = false
            result.onSuccess { orderId ->
                _lastPlacedOrderId.value = orderId
                showMessage("Order placed successfully! ID: $orderId")
                loadAnalytics(_analyticsTimeframe.value)
                onSuccess(orderId)
            }.onFailure {
                showMessage(it.message ?: "Failed to place order")
            }
        }
    }

    // Admin Order actions
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            showMessage("Order $orderId marked as $newStatus")
            loadAnalytics(_analyticsTimeframe.value)
        }
    }

    // Admin Product actions
    fun saveProduct(product: ProductEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveProduct(product)
            showMessage("Product '${product.name}' saved")
            onComplete()
        }
    }

    fun updateStock(productId: Long, newStock: Int) {
        viewModelScope.launch {
            repository.updateProductStock(productId, newStock)
            showMessage("Stock updated to $newStock")
        }
    }

    fun toggleProductActive(productId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleProductActive(productId, currentStatus)
            showMessage(if (currentStatus) "Product deactivated" else "Product activated")
        }
    }

    // Admin Settings
    fun updateStoreSettings(settings: StoreSettingsEntity) {
        viewModelScope.launch {
            repository.updateStoreSettings(settings)
            showMessage("Store settings saved")
        }
    }

    fun toggleDeliverySlot(slot: DeliverySlotEntity) {
        viewModelScope.launch {
            repository.updateDeliverySlot(slot.copy(isActive = !slot.isActive))
            showMessage("Slot '${slot.slotName}' updated")
        }
    }

    val adminUser: StateFlow<UserEntity?> = repository.getAdminUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleStoreOpenStatus() {
        viewModelScope.launch {
            val current = storeSettings.value ?: return@launch
            val updated = current.copy(isStoreOpen = !current.isStoreOpen)
            repository.updateStoreSettings(updated)
            showMessage(if (updated.isStoreOpen) "Store is now OPEN for orders" else "Store is now TEMPORARILY CLOSED")
        }
    }

    fun updateAdminCredentials(
        name: String,
        email: String,
        phone: String,
        newPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = repository.updateAdminCredentials(name, email, phone, newPassword)
            result.onSuccess {
                showMessage("Admin credentials updated successfully!")
                onSuccess()
            }.onFailure {
                showMessage(it.message ?: "Failed to update admin credentials")
            }
        }
    }

    // Admin Analytics
    fun setAnalyticsTimeframe(timeframe: String) {
        _analyticsTimeframe.value = timeframe
        loadAnalytics(timeframe)
    }

    fun loadAnalytics(timeframe: String) {
        viewModelScope.launch {
            val stats = repository.getSalesAnalytics(timeframe)
            _salesAnalytics.value = stats
        }
    }

    // Admin CSV Import
    fun setCsvInputText(text: String) {
        _csvInputText.value = text
    }

    fun loadSampleCsvTemplate() {
        _csvInputText.value = DatabaseSeeder.getSampleCsvContent()
        _csvValidationResults.value = null
    }

    fun validateCsv() {
        viewModelScope.launch {
            val results = repository.validateCsvContent(_csvInputText.value)
            _csvValidationResults.value = results
            val validCount = results.count { it.isValid }
            val invalidCount = results.count { !it.isValid }
            showMessage("Validated ${results.size} rows ($validCount valid, $invalidCount invalid)")
        }
    }

    fun executeCsvImport(filename: String = "bulk_products_catalog.csv") {
        viewModelScope.launch {
            val results = _csvValidationResults.value
            if (results.isNullOrEmpty()) {
                showMessage("Please validate CSV before importing")
                return@launch
            }
            _isImportingCsv.value = true
            val log = repository.executeCsvImport(filename, results)
            _isImportingCsv.value = false
            _csvValidationResults.value = null
            showMessage(log.summary)
        }
    }
}
