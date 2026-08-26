package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingBag
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
import com.example.data.model.CartItemWithProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: GroceryViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val settings by viewModel.storeSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Your Grocery Cart",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${cartSummary.totalItemCount} items | Bankey Bihari Super Store",
                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                        )
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Clear cart",
                                tint = GroceryDiscountBadge
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                                )
                                Text(
                                    text = "₹${cartSummary.grandTotal.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GroceryTextPrimary,
                                        fontSize = 22.sp
                                    )
                                )
                            }

                            Button(
                                onClick = onNavigateToCheckout,
                                enabled = (settings?.isStoreOpen ?: true) && (cartSummary.subtotal >= (settings?.minOrderValue ?: 0.0)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("proceed_to_checkout_button")
                            ) {
                                Text(
                                    text = "Proceed to Checkout",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        if (settings != null && !settings!!.isStoreOpen) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ordering is currently disabled as store is closed.",
                                style = MaterialTheme.typography.bodySmall.copy(color = GroceryDiscountBadge, fontWeight = FontWeight.Bold)
                            )
                        } else if (settings != null && cartSummary.subtotal < settings!!.minOrderValue) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Minimum order value is ₹${settings!!.minOrderValue.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = GroceryDiscountBadge, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(GroceryGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = GroceryGreenPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GroceryTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Explore fresh atta, dal, rice, ghee and daily snacks from Bankey Bihari Super Store.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GroceryTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToHome,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        modifier = Modifier.testTag("browse_products_button")
                    ) {
                        Text("Browse Grocery Products")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Free delivery progress banner
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (cartSummary.qualifiesForFreeDelivery) GroceryGreenContainer else GroceryAmberContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (cartSummary.qualifiesForFreeDelivery) Icons.Filled.CheckCircle else Icons.Filled.LocalShipping,
                                contentDescription = null,
                                tint = if (cartSummary.qualifiesForFreeDelivery) GroceryGreenDark else GroceryOnAmberContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (cartSummary.qualifiesForFreeDelivery) "Yay! You unlocked FREE Delivery!" else "Add ₹${cartSummary.amountNeededForFreeDelivery.toInt()} more for FREE Delivery",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (cartSummary.qualifiesForFreeDelivery) GroceryGreenDark else GroceryOnAmberContainer
                                    )
                                )
                                Text(
                                    text = "Direct from Bankey Bihari, Jai Vihar, Delhi",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (cartSummary.qualifiesForFreeDelivery) GroceryGreenDark else GroceryOnAmberContainer,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Cart Items List
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncreaseQty = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onDecreaseQty = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.cartItemId) },
                        onClick = { onNavigateToProductDetail(item.product.id) }
                    )
                }

                // Bill Details Breakdown Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bill Details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            BillRow(label = "Items Subtotal", value = "₹${cartSummary.subtotal.toInt()}")
                            
                            if (cartSummary.discount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                BillRow(
                                    label = "Product Discount Savings",
                                    value = "-₹${cartSummary.discount.toInt()}",
                                    valueColor = GroceryGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            BillRow(
                                label = "Delivery Fee",
                                value = if (cartSummary.qualifiesForFreeDelivery) "FREE" else "₹${cartSummary.deliveryFee.toInt()}",
                                valueColor = if (cartSummary.qualifiesForFreeDelivery) GroceryGreenPrimary else GroceryTextPrimary
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            BillRow(
                                label = "Total Savings",
                                value = "₹${cartSummary.totalSavings.toInt()}",
                                valueColor = GroceryGreenPrimary,
                                isBold = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            BillRow(
                                label = "Grand Total",
                                value = "₹${cartSummary.grandTotal.toInt()}",
                                isBold = true,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemWithProduct,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.mainImage,
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GrocerySurfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GroceryTextPrimary,
                        fontSize = 13.sp
                    ),
                    maxLines = 2
                )
                Text(
                    text = item.product.weight,
                    style = MaterialTheme.typography.labelSmall.copy(color = GroceryTextMuted)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "₹${(item.product.sellingPrice * item.quantity).toInt()}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GroceryTextPrimary
                        )
                    )
                    if (item.product.mrp > item.product.sellingPrice) {
                        Text(
                            text = "₹${(item.product.mrp * item.quantity).toInt()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GroceryTextMuted,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Quantity selector
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GroceryGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = onDecreaseQty, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "Minus", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = onIncreaseQty, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Plus", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    value: String,
    valueColor: Color = GroceryTextPrimary,
    isBold: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.sp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isBold) GroceryTextPrimary else GroceryTextSecondary,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontSize = fontSize
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = valueColor,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = fontSize
            )
        )
    }
}
