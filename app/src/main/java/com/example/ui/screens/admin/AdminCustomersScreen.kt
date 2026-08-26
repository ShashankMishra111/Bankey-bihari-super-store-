package com.example.ui.screens.admin

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
import com.example.data.model.CustomerWithStats
import com.example.ui.components.GrocerySearchBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCustomersScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customersWithStats.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.user.name.contains(searchQuery, ignoreCase = true) ||
            it.user.phone.contains(searchQuery, ignoreCase = true) ||
            it.user.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Directory (${customers.size})", fontWeight = FontWeight.Bold) },
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
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                GrocerySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search customers by name, phone or email..."
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCustomers) { item ->
                    CustomerDirectoryCard(customer = item)
                }
            }
        }
    }
}

@Composable
fun CustomerDirectoryCard(customer: CustomerWithStats) {
    val lastOrderStr = customer.lastOrderDate?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Never"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(GroceryGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.user.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GroceryGreenDark)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = customer.user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "📞 ${customer.user.phone}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                Text(text = "✉️ ${customer.user.email}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 11.sp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Last Order: $lastOrderStr",
                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 10.sp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${customer.totalSpent.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = GroceryGreenDark,
                    fontSize = 15.sp
                )
                Text(
                    text = "${customer.totalOrders} Orders",
                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                )
            }
        }
    }
}
