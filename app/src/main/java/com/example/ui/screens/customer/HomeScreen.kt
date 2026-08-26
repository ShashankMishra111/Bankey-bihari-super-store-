package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@Composable
fun HomeScreen(
    viewModel: GroceryViewModel,
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.storeSettings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val featuredProducts by viewModel.featuredProducts.collectAsState()
    val dealsProducts by viewModel.dealsProducts.collectAsState()
    val bestsellerProducts by viewModel.bestsellerProducts.collectAsState()
    val allActiveProducts by viewModel.activeProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()

    val cartQuantityMap = remember(cartItems) {
        cartItems.associate { it.product.id to it.quantity }
    }

    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            StoreHeader(
                user = currentUser,
                settings = settings,
                onProfileClick = onNavigateToProfile,
                onRoleToggleClick = {
                    if (currentUser?.role == "ADMIN") {
                        onNavigateToAdmin()
                    }
                }
            )
        },
        bottomBar = {
            // Floating Cart Preview Bar if cart has items
            AnimatedVisibility(
                visible = cartSummary.totalItemCount > 0,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color = GroceryGreenPrimary,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCart() }
                        .testTag("floating_cart_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "${cartSummary.totalItemCount} ITEMS | ₹${cartSummary.subtotal.toInt()}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = if (cartSummary.qualifiesForFreeDelivery) "FREE Delivery Unlocked!" else "Add ₹${cartSummary.amountNeededForFreeDelivery.toInt()} for FREE Delivery",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GroceryGreenContainer,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "View Cart",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "View Cart",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = if (cartSummary.totalItemCount > 0) 80.dp else 16.dp)
        ) {
            // Search Bar Trigger
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onNavigateToSearch(null) }
                ) {
                    GrocerySearchBar(
                        query = "",
                        onQueryChange = { onNavigateToSearch(it) },
                        placeholder = "Search atta, dal, ghee, spices, milk...",
                        trailingIcon = {
                            IconButton(onClick = { onNavigateToSearch(null) }) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "Filter",
                                    tint = GroceryGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                }
            }

            // Promotional Banners Carousel / Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToSearch("Atta") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_super_sale_1787714571166),
                        contentDescription = "Super Sale at Bankey Bihari",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Categories horizontal bar
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    SectionHeader(
                        title = "Explore Categories",
                        subtitle = "Fresh staples, dairy, snacks & more",
                        onViewAllClick = onNavigateToCategories
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                categoryName = "All",
                                isSelected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null }
                            )
                        }
                        items(categories) { category ->
                            CategoryChip(
                                categoryName = category.name,
                                isSelected = selectedCategoryFilter == category.name,
                                onClick = {
                                    selectedCategoryFilter = if (selectedCategoryFilter == category.name) null else category.name
                                }
                            )
                        }
                    }
                }
            }

            // Today's Super Deals
            if (selectedCategoryFilter == null && dealsProducts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        SectionHeader(
                            title = "Today's Super Deals",
                            subtitle = "Special discounts on daily essentials",
                            onViewAllClick = { onNavigateToSearch(null) }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(dealsProducts) { product ->
                                val qty = cartQuantityMap[product.id] ?: 0
                                ProductCard(
                                    product = product,
                                    cartQuantity = qty,
                                    onAddToCart = { viewModel.addToCart(product.id) },
                                    onIncreaseQty = { viewModel.updateCartQuantity(product.id, qty + 1) },
                                    onDecreaseQty = { viewModel.updateCartQuantity(product.id, qty - 1) },
                                    onClick = { onNavigateToProductDetail(product.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Secondary Promo Banner: Daily Essentials
            if (selectedCategoryFilter == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToSearch("Daily") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_daily_essentials_1787714588424),
                            contentDescription = "Daily Essentials Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Popular Products & Essentials Grid
            item {
                val displayProducts = if (selectedCategoryFilter != null) {
                    allActiveProducts.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
                } else {
                    featuredProducts.ifEmpty { allActiveProducts }
                }

                Column(modifier = Modifier.padding(top = 12.dp)) {
                    SectionHeader(
                        title = if (selectedCategoryFilter != null) selectedCategoryFilter!! else "Popular in Jai Vihar",
                        subtitle = "${displayProducts.size} items available",
                        onViewAllClick = { onNavigateToSearch(selectedCategoryFilter) }
                    )

                    if (displayProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingBag,
                                    contentDescription = null,
                                    tint = GroceryTextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No products found in this category",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = GroceryTextSecondary)
                                )
                            }
                        }
                    } else {
                        // 2-column grid chunked for LazyColumn performance
                        val chunkedList = displayProducts.chunked(2)
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            chunkedList.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowItems.forEach { product ->
                                        val qty = cartQuantityMap[product.id] ?: 0
                                        ProductCard(
                                            product = product,
                                            cartQuantity = qty,
                                            onAddToCart = { viewModel.addToCart(product.id) },
                                            onIncreaseQty = { viewModel.updateCartQuantity(product.id, qty + 1) },
                                            onDecreaseQty = { viewModel.updateCartQuantity(product.id, qty - 1) },
                                            onClick = { onNavigateToProductDetail(product.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
