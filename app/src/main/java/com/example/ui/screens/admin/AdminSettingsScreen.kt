package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliverySlotEntity
import com.example.data.model.StoreSettingsEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val storeSettings by viewModel.storeSettings.collectAsState()
    val deliverySlots by viewModel.allDeliverySlots.collectAsState()
    val adminUser by viewModel.adminUser.collectAsState()

    var storeName by remember(storeSettings) { mutableStateOf(storeSettings?.storeName ?: "Bankey Bihari Super Store") }
    var storeAddress by remember(storeSettings) { mutableStateOf(storeSettings?.storeAddress ?: "Jai Vihar, Najafgarh, New Delhi, Delhi 110043, India") }
    var storePhone by remember(storeSettings) { mutableStateOf(storeSettings?.storePhone ?: "+91 9811223344") }
    var storeEmail by remember(storeSettings) { mutableStateOf(storeSettings?.storeEmail ?: "support@bankeybihari.com") }
    var isStoreOpen by remember(storeSettings) { mutableStateOf(storeSettings?.isStoreOpen ?: true) }
    var closedMessage by remember(storeSettings) { mutableStateOf(storeSettings?.closedMessage ?: "Store is temporarily closed for restocking.") }
    var deliveryFeeStr by remember(storeSettings) { mutableStateOf(storeSettings?.deliveryFee?.toInt()?.toString() ?: "30") }
    var freeThresholdStr by remember(storeSettings) { mutableStateOf(storeSettings?.freeDeliveryThreshold?.toInt()?.toString() ?: "499") }
    var minOrderStr by remember(storeSettings) { mutableStateOf(storeSettings?.minOrderValue?.toInt()?.toString() ?: "100") }

    // Admin Credentials Form State
    var adminName by remember(adminUser) { mutableStateOf(adminUser?.name ?: "Store Admin") }
    var adminEmail by remember(adminUser) { mutableStateOf(adminUser?.email ?: "admin@bankeybihari.com") }
    var adminPhone by remember(adminUser) { mutableStateOf(adminUser?.phone ?: "9876543210") }
    var adminPassword by remember(adminUser) { mutableStateOf(adminUser?.passwordHash ?: "admin123") }
    var isAdminPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store & Admin Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val fee = deliveryFeeStr.toDoubleOrNull() ?: 30.0
                            val threshold = freeThresholdStr.toDoubleOrNull() ?: 499.0
                            val minVal = minOrderStr.toDoubleOrNull() ?: 100.0

                            val updated = StoreSettingsEntity(
                                id = storeSettings?.id ?: 1,
                                storeName = storeName.trim(),
                                storeAddress = storeAddress.trim(),
                                storePhone = storePhone.trim(),
                                storeEmail = storeEmail.trim(),
                                isStoreOpen = isStoreOpen,
                                closedMessage = closedMessage.trim(),
                                deliveryFee = fee,
                                freeDeliveryThreshold = threshold,
                                minOrderValue = minVal
                            )
                            viewModel.updateStoreSettings(updated)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_store_settings_button")
                    ) {
                        Text("Save Store Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
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
            // Admin Security & Credentials Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GroceryOfferYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GroceryAmberContainer, shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = GroceryOnAmberContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Admin Email & Security Credentials",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Configure login details used to access the staff portal",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp)
                                )
                            }
                        }

                        Divider(color = GroceryOutline.copy(alpha = 0.5f))

                        OutlinedTextField(
                            value = adminName,
                            onValueChange = { adminName = it },
                            label = { Text("Admin / Manager Name") },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = adminEmail,
                            onValueChange = { adminEmail = it },
                            label = { Text("Admin Email (Login ID)") },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = adminPhone,
                            onValueChange = { input ->
                                adminPhone = input.filter { it.isDigit() }.take(10)
                            },
                            label = { Text("Admin Phone (10 Digits)") },
                            prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { adminPassword = it },
                            label = { Text("Admin Password / PIN") },
                            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isAdminPasswordVisible = !isAdminPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isAdminPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            },
                            visualTransformation = if (isAdminPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (adminEmail.isBlank() || adminPassword.isBlank()) {
                                    viewModel.showMessage("Admin email and password cannot be empty")
                                    return@Button
                                }
                                if (adminPassword.length < 4) {
                                    viewModel.showMessage("Password must be at least 4 characters long")
                                    return@Button
                                }
                                viewModel.updateAdminCredentials(
                                    name = adminName.trim(),
                                    email = adminEmail.trim(),
                                    phone = adminPhone.trim(),
                                    newPassword = adminPassword.trim()
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update Admin Password & Email", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Store Open/Close Switch
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isStoreOpen) GroceryGreenContainer.copy(alpha = 0.5f) else Color(0xFFFEE2E2)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isStoreOpen) GroceryGreenPrimary else GroceryDiscountBadge
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isStoreOpen) "Store Open for Orders" else "Store Temporarily Closed",
                                fontWeight = FontWeight.Bold,
                                color = if (isStoreOpen) GroceryGreenDark else GroceryDiscountBadge
                            )
                            Text("Toggle to instantly pause/resume customer orders", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                        }
                        Switch(
                            checked = isStoreOpen,
                            onCheckedChange = { isStoreOpen = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GroceryGreenPrimary)
                        )
                    }
                }
            }

            if (!isStoreOpen) {
                item {
                    OutlinedTextField(
                        value = closedMessage,
                        onValueChange = { closedMessage = it },
                        label = { Text("Store Closed Announcement Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // General Information
            item {
                Text("Store Information", fontWeight = FontWeight.Bold)
            }

            item {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = storeAddress,
                    onValueChange = { storeAddress = it },
                    label = { Text("Physical Store Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = storePhone,
                        onValueChange = { storePhone = it },
                        label = { Text("Store Phone") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = storeEmail,
                        onValueChange = { storeEmail = it },
                        label = { Text("Store Email") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Delivery & Order Thresholds
            item {
                Text("Delivery Charges & Minimum Order", fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = deliveryFeeStr,
                        onValueChange = { deliveryFeeStr = it },
                        label = { Text("Base Delivery Fee (₹)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = freeThresholdStr,
                        onValueChange = { freeThresholdStr = it },
                        label = { Text("Free Delivery (₹)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minOrderStr,
                        onValueChange = { minOrderStr = it },
                        label = { Text("Min Order (₹)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Delivery Time Slots
            item {
                Text("Delivery Slots in Jai Vihar", fontWeight = FontWeight.Bold)
            }

            items(deliverySlots) { slot ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = slot.slotName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${slot.timeWindow} • Extra fee: ₹${slot.fee.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                        }
                        Switch(
                            checked = slot.isActive,
                            onCheckedChange = {
                                viewModel.toggleDeliverySlot(slot)
                            }
                        )
                    }
                }
            }
        }
    }
}
