package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.ui.components.GrocerySearchBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showLowStockOnly by remember { mutableStateOf(false) }

    val displayedProducts = remember(allProducts, lowStockProducts, searchQuery, showLowStockOnly) {
        val baseList = if (showLowStockOnly) lowStockProducts else allProducts
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.brand.contains(searchQuery, ignoreCase = true) ||
            it.sku.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory & Stock Control", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Low stock alert banner
            if (lowStockCount > 0) {
                Surface(
                    color = GroceryDiscountBadge.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryDiscountBadge.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showLowStockOnly = !showLowStockOnly }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = GroceryDiscountBadge)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$lowStockCount Products are Low in Stock!",
                                fontWeight = FontWeight.Bold,
                                color = GroceryDiscountBadge,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (showLowStockOnly) "Showing low stock only (Tap to show all)" else "Tap to filter low stock items",
                                style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }

            // Search and filter row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GrocerySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search stock by item or SKU...",
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = showLowStockOnly,
                    onClick = { showLowStockOnly = !showLowStockOnly },
                    label = { Text("Low Stock") }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedProducts) { product ->
                    InventoryProductRow(
                        product = product,
                        onUpdateStock = { newStock ->
                            viewModel.updateStock(product.id, newStock)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryProductRow(
    product: ProductEntity,
    onUpdateStock: (Int) -> Unit
) {
    val isLowStock = product.stockQuantity <= product.lowStockThreshold
    var isEditingDirectly by remember { mutableStateOf(false) }
    var stockInput by remember { mutableStateOf("${product.stockQuantity}") }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLowStock) Color(0xFFFFF7ED) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLowStock) GroceryOfferYellow else GroceryOutline
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product.mainImage,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GrocerySurfaceVariant)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    Text(
                        text = "${product.brand} • ${product.weight} • SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (product.stockQuantity == 0) GroceryDiscountBadge else if (isLowStock) GroceryAmberContainer else GroceryGreenContainer
                ) {
                    Text(
                        text = if (product.stockQuantity == 0) "OUT OF STOCK" else "${product.stockQuantity} in Stock",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (product.stockQuantity == 0) Color.White else if (isLowStock) GroceryOnAmberContainer else GroceryGreenDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock Quick-Adjust Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onUpdateStock((product.stockQuantity - 1).coerceAtLeast(0)) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("-1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateStock(product.stockQuantity + 5) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+5", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateStock(product.stockQuantity + 20) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+20", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onUpdateStock(product.stockQuantity + 50) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+50", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = { isEditingDirectly = !isEditingDirectly },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(if (isEditingDirectly) "Done" else "Set Qty", fontSize = 11.sp, color = GroceryGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }

            if (isEditingDirectly) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockInput,
                        onValueChange = { stockInput = it },
                        label = { Text("Exact Stock") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val parsed = stockInput.toIntOrNull()
                            if (parsed != null && parsed >= 0) {
                                onUpdateStock(parsed)
                                isEditingDirectly = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}
