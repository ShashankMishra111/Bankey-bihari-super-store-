package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
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
import com.example.ui.components.CategoryChip
import com.example.ui.components.GrocerySearchBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    val filteredProducts = remember(allProducts, searchQuery, selectedCategory) {
        allProducts.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.sku.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || product.category.equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Management (${allProducts.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingProduct = null
                    showAddEditDialog = true
                },
                containerColor = GroceryGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_add_product_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Product")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                GrocerySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Filter by name, brand, or SKU..."
                )
            }

            // Categories Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        categoryName = "All (${allProducts.size})",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(categories) { cat ->
                    val count = allProducts.count { it.category == cat.name }
                    CategoryChip(
                        categoryName = "${cat.name} ($count)",
                        isSelected = selectedCategory == cat.name,
                        onClick = { selectedCategory = if (selectedCategory == cat.name) null else cat.name }
                    )
                }
            }

            // Product List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = product.mainImage,
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GrocerySurfaceVariant)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${product.brand} • ${product.category} • SKU: ${product.sku}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "₹${product.sellingPrice.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        color = GroceryGreenDark,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "MRP ₹${product.mrp.toInt()}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 11.sp)
                                    )
                                    Text(
                                        text = "Stock: ${product.stockQuantity}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (product.stockQuantity <= product.lowStockThreshold) GroceryDiscountBadge else GroceryTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = {
                                        editingProduct = product
                                        showAddEditDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = GroceryGreenPrimary)
                                }

                                Switch(
                                    checked = product.isActive,
                                    onCheckedChange = { viewModel.toggleProductActive(product.id, product.isActive) },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add/Edit Product Dialog
        if (showAddEditDialog) {
            ProductFormDialog(
                initialProduct = editingProduct,
                categories = categories.map { it.name },
                onDismiss = { showAddEditDialog = false },
                onSave = { prod ->
                    viewModel.saveProduct(prod) {
                        showAddEditDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun ProductFormDialog(
    initialProduct: ProductEntity?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var sku by remember { mutableStateOf(initialProduct?.sku ?: "BBS-${(1000..9999).random()}") }
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var brand by remember { mutableStateOf(initialProduct?.brand ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: categories.firstOrNull() ?: "Flours & Grains") }
    var subcategory by remember { mutableStateOf(initialProduct?.subcategory ?: "") }
    var weight by remember { mutableStateOf(initialProduct?.weight ?: "1 kg") }
    var unit by remember { mutableStateOf(initialProduct?.unit ?: "pkt") }
    var priceStr by remember { mutableStateOf(initialProduct?.sellingPrice?.toInt()?.toString() ?: "") }
    var mrpStr by remember { mutableStateOf(initialProduct?.mrp?.toInt()?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(initialProduct?.stockQuantity?.toString() ?: "50") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var imageUrl by remember { mutableStateOf(initialProduct?.mainImage ?: "") }
    var isFeatured by remember { mutableStateOf(initialProduct?.isFeatured ?: false) }
    var isBestseller by remember { mutableStateOf(initialProduct?.isBestseller ?: false) }
    var isActive by remember { mutableStateOf(initialProduct?.isActive ?: true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialProduct == null) "Add Grocery Product" else "Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMsg != null) {
                    Text(errorMsg!!, color = GroceryDiscountBadge, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight / Pack Size (e.g. 5 kg)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Selling Price (₹)") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = mrpStr, onValueChange = { mrpStr = it }, label = { Text("MRP (₹)") }, modifier = Modifier.weight(1f), singleLine = true)
                }

                OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Initial Stock Quantity") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Product Description") }, modifier = Modifier.fillMaxWidth())

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Featured on Home", fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isBestseller, onCheckedChange = { isBestseller = it })
                    Text("Bestseller Tag", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull()
                    val mrp = mrpStr.toDoubleOrNull() ?: price
                    val stock = stockStr.toIntOrNull()

                    if (name.isBlank() || price == null || stock == null) {
                        errorMsg = "Please fill product name, valid price and stock"
                    } else {
                        val discount = if (mrp != null && mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0
                        val prod = ProductEntity(
                            id = initialProduct?.id ?: 0L,
                            sku = sku.trim(),
                            name = name.trim(),
                            brand = brand.trim().ifBlank { "Bankey Bihari" },
                            category = category.trim(),
                            subcategory = subcategory.trim(),
                            description = description.trim().ifBlank { "$name from $brand, high quality product." },
                            weight = weight.trim(),
                            unit = unit.trim(),
                            sellingPrice = price,
                            mrp = mrp ?: price,
                            discountPercentage = discount,
                            stockQuantity = stock,
                            lowStockThreshold = 10,
                            mainImage = imageUrl.trim().ifBlank { "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80" },
                            additionalImages = initialProduct?.additionalImages ?: "",
                            isActive = isActive,
                            isFeatured = isFeatured,
                            isBestseller = isBestseller
                        )
                        onSave(prod)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
