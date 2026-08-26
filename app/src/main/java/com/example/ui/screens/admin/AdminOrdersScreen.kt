package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.GrocerySearchBar
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: GroceryViewModel,
    onNavigateToOrderDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val statusTabs = listOf("All", "Pending", "Confirmed", "Preparing", "Out for Delivery", "Delivered", "Cancelled")

    val filteredOrders = remember(allOrders, searchQuery, selectedStatusFilter) {
        allOrders.filter { order ->
            val matchesStatus = selectedStatusFilter == "All" || order.orderStatus.equals(selectedStatusFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    order.orderId.contains(searchQuery, ignoreCase = true) ||
                    order.customerName.contains(searchQuery, ignoreCase = true) ||
                    order.customerPhone.contains(searchQuery, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Fulfillment (${allOrders.size})", fontWeight = FontWeight.Bold) },
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
            // Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                GrocerySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search order ID, customer name or phone..."
                )
            }

            // Status Tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusTabs) { status ->
                    val count = if (status == "All") allOrders.size else allOrders.count { it.orderStatus.equals(status, ignoreCase = true) }
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        label = { Text("$status ($count)") }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No orders match the selected filter.", color = GroceryTextSecondary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderManagementCard(
                            order = order,
                            onUpdateStatus = { next -> viewModel.updateOrderStatus(order.orderId, next) },
                            onClick = { onNavigateToOrderDetail(order.orderId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderManagementCard(
    order: OrderEntity,
    onUpdateStatus: (String) -> Unit,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "${order.customerName} • ${order.customerPhone}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${order.orderId} • $dateStr", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📍 ${order.deliveryAddress}",
                style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🚚 Slot: ${order.deliverySlot} | 💳 ${order.paymentMethod} (${order.paymentStatus})",
                style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 11.sp)
            )

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 Note: ${order.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryOfferYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${order.grandTotal.toInt()} (${order.totalItems} items)",
                    fontWeight = FontWeight.Bold,
                    color = GroceryGreenDark,
                    fontSize = 15.sp
                )

                // Advance status action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    when (order.orderStatus) {
                        "Pending" -> {
                            Button(
                                onClick = { onUpdateStatus("Confirmed") },
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Confirm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        "Confirmed" -> {
                            Button(
                                onClick = { onUpdateStatus("Preparing") },
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Mark Preparing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                        "Preparing" -> {
                            Button(
                                onClick = { onUpdateStatus("Out for Delivery") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Out for Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        "Out for Delivery" -> {
                            Button(
                                onClick = { onUpdateStatus("Delivered") },
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Mark Delivered", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        else -> {}
                    }

                    if (order.orderStatus != "Delivered" && order.orderStatus != "Cancelled") {
                        OutlinedButton(
                            onClick = { onUpdateStatus("Cancelled") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GroceryDiscountBadge),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GroceryDiscountBadge),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
