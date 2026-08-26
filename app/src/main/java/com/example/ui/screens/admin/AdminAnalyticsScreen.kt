package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeframe by viewModel.analyticsTimeframe.collectAsState()
    val analytics by viewModel.salesAnalytics.collectAsState()

    val timeframes = listOf("Today", "Last 7 Days", "This Month")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Analytics & Reports", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeframe Segmented Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeframes.forEach { tf ->
                        val isSelected = timeframe == tf
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GroceryGreenPrimary else GrocerySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GroceryGreenPrimary else GroceryOutline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextButton(
                                onClick = { viewModel.setAnalyticsTimeframe(tf) },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = tf,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) Color.White else GroceryTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Key Metrics Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "$timeframe Revenue",
                            value = "₹${analytics?.totalRevenue?.toInt() ?: 0}",
                            icon = Icons.Filled.CurrencyRupee,
                            accentColor = GroceryGreenPrimary,
                            subtext = "Fulfillment total",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Total Orders",
                            value = "${analytics?.totalOrders ?: 0}",
                            icon = Icons.Filled.ShoppingBag,
                            accentColor = Color(0xFF0284C7),
                            subtext = "Completed orders",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Avg Order Value",
                            value = "₹${analytics?.avgOrderValue?.toInt() ?: 0}",
                            icon = Icons.Filled.ShowChart,
                            accentColor = GroceryOfferYellow,
                            subtext = "Basket size",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Units Sold",
                            value = "${analytics?.totalUnitsSold ?: 0}",
                            icon = Icons.Filled.Inventory,
                            accentColor = Color(0xFF8B5CF6),
                            subtext = "Grocery items delivered",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Top Selling Products Leaderboard
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Leaderboard, contentDescription = null, tint = GroceryGreenPrimary)
                            Text("Top Selling Grocery Items", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val topProds = analytics?.topProducts ?: emptyList()
                        if (topProds.isEmpty()) {
                            Text("No product sales data recorded yet in this timeframe.", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                        } else {
                            topProds.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            color = GroceryGreenPrimary,
                                            fontSize = 13.sp
                                        )
                                        Column {
                                            Text(text = item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                                            Text(text = "${item.unitsSold} units sold", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                                        }
                                    }

                                    Text(
                                        text = "₹${item.revenue.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        color = GroceryGreenDark,
                                        fontSize = 13.sp
                                    )
                                }
                                if (index < topProds.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = GroceryOutline.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }

            // Category/Brand breakdown
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.PieChart, contentDescription = null, tint = GroceryGreenPrimary)
                            Text("Brand Performance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val catSales = analytics?.categorySales ?: emptyList()
                        if (catSales.isEmpty()) {
                            Text("No brand sales data recorded yet.", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                        } else {
                            catSales.forEach { cat ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat.categoryName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                    Text(text = "₹${cat.revenue.toInt()} (${cat.unitsSold} sold)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GroceryTextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
