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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCsvImportScreen(
    viewModel: GroceryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val csvText by viewModel.csvInputText.collectAsState()
    val validationResults by viewModel.csvValidationResults.collectAsState()
    val isImporting by viewModel.isImportingCsv.collectAsState()
    val importLogs by viewModel.csvImportLogs.collectAsState()

    val validCount = validationResults?.count { it.isValid } ?: 0
    val invalidCount = validationResults?.count { !it.isValid } ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CSV Bulk Product Importer", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instructions Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GroceryGreenContainer.copy(alpha = 0.4f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryGreenPrimary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = GroceryGreenDark)
                            Text("CSV Schema Format", fontWeight = FontWeight.Bold, color = GroceryGreenDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Required Columns in Order:\nSKU, Name, Brand, Category, Subcategory, SellingPrice, MRP, StockQuantity, Discount%, Description, Weight, Unit, ImageUrl",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // CSV Input & Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("CSV Data Content", fontWeight = FontWeight.Bold)
                        TextButton(onClick = { viewModel.loadSampleCsvTemplate() }) {
                            Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Load Sample CSV", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = csvText,
                        onValueChange = { viewModel.setCsvInputText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        placeholder = { Text("Paste CSV rows here...") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.validateCsv() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("validate_csv_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Checklist, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Validate CSV Data", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.executeCsvImport() },
                            enabled = validationResults != null && validCount > 0 && !isImporting,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("execute_import_button")
                        ) {
                            if (isImporting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute Import ($validCount)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Validation Results Preview
            if (validationResults != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GroceryOutline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Validation Results Preview", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GroceryGreenContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$validCount", fontWeight = FontWeight.Bold, color = GroceryGreenDark, fontSize = 18.sp)
                                        Text("Valid Rows", style = MaterialTheme.typography.labelSmall.copy(color = GroceryGreenDark))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (invalidCount > 0) Color(0xFFFEE2E2) else GrocerySurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$invalidCount", fontWeight = FontWeight.Bold, color = if (invalidCount > 0) GroceryDiscountBadge else GroceryTextSecondary, fontSize = 18.sp)
                                        Text("Invalid Rows", style = MaterialTheme.typography.labelSmall.copy(color = if (invalidCount > 0) GroceryDiscountBadge else GroceryTextSecondary))
                                    }
                                }
                            }
                        }
                    }
                }

                // Row-by-row validation breakdown
                items(validationResults!!) { row ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (row.isValid) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (row.isValid) GroceryGreenPrimary.copy(alpha = 0.4f) else GroceryDiscountBadge.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (row.isValid) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (row.isValid) GroceryGreenPrimary else GroceryDiscountBadge,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Row ${row.rowNumber}: ${row.name} (${row.sku})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (row.isValid) {
                                    Text(
                                        text = "Category: ${row.category} • Price: ₹${row.price?.toInt()} • MRP: ₹${row.mrp?.toInt()} • Stock: ${row.stock}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GroceryTextSecondary)
                                    )
                                } else {
                                    Text(
                                        text = row.errors.joinToString("; "),
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GroceryDiscountBadge, fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Past CSV Imports History
            item {
                Text("Past Import Logs", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            if (importLogs.isEmpty()) {
                item {
                    Text("No bulk imports logged yet.", color = GroceryTextSecondary, fontSize = 12.sp)
                }
            } else {
                items(importLogs) { log ->
                    val logDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(log.createdAt))
                    Card(
                        shape = RoundedCornerShape(10.dp),
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
                                Text(text = log.filename, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (log.status == "SUCCESS") GroceryGreenContainer else GroceryAmberContainer
                                ) {
                                    Text(
                                        text = log.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (log.status == "SUCCESS") GroceryGreenDark else GroceryOnAmberContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(text = logDate, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextMuted, fontSize = 10.sp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = log.summary, style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp))
                        }
                    }
                }
            }
        }
    }
}
