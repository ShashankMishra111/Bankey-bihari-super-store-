package com.example.ui.screens.admin

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.MetricCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: GroceryViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCsvImport: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSwitchToCustomerView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storeSettings by viewModel.storeSettings.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val pendingCount by viewModel.pendingOrdersCount.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val customers by viewModel.customersWithStats.collectAsState()
    val analytics by viewModel.salesAnalytics.collectAsState()

    val totalRevenue = remember(allOrders) {
        allOrders.filter { it.orderStatus != "Cancelled" }.sumOf { it.grandTotal }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Store Admin Portal", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Bankey Bihari • Jai Vihar, Delhi",
                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSwitchToCustomerView) {
                        Icon(imageVector = Icons.Filled.Storefront, contentDescription = null, tint = GroceryGreenPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Store View", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold)
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
            // Store Open/Close Status Control Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (storeSettings?.isStoreOpen == true) GroceryGreenContainer.copy(alpha = 0.5f) else Color(0xFFFEE2E2)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (storeSettings?.isStoreOpen == true) GroceryGreenPrimary else GroceryDiscountBadge
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (storeSettings?.isStoreOpen == true) GroceryGreenPrimary else GroceryDiscountBadge
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (storeSettings?.isStoreOpen == true) Icons.Filled.Check else Icons.Filled.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            Column {
                                Text(
                                    text = if (storeSettings?.isStoreOpen == true) "Store is OPEN" else "Store is CLOSED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (storeSettings?.isStoreOpen == true) GroceryGreenDark else GroceryDiscountBadge
                                )
                                Text(
                                    text = if (storeSettings?.isStoreOpen == true) "Accepting orders from Jai Vihar" else "Ordering disabled for customers",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GroceryTextSecondary)
                                )
                            }
                        }

                        Switch(
                            checked = storeSettings?.isStoreOpen == true,
                            onCheckedChange = { viewModel.toggleStoreOpenStatus() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GroceryGreenPrimary),
                            modifier = Modifier.testTag("store_open_toggle")
                        )
                    }
                }
            }

            // Overview Metric Cards (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Total Revenue",
                            value = "₹${totalRevenue.toInt()}",
                            icon = Icons.Filled.CurrencyRupee,
                            accentColor = GroceryGreenPrimary,
                            subtext = "${allOrders.size} total orders",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Pending Orders",
                            value = "$pendingCount",
                            icon = Icons.Filled.PendingActions,
                            accentColor = GroceryOfferYellow,
                            subtext = "Needs fulfillment",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "Low Stock Items",
                            value = "$lowStockCount",
                            icon = Icons.Filled.WarningAmber,
                            accentColor = GroceryDiscountBadge,
                            subtext = "Reorder immediately",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Registered Users",
                            value = "${customers.size}",
                            icon = Icons.Filled.PeopleAlt,
                            accentColor = Color(0xFF0284C7),
                            subtext = "Jai Vihar customers",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Admin Modules Navigation Grid
            item {
                Text(
                    text = "Store Management Modules",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminMenuRow(
                        title = "Products & Catalog",
                        subtitle = "Add, edit prices, descriptions, images & stock",
                        icon = Icons.Filled.Inventory2,
                        onClick = onNavigateToProducts
                    )
                    AdminMenuRow(
                        title = "Inventory & Low Stock Alerts",
                        subtitle = "Quick stock increment/decrement & restock tracker",
                        icon = Icons.Filled.Layers,
                        badge = if (lowStockCount > 0) "$lowStockCount Low" else null,
                        onClick = onNavigateToInventory
                    )
                    AdminMenuRow(
                        title = "Order Fulfillment & Delivery",
                        subtitle = "Track orders, update statuses, view customer notes",
                        icon = Icons.Filled.LocalShipping,
                        badge = if (pendingCount > 0) "$pendingCount New" else null,
                        onClick = onNavigateToOrders
                    )
                    AdminMenuRow(
                        title = "CSV Bulk Product Importer",
                        subtitle = "Import hundreds of grocery items with live validation",
                        icon = Icons.Filled.FileDownload,
                        onClick = onNavigateToCsvImport
                    )
                    AdminMenuRow(
                        title = "Sales Analytics & Reports",
                        subtitle = "Revenue, top selling groceries, brand trends",
                        icon = Icons.Filled.Assessment,
                        onClick = onNavigateToAnalytics
                    )
                    AdminMenuRow(
                        title = "Customer Directory",
                        subtitle = "View customer profiles, order history & total spend",
                        icon = Icons.Filled.People,
                        onClick = onNavigateToCustomers
                    )
                    AdminMenuRow(
                        title = "Store Settings & Time Slots",
                        subtitle = "Operating hours, delivery charges, free threshold",
                        icon = Icons.Filled.Settings,
                        onClick = onNavigateToSettings
                    )
                }
            }

            // Recent Orders section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Store Orders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onNavigateToOrders) {
                        Text("View All (${allOrders.size})", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (allOrders.isEmpty()) {
                item {
                    Text("No orders placed yet.", color = GroceryTextSecondary, fontSize = 13.sp)
                }
            } else {
                items(allOrders.take(5)) { order ->
                    AdminRecentOrderRow(
                        order = order,
                        onStatusAdvance = { nextStatus ->
                            viewModel.updateOrderStatus(order.orderId, nextStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GroceryGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GroceryDiscountBadge
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
            }

            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = GroceryTextMuted)
        }
    }
}

@Composable
fun AdminRecentOrderRow(
    order: OrderEntity,
    onStatusAdvance: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "${order.customerName} (${order.orderId})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "₹${order.grandTotal.toInt()} • ${order.totalItems} items • ${order.deliverySlot}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Status advance button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (order.orderStatus) {
                    "Confirmed" -> {
                        Button(
                            onClick = { onStatusAdvance("Preparing") },
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Mark Preparing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    "Preparing" -> {
                        Button(
                            onClick = { onStatusAdvance("Out for Delivery") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Out for Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    "Out for Delivery" -> {
                        Button(
                            onClick = { onStatusAdvance("Delivered") },
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Mark Delivered", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
