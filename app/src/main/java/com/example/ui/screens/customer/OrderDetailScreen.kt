package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.OrderItemEntity
import com.example.ui.components.BillRow
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val order = remember(allOrders, orderId) {
        allOrders.firstOrNull { it.orderId == orderId }
    }

    val orderItemsFlow = remember(orderId) {
        viewModel.repository.getOrderItemsForOrder(orderId)
    }
    val orderItems by orderItemsFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Order $orderId", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
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
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GroceryGreenPrimary)
            }
        } else {
            val dateStr = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Status Card & Timeline Tracker
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Order Placed Successfully", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = dateStr, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                                }
                                OrderStatusBadge(status = order.orderStatus)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Timeline Tracker
                            OrderTimelineTracker(currentStatus = order.orderStatus)
                        }
                    }
                }

                // Delivery Details Card
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
                                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = GroceryGreenPrimary)
                                Text("Delivery Address", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = order.deliveryAddress,
                                style = MaterialTheme.typography.bodyMedium.copy(color = GroceryTextSecondary, lineHeight = 20.sp)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Schedule, contentDescription = null, tint = GroceryGreenPrimary)
                                Text("Delivery Slot: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(order.deliverySlot, fontSize = 13.sp)
                            }

                            if (order.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Notes, contentDescription = null, tint = GroceryGreenPrimary)
                                    Text("Note: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(order.notes, fontSize = 13.sp, color = GroceryTextSecondary)
                                }
                            }
                        }
                    }
                }

                // Items list
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Items in Order (${orderItems.size})", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))

                            orderItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            text = "${item.brand} • ${item.weight} x ${item.quantity}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 11.sp)
                                        )
                                    }
                                    Text(
                                        text = "₹${item.itemTotal.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                if (item != orderItems.lastOrNull()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = GroceryOutline.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }

                // Bill Details Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Payment & Bill Breakdown", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))

                            BillRow(label = "Items Subtotal", value = "₹${order.subtotal.toInt()}")
                            if (order.discountAmount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                BillRow(label = "Discount Savings", value = "-₹${order.discountAmount.toInt()}", valueColor = GroceryGreenPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            BillRow(
                                label = "Delivery Fee",
                                value = if (order.deliveryFee == 0.0) "FREE" else "₹${order.deliveryFee.toInt()}",
                                valueColor = if (order.deliveryFee == 0.0) GroceryGreenPrimary else GroceryTextPrimary
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            BillRow(label = "Grand Total", value = "₹${order.grandTotal.toInt()}", isBold = true, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            BillRow(label = "Payment Mode", value = "${order.paymentMethod} (${order.paymentStatus})", fontSize = 12.sp)
                        }
                    }
                }

                // Store Assistance
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GrocerySurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Storefront, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Bankey Bihari Super Store", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Jai Vihar, Najafgarh, New Delhi 110043", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                                Text("Helpline: +91 9811223344", style = MaterialTheme.typography.bodySmall.copy(color = GroceryGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTimelineTracker(currentStatus: String) {
    val steps = listOf("Confirmed", "Preparing", "Out for Delivery", "Delivered")
    val currentStepIndex = when (currentStatus) {
        "Confirmed" -> 0
        "Preparing" -> 1
        "Out for Delivery" -> 2
        "Delivered" -> 3
        "Cancelled" -> -1
        else -> 0
    }

    if (currentStatus == "Cancelled") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Cancel, contentDescription = null, tint = GroceryDiscountBadge)
            Text("This order was cancelled.", fontWeight = FontWeight.Bold, color = GroceryDiscountBadge)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, stepName ->
                val isCompleted = index <= currentStepIndex
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) GroceryGreenPrimary else GrocerySurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.Check else Icons.Filled.Circle,
                            contentDescription = null,
                            tint = if (isCompleted) Color.White else GroceryTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stepName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = if (index == currentStepIndex) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCompleted) GroceryGreenDark else GroceryTextMuted
                        )
                    )
                }
            }
        }
    }
}
