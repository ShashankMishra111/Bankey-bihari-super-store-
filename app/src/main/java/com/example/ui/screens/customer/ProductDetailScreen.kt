package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: GroceryViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val product = remember(allProducts, productId) {
        allProducts.firstOrNull { it.id == productId }
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val currentCartItem = remember(cartItems, productId) {
        cartItems.firstOrNull { it.product.id == productId }
    }
    val cartQty = currentCartItem?.quantity ?: 0

    val relatedProducts = remember(allProducts, product) {
        if (product == null) emptyList()
        else allProducts.filter { it.category == product.category && it.id != product.id && it.isActive }.take(6)
    }

    var selectedImageIndex by remember { mutableStateOf(0) }
    val imageList = remember(product) {
        if (product == null) emptyList()
        else {
            val list = mutableListOf(product.mainImage)
            if (product.additionalImages.isNotBlank()) {
                list.addAll(product.additionalImages.split(",").map { it.trim() }.filter { it.isNotBlank() })
            }
            list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Product Details", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Badge(containerColor = GroceryDiscountBadge) {
                            Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = GroceryGreenPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (product != null && product.stockQuantity > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (cartQty <= 0) {
                            OutlinedButton(
                                onClick = { viewModel.addToCart(product.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GroceryGreenPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, GroceryGreenPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("detail_add_to_cart")
                            ) {
                                Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add to Cart", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GroceryGreenContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { viewModel.updateCartQuantity(product.id, cartQty - 1) }) {
                                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease", tint = GroceryGreenDark)
                                    }
                                    Text(
                                        text = "$cartQty in Cart",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GroceryGreenDark
                                        )
                                    )
                                    IconButton(onClick = { viewModel.updateCartQuantity(product.id, cartQty + 1) }) {
                                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", tint = GroceryGreenDark)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (cartQty <= 0) {
                                    viewModel.addToCart(product.id)
                                }
                                onNavigateToCart()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_buy_now")
                        ) {
                            Text(
                                text = "Buy Now",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Large Image Hero & Gallery
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color(0xFFF8FAFC))
                    ) {
                        val currentImg = imageList.getOrNull(selectedImageIndex) ?: product.mainImage
                        AsyncImage(
                            model = currentImg,
                            contentDescription = product.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )

                        if (product.discountPercentage > 0) {
                            Surface(
                                shape = RoundedCornerShape(bottomEnd = 12.dp),
                                color = GroceryDiscountBadge,
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "${product.discountPercentage}% OFF",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Gallery thumbnails if multiple
                    if (imageList.size > 1) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(imageList.indices.toList()) { index ->
                                val img = imageList[index]
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        2.dp,
                                        if (selectedImageIndex == index) GroceryGreenPrimary else GroceryOutline
                                    ),
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedImageIndex = index }
                                ) {
                                    AsyncImage(
                                        model = img,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                // Details Content
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Brand and category
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.brand.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = GroceryGreenPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GrocerySurfaceVariant
                            ) {
                                Text(
                                    text = product.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GroceryTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Product Name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GroceryTextPrimary,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Net Qty: ${product.weight} (${product.unit})",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GroceryTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price & Savings banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GroceryGreenContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "₹${product.sellingPrice.toInt()}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GroceryGreenDark,
                                            fontSize = 24.sp
                                        )
                                    )

                                    if (product.mrp > product.sellingPrice) {
                                        Text(
                                            text = "MRP ₹${product.mrp.toInt()}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = GroceryTextMuted,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        )
                                    }
                                }

                                if (product.mrp > product.sellingPrice) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GroceryGreenPrimary
                                    ) {
                                        Text(
                                            text = "Save ₹${(product.mrp - product.sellingPrice).toInt()}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Delivery & Stock info cards
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ElectricBolt,
                                        contentDescription = null,
                                        tint = GroceryOfferYellow,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Superfast Delivery in Jai Vihar",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Order now & get it delivered in 30-45 mins",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (product.stockQuantity > 0) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                        contentDescription = null,
                                        tint = if (product.stockQuantity > 0) GroceryGreenPrimary else GroceryDiscountBadge,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (product.stockQuantity > 0) "In Stock (${product.stockQuantity} available)" else "Currently Out of Stock",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (product.stockQuantity > 0) GroceryGreenPrimary else GroceryDiscountBadge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text(
                            text = "Product Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.description.ifBlank { "Guaranteed fresh and authentic quality direct from Bankey Bihari Super Store, Jai Vihar." },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GroceryTextSecondary,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "SKU: ${product.sku}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GroceryTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Related Products Section
                if (relatedProducts.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            SectionHeader(
                                title = "You Might Also Need",
                                subtitle = "Frequently bought together in ${product.category}"
                            )

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(relatedProducts) { related ->
                                    val relQty = cartItems.firstOrNull { it.product.id == related.id }?.quantity ?: 0
                                    ProductCard(
                                        product = related,
                                        cartQuantity = relQty,
                                        onAddToCart = { viewModel.addToCart(related.id) },
                                        onIncreaseQty = { viewModel.updateCartQuantity(related.id, relQty + 1) },
                                        onDecreaseQty = { viewModel.updateCartQuantity(related.id, relQty - 1) },
                                        onClick = { onNavigateToProductDetail(related.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
