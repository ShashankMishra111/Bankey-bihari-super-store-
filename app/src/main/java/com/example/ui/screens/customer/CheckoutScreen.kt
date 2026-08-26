package com.example.ui.screens.customer

import androidx.compose.animation.*
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
import com.example.data.model.AddressEntity
import com.example.data.model.DeliverySlotEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: GroceryViewModel,
    onOrderPlaced: (String) -> Unit,
    onNavigateToAddAddress: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addresses by viewModel.addresses.collectAsState()
    val deliverySlots by viewModel.deliverySlots.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()

    var currentStep by remember { mutableStateOf(1) }
    var selectedAddress by remember { mutableStateOf<AddressEntity?>(null) }
    var selectedSlot by remember { mutableStateOf<DeliverySlotEntity?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var orderNotes by remember { mutableStateOf("") }

    LaunchedEffect(addresses) {
        if (selectedAddress == null && addresses.isNotEmpty()) {
            selectedAddress = addresses.firstOrNull { it.isDefault } ?: addresses.first()
        }
    }

    LaunchedEffect(deliverySlots) {
        if (selectedSlot == null && deliverySlots.isNotEmpty()) {
            selectedSlot = deliverySlots.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout (Step $currentStep of 4)",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep -= 1
                        else onBackClick()
                    }) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Grand Total", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                        Text(
                            text = "₹${(cartSummary.grandTotal + (selectedSlot?.fee ?: 0.0)).toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GroceryTextPrimary,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> {
                                    if (selectedAddress != null) currentStep = 2
                                    else viewModel.showMessage("Please select or add a delivery address")
                                }
                                2 -> {
                                    if (selectedSlot != null) currentStep = 3
                                    else viewModel.showMessage("Please select a delivery slot")
                                }
                                3 -> {
                                    currentStep = 4
                                }
                                4 -> {
                                    if (selectedAddress != null && selectedSlot != null) {
                                        viewModel.placeOrder(
                                            address = selectedAddress!!,
                                            slot = selectedSlot!!,
                                            paymentMethod = selectedPaymentMethod,
                                            notes = orderNotes,
                                            onSuccess = { orderId ->
                                                onOrderPlaced(orderId)
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        enabled = !isPlacingOrder,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("checkout_action_button")
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (currentStep == 4) "Place Order" else "Continue",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (currentStep == 4) Icons.Filled.CheckCircle else Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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
            // Step Progress Indicator
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CheckoutStepIndicator(step = 1, title = "Address", activeStep = currentStep)
                    StepDivider(isDone = currentStep > 1)
                    CheckoutStepIndicator(step = 2, title = "Slot", activeStep = currentStep)
                    StepDivider(isDone = currentStep > 2)
                    CheckoutStepIndicator(step = 3, title = "Payment", activeStep = currentStep)
                    StepDivider(isDone = currentStep > 3)
                    CheckoutStepIndicator(step = 4, title = "Review", activeStep = currentStep)
                }
            }

            // STEP 1: Address Selection
            if (currentStep == 1) {
                item {
                    Text(
                        text = "Select Delivery Address in Jai Vihar / Delhi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (addresses.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GrocerySurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No saved addresses found", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToAddAddress,
                                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                                ) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add New Address")
                                }
                            }
                        }
                    }
                } else {
                    items(addresses) { addr ->
                        val isSelected = selectedAddress?.id == addr.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GroceryGreenContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) GroceryGreenPrimary else GroceryOutline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAddress = addr }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAddress = addr }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = addr.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (addr.isDefault) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GroceryGreenPrimary
                                            ) {
                                                Text(
                                                    text = "DEFAULT",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(text = "Phone: ${addr.phone}", style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${addr.houseFlat}, ${addr.street}, ${addr.area}\n${addr.landmark.let { if (it.isNotBlank()) "Near $it, " else "" }}${addr.city}, ${addr.state} - ${addr.pinCode}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextPrimary)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onNavigateToAddAddress,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GroceryGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = GroceryGreenPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Another Address", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // STEP 2: Delivery Slot Selection
            if (currentStep == 2) {
                item {
                    Text(
                        text = "Choose Delivery Time Slot",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(deliverySlots) { slot ->
                    val isSelected = selectedSlot?.id == slot.id
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) GroceryGreenContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) GroceryGreenPrimary else GroceryOutline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlot = slot }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedSlot = slot }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = slot.slotName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = slot.timeWindow,
                                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                                )
                            }
                            if (slot.fee > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GroceryAmberContainer
                                ) {
                                    Text(
                                        text = "+₹${slot.fee.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GroceryOnAmberContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GroceryGreenContainer
                                ) {
                                    Text(
                                        text = "FREE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GroceryGreenDark,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 3: Payment Method Selection
            if (currentStep == 3) {
                item {
                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                val paymentOptions = listOf(
                    Triple("Cash on Delivery", "Pay cash or QR on doorstep in Jai Vihar", Icons.Filled.Money),
                    Triple("UPI", "Pay via Google Pay, PhonePe, Paytm, BHIM", Icons.Filled.QrCode2),
                    Triple("Credit / Debit Card", "Visa, Mastercard, RuPay Cards", Icons.Filled.CreditCard)
                )

                items(paymentOptions) { (title, subtitle, icon) ->
                    val isSelected = selectedPaymentMethod == title
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) GroceryGreenContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) GroceryGreenPrimary else GroceryOutline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentMethod = title }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPaymentMethod = title }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GrocerySurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = GroceryGreenPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary))
                            }
                        }
                    }
                }
            }

            // STEP 4: Review & Final Confirmation
            if (currentStep == 4) {
                item {
                    Text(
                        text = "Review Order Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Delivery summary card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = GroceryGreenPrimary)
                                Text("Delivering To", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            selectedAddress?.let { addr ->
                                Text(
                                    text = "${addr.name} (${addr.phone})\n${addr.houseFlat}, ${addr.street}, ${addr.area}, ${addr.city} - ${addr.pinCode}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Schedule, contentDescription = null, tint = GroceryGreenPrimary)
                                Text("Delivery Slot: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${selectedSlot?.slotName} (${selectedSlot?.timeWindow})", fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Payment, contentDescription = null, tint = GroceryGreenPrimary)
                                Text("Payment: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(selectedPaymentMethod, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Order items list
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.product.name} (${item.product.weight})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₹${(item.product.sellingPrice * item.quantity).toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Delivery Instructions Note
                item {
                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = { orderNotes = it },
                        label = { Text("Delivery Instructions (Optional)") },
                        placeholder = { Text("e.g. Ring bell, leave near gate in Jai Vihar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutStepIndicator(step: Int, title: String, activeStep: Int) {
    val isDone = activeStep > step
    val isCurrent = activeStep == step

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isDone || isCurrent) GroceryGreenPrimary else GrocerySurfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$step",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isCurrent) Color.White else GroceryTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) GroceryGreenPrimary else GroceryTextSecondary
            )
        )
    }
}

@Composable
fun StepDivider(isDone: Boolean) {
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(2.dp)
            .background(if (isDone) GroceryGreenPrimary else GroceryOutline)
    )
}
