package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "ic_flour" -> Icons.Filled.Grain
        "ic_rice" -> Icons.Filled.RiceBowl
        "ic_pulses" -> Icons.Filled.Egg
        "ic_oil" -> Icons.Filled.Opacity
        "ic_spices" -> Icons.Filled.LocalFireDepartment
        "ic_sugar" -> Icons.Filled.Cookie
        "ic_nuts" -> Icons.Filled.Spa
        "ic_biscuits" -> Icons.Filled.Cake
        "ic_snacks" -> Icons.Filled.Fastfood
        "ic_tea" -> Icons.Filled.Coffee
        "ic_cereal" -> Icons.Filled.BreakfastDining
        "ic_dairy" -> Icons.Filled.LocalDrink
        "ic_bread" -> Icons.Filled.BakeryDining
        "ic_beverages" -> Icons.Filled.EmojiFoodBeverage
        "ic_noodles" -> Icons.Filled.RamenDining
        "ic_sauce" -> Icons.Filled.SoupKitchen
        "ic_personal" -> Icons.Filled.CleanHands
        "ic_cleaning" -> Icons.Filled.CleaningServices
        "ic_baby" -> Icons.Filled.ChildCare
        "ic_pooja" -> Icons.Filled.WbSunny
        else -> Icons.Filled.ShoppingBag
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: GroceryViewModel,
    onCategorySelect: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val allProducts by viewModel.activeProducts.collectAsState()

    val productCountByCategory = allProducts.groupingBy { it.category }.eachCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All Grocery Categories",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(categories) { category ->
                val count = productCountByCategory[category.name] ?: 0
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCategorySelect(category.name) }
                        .testTag("category_grid_item_${category.name}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GroceryGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(category.iconName),
                                contentDescription = category.name,
                                tint = GroceryGreenPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GroceryTextPrimary,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "$count items",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GroceryTextMuted,
                                fontSize = 10.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
