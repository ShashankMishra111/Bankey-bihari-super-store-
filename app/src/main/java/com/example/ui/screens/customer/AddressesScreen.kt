package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.example.data.model.AddressEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addresses by viewModel.addresses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<AddressEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Delivery Addresses", fontWeight = FontWeight.Bold) },
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
                    editingAddress = null
                    showAddDialog = true
                },
                containerColor = GroceryGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_address_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Address")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (addresses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOff,
                        contentDescription = null,
                        tint = GroceryTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Addresses Saved", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Add your house or flat address in Jai Vihar for delivery",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GroceryTextSecondary)
                    )
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
                items(addresses) { address ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (address.addressType == "Home") Icons.Filled.Home else Icons.Filled.Work,
                                        contentDescription = null,
                                        tint = GroceryGreenPrimary
                                    )
                                    Text(text = "${address.name} (${address.addressType})", fontWeight = FontWeight.Bold)
                                    if (address.isDefault) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GroceryGreenContainer
                                        ) {
                                            Text(
                                                text = "DEFAULT",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GroceryGreenDark,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            editingAddress = address
                                            showAddDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = GroceryTextSecondary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAddress(address.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = GroceryDiscountBadge, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Phone: ${address.phone}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                            Text(
                                text = "${address.houseFlat}, ${address.street}, ${address.area}\n${address.landmark.let { if (it.isNotBlank()) "Near $it, " else "" }}${address.city}, ${address.state} - ${address.pinCode}",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp)
                            )

                            if (!address.isDefault) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { viewModel.setDefaultAddress(address.id) },
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    Text("Set as Default Delivery Address", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add/Edit Address Dialog
        if (showAddDialog) {
            AddressFormDialog(
                initialAddress = editingAddress,
                onDismiss = { showAddDialog = false },
                onSave = { newAddress ->
                    viewModel.saveAddress(newAddress) {
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun AddressFormDialog(
    initialAddress: AddressEntity?,
    onDismiss: () -> Unit,
    onSave: (AddressEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialAddress?.name ?: "") }
    var phone by remember { mutableStateOf(initialAddress?.phone ?: "") }
    var houseFlat by remember { mutableStateOf(initialAddress?.houseFlat ?: "") }
    var street by remember { mutableStateOf(initialAddress?.street ?: "") }
    var area by remember { mutableStateOf(initialAddress?.area ?: "Jai Vihar, Najafgarh") }
    var landmark by remember { mutableStateOf(initialAddress?.landmark ?: "") }
    var city by remember { mutableStateOf(initialAddress?.city ?: "New Delhi") }
    var state by remember { mutableStateOf(initialAddress?.state ?: "Delhi") }
    var pinCode by remember { mutableStateOf(initialAddress?.pinCode ?: "110043") }
    var addressType by remember { mutableStateOf(initialAddress?.addressType ?: "Home") }
    var isDefault by remember { mutableStateOf(initialAddress?.isDefault ?: false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialAddress == null) "Add Delivery Address" else "Edit Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = GroceryDiscountBadge, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Receiver Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("10-digit Mobile Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = houseFlat,
                    onValueChange = { houseFlat = it },
                    label = { Text("Flat / House No. / Building") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street / Road / Gali No.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark (e.g. Near Shiv Mandir)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area / Locality") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it },
                        label = { Text("Pincode") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Set as default address", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() || houseFlat.isBlank() || street.isBlank()) {
                        errorMessage = "Please fill in all required address fields"
                    } else {
                        val addr = AddressEntity(
                            id = initialAddress?.id ?: 0L,
                            userId = initialAddress?.userId ?: 0L,
                            name = name.trim(),
                            phone = phone.trim(),
                            houseFlat = houseFlat.trim(),
                            street = street.trim(),
                            area = area.trim(),
                            landmark = landmark.trim(),
                            city = city.trim(),
                            state = state.trim(),
                            pinCode = pinCode.trim(),
                            addressType = addressType,
                            isDefault = isDefault
                        )
                        onSave(addr)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
            ) {
                Text("Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
