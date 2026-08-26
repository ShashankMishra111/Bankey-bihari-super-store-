package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.admin.*
import com.example.ui.screens.customer.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Categories : Screen("categories", "Categories", Icons.Filled.GridView, Icons.Outlined.GridView)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Cart : Screen("cart", "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Orders : Screen("orders", "Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    object Profile : Screen("profile", "Account", Icons.Filled.Person, Icons.Outlined.Person)
    object Admin : Screen("admin_dashboard", "Admin", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryTheme {
                GroceryApp()
            }
        }
    }
}

@Composable
fun GroceryApp() {
    val navController = rememberNavController()
    val viewModel: GroceryViewModel = viewModel()

    val currentUser by viewModel.currentUser.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = remember(currentUser) {
        if (currentUser?.role == "ADMIN") {
            listOf(
                Screen.Home,
                Screen.Categories,
                Screen.Search,
                Screen.Cart,
                Screen.Orders,
                Screen.Admin
            )
        } else {
            listOf(
                Screen.Home,
                Screen.Categories,
                Screen.Search,
                Screen.Cart,
                Screen.Orders,
                Screen.Profile
            )
        }
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Categories.route,
        Screen.Search.route,
        Screen.Cart.route,
        Screen.Orders.route,
        Screen.Profile.route,
        Screen.Admin.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    bottomNavScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                if (screen == Screen.Cart && cartSummary.totalItemCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = GroceryGreenPrimary,
                                                contentColor = Color.White
                                            ) {
                                                Text("${cartSummary.totalItemCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) screen.icon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) screen.icon else screen.unselectedIcon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GroceryGreenDark,
                                selectedTextColor = GroceryGreenDark,
                                indicatorColor = GroceryGreenPrimary.copy(alpha = 0.15f)
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Customer Routes
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSearch = { query ->
                        if (query.isNullOrBlank()) {
                            navController.navigate("search")
                        } else {
                            navController.navigate("search?q=$query")
                        }
                    },
                    onNavigateToCategories = { navController.navigate("categories") },
                    onNavigateToProductDetail = { id -> navController.navigate("product_detail/$id") },
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToAdmin = { navController.navigate("admin_dashboard") }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onCategorySelect = { category ->
                        navController.navigate("search?cat=$category")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "search?q={q}&cat={cat}",
                arguments = listOf(
                    navArgument("q") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("cat") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val q = backStackEntry.arguments?.getString("q")
                val cat = backStackEntry.arguments?.getString("cat")
                SearchScreen(
                    viewModel = viewModel,
                    initialQuery = q,
                    initialCategory = cat,
                    onNavigateToProductDetail = { id -> navController.navigate("product_detail/$id") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("search") {
                SearchScreen(
                    viewModel = viewModel,
                    initialQuery = null,
                    initialCategory = null,
                    onNavigateToProductDetail = { id -> navController.navigate("product_detail/$id") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToProductDetail = { id -> navController.navigate("product_detail/$id") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToCheckout = { navController.navigate("checkout") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) },
                    onNavigateToProductDetail = { id -> navController.navigate("product_detail/$id") }
                )
            }

            composable("checkout") {
                CheckoutScreen(
                    viewModel = viewModel,
                    onOrderPlaced = { orderId ->
                        navController.navigate("order_detail/$orderId") {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    },
                    onNavigateToAddAddress = { navController.navigate("addresses") },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Orders.route) {
                OrdersScreen(
                    viewModel = viewModel,
                    onNavigateToOrderDetail = { orderId -> navController.navigate("order_detail/$orderId") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                )
            }

            composable(
                route = "order_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("addresses") {
                AddressesScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAddresses = { navController.navigate("addresses") },
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                    onNavigateToAdmin = { navController.navigate("admin_dashboard") },
                    onNavigateToAuth = { navController.navigate("auth") }
                )
            }

            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Admin Routes (Role-Guarded)
            composable("admin_dashboard") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToProducts = { navController.navigate("admin_products") },
                        onNavigateToInventory = { navController.navigate("admin_inventory") },
                        onNavigateToOrders = { navController.navigate("admin_orders") },
                        onNavigateToCsvImport = { navController.navigate("admin_csv_import") },
                        onNavigateToAnalytics = { navController.navigate("admin_analytics") },
                        onNavigateToCustomers = { navController.navigate("admin_customers") },
                        onNavigateToSettings = { navController.navigate("admin_settings") },
                        onSwitchToCustomerView = { navController.navigate(Screen.Home.route) }
                    )
                }
            }

            composable("admin_products") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminProductsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_inventory") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminInventoryScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_orders") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminOrdersScreen(
                        viewModel = viewModel,
                        onNavigateToOrderDetail = { orderId -> navController.navigate("order_detail/$orderId") },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_csv_import") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminCsvImportScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_analytics") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminAnalyticsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_customers") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminCustomersScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("admin_settings") {
                AdminGuard(
                    currentUserRole = currentUser?.role,
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToHome = { navController.navigate(Screen.Home.route) }
                ) {
                    AdminSettingsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGuard(
    currentUserRole: String?,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    content: @Composable () -> Unit
) {
    if (currentUserRole == "ADMIN") {
        content()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Access Restricted", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToHome) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GroceryOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GroceryAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = GroceryOnAmberContainer,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Store Admin Portal Only",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "You are currently signed in as a Customer. To access inventory, order management, CSV bulk upload, and store analytics, please sign in with your Store Admin credentials.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GroceryTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNavigateToAuth,
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In as Store Admin", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onNavigateToHome,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("Return to Grocery Store")
                        }
                    }
                }
            }
        }
    }
}
