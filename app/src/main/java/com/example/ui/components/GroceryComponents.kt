package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.data.model.StoreSettingsEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun StoreHeader(
    user: UserEntity?,
    settings: StoreSettingsEntity?,
    onProfileClick: () -> Unit,
    onRoleToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(GroceryGreenPrimary, GroceryGreenLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "Store Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = settings?.storeName ?: "Bankey Bihari Super Store",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Delivery Location",
                                tint = GroceryGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Delivering to Jai Vihar, Delhi 110043",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GroceryTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (user?.role == "ADMIN") {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GroceryAmberContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onRoleToggleClick() }
                                .testTag("role_toggle_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = GroceryOnAmberContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "ADMIN PORTAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GroceryOnAmberContainer,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            if (settings != null && !settings.isStoreOpen) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = GroceryDiscountBadge.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Store Closed",
                            tint = GroceryDiscountBadge,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = settings.closedMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GroceryDiscountBadge,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GrocerySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    placeholder: String = "Search for atta, milk, biscuits...",
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GrocerySurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = GroceryGreenPrimary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GroceryTextMuted,
                            fontSize = 14.sp
                        )
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("grocery_search_input")
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear search",
                        tint = GroceryTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            trailingIcon?.invoke()
        }
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image and badges container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFF8FAFC))
            ) {
                AsyncImage(
                    model = product.mainImage,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Badge
                if (product.discountPercentage > 0) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 14.dp, bottomEnd = 10.dp),
                        color = GroceryDiscountBadge,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "${product.discountPercentage}% OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Out of stock overlay
                if (product.stockQuantity <= 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = GroceryDiscountBadge,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "OUT OF STOCK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else if (product.stockQuantity <= product.lowStockThreshold) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GroceryAmberContainer,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Only ${product.stockQuantity} left",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GroceryOnAmberContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Info container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GroceryTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GroceryTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        lineHeight = 17.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(34.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.weight,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GroceryTextMuted,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price and Add button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${product.sellingPrice.toInt()}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GroceryTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        if (product.mrp > product.sellingPrice) {
                            Text(
                                text = "₹${product.mrp.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GroceryTextMuted,
                                    textDecoration = TextDecoration.LineThrough,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Dynamic Quantity button
                    if (product.stockQuantity > 0) {
                        if (cartQuantity <= 0) {
                            Button(
                                onClick = onAddToCart,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GroceryGreenPrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("add_to_cart_${product.id}")
                            ) {
                                Text(
                                    text = "ADD",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GroceryGreenPrimary,
                                contentColor = Color.White,
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("qty_selector_${product.id}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    IconButton(
                                        onClick = onDecreaseQty,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Remove,
                                            contentDescription = "Decrease",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Text(
                                        text = "$cartQuantity",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    IconButton(
                                        onClick = onIncreaseQty,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = "Increase",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
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
}

@Composable
fun CategoryChip(
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) GroceryGreenPrimary else GrocerySurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GroceryGreenPrimary else GroceryOutline
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("category_chip_$categoryName")
    ) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else GroceryTextPrimary,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    onViewAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GroceryTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }

        if (onViewAllClick != null) {
            TextButton(
                onClick = onViewAllClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GroceryGreenPrimary
                    )
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = GroceryGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (status) {
        "Confirmed", "Delivered" -> GroceryGreenContainer to GroceryGreenDark
        "Preparing", "Out for Delivery" -> GroceryAmberContainer to GroceryOnAmberContainer
        "Pending" -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
        "Cancelled" -> Color(0xFFFEE2E2) to GroceryDiscountBadge
        else -> GrocerySurfaceVariant to GroceryTextSecondary
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(
                color = fg,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    subtext: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GroceryTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
            )

            if (subtext != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GroceryTextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false,
    color: Color = GroceryTextPrimary,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color)
            else MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                color = color
            )
        )
    }
}

