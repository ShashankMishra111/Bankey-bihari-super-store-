package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.GrocerySearchBar
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: GroceryViewModel,
    initialQuery: String? = null,
    initialCategory: String? = null,
    onNavigateToProductDetail: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchFilter by viewModel.searchFilter.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val allProducts by viewModel.activeProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val cartQuantityMap = remember(cartItems) {
        cartItems.associate { it.product.id to it.quantity }
    }

    var showFilterSheet by remember { mutableStateOf(false) }
    val brands = remember(allProducts) {
        allProducts.map { it.brand }.distinct().sorted()
    }

    LaunchedEffect(initialQuery, initialCategory) {
        if (initialQuery != null) {
            viewModel.updateSearchQuery(initialQuery)
        }
        if (initialCategory != null) {
            viewModel.selectCategoryFilter(initialCategory)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }

                        GrocerySearchBar(
                            query = searchFilter.query,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            placeholder = "Search atta, rice, oil, ghee, biscuits...",
                            trailingIcon = {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Badge(
                                        containerColor = if (searchFilter.selectedCategory != null || searchFilter.selectedBrand != null || searchFilter.inStockOnly) GroceryGreenPrimary else Color.Transparent
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.FilterList,
                                            contentDescription = "Filters",
                                            tint = if (searchFilter.selectedCategory != null || searchFilter.selectedBrand != null || searchFilter.inStockOnly) Color.White else GroceryTextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Horizontal Category filter chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                categoryName = "All",
                                isSelected = searchFilter.selectedCategory == null,
                                onClick = { viewModel.selectCategoryFilter(null) }
                            )
                        }
                        items(categories) { cat ->
                            CategoryChip(
                                categoryName = cat.name,
                                isSelected = searchFilter.selectedCategory == cat.name,
                                onClick = {
                                    viewModel.selectCategoryFilter(if (searchFilter.selectedCategory == cat.name) null else cat.name)
                                }
                            )
                        }
                    }

                    // Sort & Active filters summary row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${searchResults.size} Products found",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GroceryTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        TextButton(
                            onClick = { showFilterSheet = true },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapVert,
                                contentDescription = null,
                                tint = GroceryGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = searchFilter.sortOption.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GroceryGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = null,
                        tint = GroceryTextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GroceryTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try searching for 'Atta', 'Oil', 'Amul', 'Rice' or clear active filters.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GroceryTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(searchResults) { product ->
                    val qty = cartQuantityMap[product.id] ?: 0
                    ProductCard(
                        product = product,
                        cartQuantity = qty,
                        onAddToCart = { viewModel.addToCart(product.id) },
                        onIncreaseQty = { viewModel.updateCartQuantity(product.id, qty + 1) },
                        onDecreaseQty = { viewModel.updateCartQuantity(product.id, qty - 1) },
                        onClick = { onNavigateToProductDetail(product.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Filter & Sort Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sort & Filter",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Reset", color = GroceryDiscountBadge)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Sort By",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column {
                        SortOption.values().forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setSortOption(option) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = searchFilter.sortOption == option,
                                    onClick = { viewModel.setSortOption(option) }
                                )
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (searchFilter.sortOption == option) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Filter by Brand",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(brands) { brand ->
                            FilterChip(
                                selected = searchFilter.selectedBrand == brand,
                                onClick = {
                                    viewModel.selectBrandFilter(if (searchFilter.selectedBrand == brand) null else brand)
                                },
                                label = { Text(brand) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleInStockOnly() }
                    ) {
                        Checkbox(
                            checked = searchFilter.inStockOnly,
                            onCheckedChange = { viewModel.toggleInStockOnly() }
                        )
                        Text(
                            text = "In-Stock Products Only",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Apply Filters (${searchResults.size} results)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
