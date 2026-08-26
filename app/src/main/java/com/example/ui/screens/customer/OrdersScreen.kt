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
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: GroceryViewModel,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.customerOrders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Grocery Orders",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (orders.isEmpty()) {
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
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(GroceryGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = GroceryGreenPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Orders Yet",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "You haven't placed any orders yet. Start shopping fresh groceries from Bankey Bihari!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GroceryTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateToHome,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                    ) {
                        Text("Start Shopping")
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
                items(orders) { order ->
                    CustomerOrderCard(
                        order = order,
                        onClick = { onNavigateToOrderDetail(order.orderId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("order_card_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderId,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                    )
                }

                OrderStatusBadge(status = order.orderStatus)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.totalItems} Items | ${order.deliverySlot}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GroceryTextSecondary,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Payment: ${order.paymentMethod} (${order.paymentStatus})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GroceryTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = "₹${order.grandTotal.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GroceryGreenDark,
                        fontSize = 17.sp
                    )
                )
            }
        }
    }
}
