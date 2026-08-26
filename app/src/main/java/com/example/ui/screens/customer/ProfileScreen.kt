package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GroceryViewModel,
    onNavigateToAddresses: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.storeSettings.collectAsState()
    var showStoreInfoDialog by remember { mutableStateOf(false) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }
    var adminEmailOrPhone by remember { mutableStateOf("admin@bankeybihari.com") }
    var adminPassword by remember { mutableStateOf("") }
    var adminAuthError by remember { mutableStateOf<String?>(null) }
    var isAuthenticatingAdmin by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (currentUser?.role == "ADMIN") GroceryOfferYellow else GroceryGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (currentUser?.name?.take(1) ?: "U").uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.name ?: "Guest Customer",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = currentUser?.phone ?: "No phone number",
                                style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                            )
                            Text(
                                text = currentUser?.email ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 11.sp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentUser?.role == "ADMIN") GroceryAmberContainer else GroceryGreenContainer
                        ) {
                            Text(
                                text = currentUser?.role ?: "CUSTOMER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (currentUser?.role == "ADMIN") GroceryOnAmberContainer else GroceryGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Only show Admin Management Banner if already authenticated as ADMIN
            if (currentUser?.role == "ADMIN") {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOfferYellow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = GroceryOnAmberContainer
                                )
                                Column {
                                    Text(
                                        text = "Store Admin Portal Active",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Inventory, orders, analytics & CSV import",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GroceryTextSecondary)
                                    )
                                }
                            }

                            Button(
                                onClick = onNavigateToAdmin,
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "Dashboard",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Options
            item {
                Text(
                    text = "Account Actions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (currentUser?.role == "ADMIN") {
                item {
                    ProfileMenuRow(
                        icon = Icons.Filled.Dashboard,
                        title = "Store Admin Portal",
                        subtitle = "Inventory, orders, CSV bulk import, analytics",
                        onClick = onNavigateToAdmin
                    )
                }
            }

            item {
                ProfileMenuRow(
                    icon = Icons.Outlined.ReceiptLong,
                    title = "My Orders",
                    subtitle = "Track, view past grocery deliveries",
                    onClick = onNavigateToOrders
                )
            }

            item {
                ProfileMenuRow(
                    icon = Icons.Outlined.LocationOn,
                    title = "Delivery Addresses",
                    subtitle = "Manage home, flat, and work addresses in Jai Vihar",
                    onClick = onNavigateToAddresses
                )
            }

            item {
                ProfileMenuRow(
                    icon = Icons.Outlined.Storefront,
                    title = "About Bankey Bihari Super Store",
                    subtitle = "Location, operating hours & helpline in Jai Vihar",
                    onClick = { showStoreInfoDialog = true }
                )
            }

            item {
                ProfileMenuRow(
                    icon = Icons.Outlined.SwitchAccount,
                    title = "Switch Account / Customer Login",
                    subtitle = "Login with mobile or email",
                    onClick = onNavigateToAuth
                )
            }

            // Store Staff Access (Secured with Password / PIN)
            if (currentUser?.role != "ADMIN") {
                item {
                    ProfileMenuRow(
                        icon = Icons.Outlined.Security,
                        title = "Store Staff & Admin Login",
                        subtitle = "Restricted access for store managers & staff",
                        onClick = {
                            adminEmailOrPhone = "admin@bankeybihari.com"
                            adminPassword = ""
                            adminAuthError = null
                            showAdminAuthDialog = true
                        }
                    )
                }
            }

            item {
                ProfileMenuRow(
                    icon = Icons.Outlined.Logout,
                    title = "Logout",
                    subtitle = "Sign out from this device",
                    textColor = GroceryDiscountBadge,
                    onClick = {
                        viewModel.logout(onLoggedOut = onNavigateToAuth)
                    }
                )
            }
        }

        // Store Admin Security Verification Dialog
        if (showAdminAuthDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isAuthenticatingAdmin) {
                        showAdminAuthDialog = false
                        adminAuthError = null
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = GroceryOfferYellow,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Store Staff & Admin Access",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "This section is restricted to Bankey Bihari store managers and authorized staff only. Customers cannot access without admin credentials.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                        )

                        OutlinedTextField(
                            value = adminEmailOrPhone,
                            onValueChange = { adminEmailOrPhone = it },
                            label = { Text("Admin Email / Phone") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { adminPassword = it },
                            label = { Text("Admin Password / PIN") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        adminAuthError?.let { err ->
                            Text(
                                text = err,
                                color = GroceryDiscountBadge,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (adminEmailOrPhone.isBlank() || adminPassword.isBlank()) {
                                adminAuthError = "Please enter both admin email and password"
                                return@Button
                            }
                            isAuthenticatingAdmin = true
                            adminAuthError = null
                            viewModel.login(adminEmailOrPhone.trim(), adminPassword.trim()) {
                                isAuthenticatingAdmin = false
                                showAdminAuthDialog = false
                                onNavigateToAdmin()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                        enabled = !isAuthenticatingAdmin
                    ) {
                        Text("Verify & Open Admin", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAdminAuthDialog = false
                            adminAuthError = null
                        },
                        enabled = !isAuthenticatingAdmin
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Store Information Dialog
        if (showStoreInfoDialog) {
            AlertDialog(
                onDismissRequest = { showStoreInfoDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Storefront, contentDescription = null, tint = GroceryGreenPrimary)
                        Text(settings?.storeName ?: "Bankey Bihari Super Store", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "📍 Location:\nJai Vihar, Najafgarh, New Delhi, Delhi 110043, India",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "📞 Phone / WhatsApp: +91 9811223344",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "✉️ Email: support@bankeybihari.com",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "⏰ Timings: 7:00 AM – 10:00 PM (All 7 Days)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "🚚 Delivery Areas: Jai Vihar, Baprola, Najafgarh, Ranaji Enclave, Vikas Nagar & surrounding Delhi 110043 pockets.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showStoreInfoDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = GroceryTextPrimary
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
                    .background(GrocerySurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = if (textColor == GroceryDiscountBadge) GroceryDiscountBadge else GroceryGreenPrimary)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = textColor, fontSize = 14.sp)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
            }

            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = GroceryTextMuted)
        }
    }
}
