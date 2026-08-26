package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

enum class AuthMode {
    SIGN_IN_OTP,
    SIGN_IN_PASSWORD,
    SIGN_IN_ADMIN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: GroceryViewModel,
    onAuthSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN_OTP) }

    // Registration Form State
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }

    // Sign In State
    var loginPhone by remember { mutableStateOf("9811223344") }
    var loginEmailOrPhone by remember { mutableStateOf("sundeepmishra3330@gmail.com") }
    var loginPassword by remember { mutableStateOf("customer123") }
    var isLoginPasswordVisible by remember { mutableStateOf(false) }

    // Admin Mode State
    var adminEmail by remember { mutableStateOf("admin@bankeybihari.com") }
    var adminPassword by remember { mutableStateOf("admin123") }
    var adminPin by remember { mutableStateOf("") }

    // OTP Verification State
    var isOtpVerificationActive by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var verificationTarget by remember { mutableStateOf("") }
    var isVerifyingForRegistration by remember { mutableStateOf(false) }

    // ViewModel State Observers
    val otpBanner by viewModel.otpBanner.collectAsState()
    val otpCode by viewModel.otpCode.collectAsState()
    val otpTimerSeconds by viewModel.otpTimerSeconds.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isOtpVerificationActive) "Verification"
                        else when (authMode) {
                            AuthMode.REGISTER -> "Create Account"
                            AuthMode.SIGN_IN_OTP -> "Sign In with OTP"
                            AuthMode.SIGN_IN_PASSWORD -> "Sign In"
                            AuthMode.SIGN_IN_ADMIN -> "Staff & Admin Portal"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isOtpVerificationActive) {
                                isOtpVerificationActive = false
                                enteredOtp = ""
                            } else {
                                onBackClick()
                            }
                        }
                    ) {
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
                .verticalScroll(rememberScrollState())
        ) {
            // Live OTP Simulation Banner
            AnimatedVisibility(
                visible = otpBanner != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sms,
                                contentDescription = null,
                                tint = GroceryOnAmberContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "SMS Verification Service",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GroceryOnAmberContainer
                                )
                                Text(
                                    text = otpBanner ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GroceryOnAmberContainer
                                    )
                                )
                            }
                        }

                        // Auto-fill button in banner
                        if (otpCode != null) {
                            Button(
                                onClick = {
                                    enteredOtp = otpCode ?: ""
                                    viewModel.showMessage("OTP code auto-filled")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Auto-Fill", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        IconButton(
                            onClick = { viewModel.dismissOtpBanner() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Dismiss", tint = GroceryOnAmberContainer, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Header
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.linearGradient(
                                if (authMode == AuthMode.SIGN_IN_ADMIN) listOf(GroceryOfferYellow, GroceryAmberContainer)
                                else listOf(GroceryGreenPrimary, GroceryGreenLight)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (authMode == AuthMode.SIGN_IN_ADMIN) Icons.Filled.AdminPanelSettings else Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        tint = if (authMode == AuthMode.SIGN_IN_ADMIN) GroceryOnAmberContainer else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bankey Bihari Super Store",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GroceryTextPrimary
                    )
                )
                Text(
                    text = "Jai Vihar, Najafgarh, New Delhi",
                    style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isOtpVerificationActive) {
                    // OTP Verification Card
                    OtpVerificationSection(
                        target = verificationTarget,
                        enteredOtp = enteredOtp,
                        onOtpChange = { if (it.length <= 6) enteredOtp = it },
                        otpTimerSeconds = otpTimerSeconds,
                        isLoading = isAuthLoading,
                        onResendOtp = {
                            viewModel.resendOtp(verificationTarget)
                        },
                        onVerify = {
                            if (enteredOtp.length != 6) {
                                viewModel.showMessage("Please enter complete 6-digit OTP")
                                return@OtpVerificationSection
                            }

                            if (isVerifyingForRegistration) {
                                viewModel.verifyAndCompleteSignup(
                                    name = regName,
                                    email = regEmail,
                                    phone = regPhone,
                                    pass = regPassword,
                                    enteredOtp = enteredOtp,
                                    onSuccess = onAuthSuccess
                                )
                            } else {
                                viewModel.verifyAndCompletePhoneLogin(
                                    phoneOrEmail = verificationTarget,
                                    enteredOtp = enteredOtp,
                                    onSuccess = onAuthSuccess
                                )
                            }
                        },
                        onEditTarget = {
                            isOtpVerificationActive = false
                            enteredOtp = ""
                        }
                    )
                } else {
                    // Auth Tabs
                    TabRow(
                        selectedTabIndex = if (authMode == AuthMode.REGISTER) 1 else 0,
                        containerColor = GrocerySurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = authMode != AuthMode.REGISTER,
                            onClick = {
                                if (authMode == AuthMode.REGISTER) authMode = AuthMode.SIGN_IN_OTP
                            },
                            text = { Text("Sign In", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = authMode == AuthMode.REGISTER,
                            onClick = { authMode = AuthMode.REGISTER },
                            text = { Text("Create Account", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    when (authMode) {
                        AuthMode.REGISTER -> {
                            RegisterSection(
                                name = regName,
                                onNameChange = { regName = it },
                                email = regEmail,
                                onEmailChange = { regEmail = it },
                                phone = regPhone,
                                onPhoneChange = { regPhone = it },
                                password = regPassword,
                                onPasswordChange = { regPassword = it },
                                isPasswordVisible = isRegPasswordVisible,
                                onTogglePasswordVisibility = { isRegPasswordVisible = !isRegPasswordVisible },
                                isLoading = isAuthLoading,
                                onSubmit = {
                                    // Validations
                                    if (regName.trim().length < 2) {
                                        viewModel.showMessage("Please enter a valid full name")
                                        return@RegisterSection
                                    }
                                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmail.trim()).matches()) {
                                        viewModel.showMessage("Please enter a valid email address")
                                        return@RegisterSection
                                    }
                                    val cleanPhone = regPhone.trim().replace("+91", "").replace(" ", "").replace("-", "")
                                    if (cleanPhone.length != 10 || !cleanPhone.all { it.isDigit() }) {
                                        viewModel.showMessage("Please enter a valid 10-digit mobile number")
                                        return@RegisterSection
                                    }
                                    if (regPassword.length < 6) {
                                        viewModel.showMessage("Password must be at least 6 characters long")
                                        return@RegisterSection
                                    }

                                    isVerifyingForRegistration = true
                                    verificationTarget = cleanPhone
                                    viewModel.requestSignupVerification(
                                        name = regName.trim(),
                                        email = regEmail.trim(),
                                        phone = cleanPhone,
                                        pass = regPassword,
                                        onOtpSent = {
                                            isOtpVerificationActive = true
                                            enteredOtp = ""
                                        }
                                    )
                                }
                            )
                        }

                        AuthMode.SIGN_IN_OTP -> {
                            SignInOtpSection(
                                phone = loginPhone,
                                onPhoneChange = { loginPhone = it },
                                isLoading = isAuthLoading,
                                onRequestOtp = {
                                    val cleanPhone = loginPhone.trim().replace("+91", "").replace(" ", "").replace("-", "")
                                    if (cleanPhone.length != 10 || !cleanPhone.all { it.isDigit() }) {
                                        viewModel.showMessage("Please enter a valid 10-digit Indian mobile number")
                                        return@SignInOtpSection
                                    }
                                    isVerifyingForRegistration = false
                                    verificationTarget = cleanPhone
                                    viewModel.requestLoginOtp(cleanPhone) {
                                        isOtpVerificationActive = true
                                        enteredOtp = ""
                                    }
                                },
                                onSwitchToPassword = { authMode = AuthMode.SIGN_IN_PASSWORD },
                                onSwitchToAdmin = { authMode = AuthMode.SIGN_IN_ADMIN }
                            )
                        }

                        AuthMode.SIGN_IN_PASSWORD -> {
                            SignInPasswordSection(
                                emailOrPhone = loginEmailOrPhone,
                                onEmailOrPhoneChange = { loginEmailOrPhone = it },
                                password = loginPassword,
                                onPasswordChange = { loginPassword = it },
                                isPasswordVisible = isLoginPasswordVisible,
                                onTogglePasswordVisibility = { isLoginPasswordVisible = !isLoginPasswordVisible },
                                isLoading = isAuthLoading,
                                onSignIn = {
                                    if (loginEmailOrPhone.isBlank() || loginPassword.isBlank()) {
                                        viewModel.showMessage("Please enter email/phone and password")
                                        return@SignInPasswordSection
                                    }
                                    viewModel.login(loginEmailOrPhone.trim(), loginPassword.trim(), onSuccess = onAuthSuccess)
                                },
                                onSwitchToOtp = { authMode = AuthMode.SIGN_IN_OTP },
                                onSwitchToAdmin = { authMode = AuthMode.SIGN_IN_ADMIN }
                            )
                        }

                        AuthMode.SIGN_IN_ADMIN -> {
                            SignInAdminSection(
                                email = adminEmail,
                                onEmailChange = { adminEmail = it },
                                password = adminPassword,
                                onPasswordChange = { adminPassword = it },
                                adminPin = adminPin,
                                onPinChange = { adminPin = it },
                                isLoading = isAuthLoading,
                                onAdminLogin = {
                                    if (adminEmail.isBlank() || adminPassword.isBlank()) {
                                        viewModel.showMessage("Please enter admin email and password")
                                        return@SignInAdminSection
                                    }
                                    viewModel.login(adminEmail.trim(), adminPassword.trim(), onSuccess = onAuthSuccess)
                                },
                                onSwitchToCustomer = { authMode = AuthMode.SIGN_IN_OTP }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// OTP VERIFICATION COMPOSABLE
// -------------------------------------------------------------
@Composable
fun OtpVerificationSection(
    target: String,
    enteredOtp: String,
    onOtpChange: (String) -> Unit,
    otpTimerSeconds: Int,
    isLoading: Boolean,
    onResendOtp: () -> Unit,
    onVerify: () -> Unit,
    onEditTarget: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GroceryOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GroceryGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = GroceryGreenDark,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Enter 6-Digit OTP",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "We sent a verification code to +91 $target. Enter the code below to verify your account.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GroceryTextSecondary,
                    textAlign = TextAlign.Center
                )
            )

            // OTP Digits Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val char = enteredOtp.getOrNull(i)?.toString() ?: ""
                    val isFocused = enteredOtp.length == i
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (char.isNotEmpty()) GroceryGreenContainer
                                else if (isFocused) GrocerySurfaceVariant
                                else GrocerySurfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) GroceryGreenPrimary else if (char.isNotEmpty()) GroceryGreenDark else GroceryOutline,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (char.isNotEmpty()) GroceryGreenDark else GroceryTextPrimary
                            )
                        )
                    }
                }
            }

            // Hidden or Simple Text Field for Input
            OutlinedTextField(
                value = enteredOtp,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(6)
                    onOtpChange(filtered)
                },
                label = { Text("Type 6-Digit Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onVerify() }),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input_field")
            )

            // Resend and Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEditTarget) {
                    Text("Change Number", fontSize = 12.sp, color = GroceryTextSecondary)
                }

                if (otpTimerSeconds > 0) {
                    Text(
                        text = "Resend OTP in ${otpTimerSeconds}s",
                        style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontWeight = FontWeight.Medium)
                    )
                } else {
                    TextButton(onClick = onResendOtp) {
                        Text("Resend OTP", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GroceryGreenPrimary)
                    }
                }
            }

            Button(
                onClick = onVerify,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                enabled = !isLoading && enteredOtp.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verify_otp_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Verify & Continue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SIGN UP FORM
// -------------------------------------------------------------
@Composable
fun RegisterSection(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            placeholder = { Text("e.g. Sundeep Mishra") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            placeholder = { Text("e.g. sundeep@example.com") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }.take(10)
                onPhoneChange(digitsOnly)
            },
            label = { Text("10-Digit Mobile Number") },
            placeholder = { Text("9811223344") },
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Create Password") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Password Strength Indicator
        if (password.isNotEmpty()) {
            val strength = calculatePasswordStrength(password)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Password Strength:", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GroceryTextSecondary))
                    Text(
                        text = strength.label,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = strength.color)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { strength.progress },
                    color = strength.color,
                    trackColor = GrocerySurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onSubmit,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("register_send_otp_button")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(imageVector = Icons.Filled.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verify Mobile & Sign Up", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Text(
            text = "By signing up, you agree to receive order notifications & delivery updates for Jai Vihar, Najafgarh.",
            style = MaterialTheme.typography.bodySmall.copy(color = GroceryTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------
// FAST SIGN IN VIA OTP
// -------------------------------------------------------------
@Composable
fun SignInOtpSection(
    phone: String,
    onPhoneChange: (String) -> Unit,
    isLoading: Boolean,
    onRequestOtp: () -> Unit,
    onSwitchToPassword: () -> Unit,
    onSwitchToAdmin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GroceryGreenContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.FlashOn, contentDescription = null, tint = GroceryGreenDark)
                Text(
                    text = "Fast 1-Tap OTP Sign In. No password needed.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = GroceryGreenDark)
                )
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }.take(10)
                onPhoneChange(digitsOnly)
            },
            label = { Text("10-Digit Mobile Number") },
            placeholder = { Text("9811223344") },
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onRequestOtp,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("signin_request_otp_button")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Get Verification Code", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onSwitchToPassword) {
                Text("Sign In with Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onSwitchToAdmin) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = GroceryOfferYellow)
                    Text("Store Staff Login", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GroceryOfferYellow)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SIGN IN VIA PASSWORD
// -------------------------------------------------------------
@Composable
fun SignInPasswordSection(
    emailOrPhone: String,
    onEmailOrPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    onSignIn: () -> Unit,
    onSwitchToOtp: () -> Unit,
    onSwitchToAdmin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = onEmailOrPhoneChange,
            label = { Text("Email or 10-Digit Mobile") },
            leadingIcon = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSignIn,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_login_button")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Sign In as Customer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onSwitchToOtp) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text("Fast OTP Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onSwitchToAdmin) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = GroceryOfferYellow)
                    Text("Store Staff Login", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GroceryOfferYellow)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STORE STAFF & ADMIN SIGN IN
// -------------------------------------------------------------
@Composable
fun SignInAdminSection(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    adminPin: String,
    onPinChange: (String) -> Unit,
    isLoading: Boolean,
    onAdminLogin: () -> Unit,
    onSwitchToCustomer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, GroceryOfferYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = GroceryOnAmberContainer)
                Text(
                    text = "Bankey Bihari Management Portal. Restricted to store staff.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = GroceryOnAmberContainer)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Admin Email / Staff ID") },
            leadingIcon = { Icon(imageVector = Icons.Filled.AdminPanelSettings, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Admin Master Password") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onAdminLogin,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryOfferYellow),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verify & Open Admin Portal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            }
        }

        TextButton(
            onClick = onSwitchToCustomer,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to Customer Sign In", fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// HELPER: Password Strength Calculator
// -------------------------------------------------------------
data class PasswordStrength(
    val label: String,
    val progress: Float,
    val color: Color
)

fun calculatePasswordStrength(pass: String): PasswordStrength {
    var score = 0
    if (pass.length >= 6) score++
    if (pass.length >= 8) score++
    if (pass.any { it.isDigit() }) score++
    if (pass.any { it.isUpperCase() }) score++
    if (pass.any { !it.isLetterOrDigit() }) score++

    return when {
        score <= 1 -> PasswordStrength("Weak", 0.25f, GroceryDiscountBadge)
        score in 2..3 -> PasswordStrength("Fair", 0.60f, GroceryOfferYellow)
        score == 4 -> PasswordStrength("Strong", 0.85f, GroceryGreenPrimary)
        else -> PasswordStrength("Excellent", 1.0f, GroceryGreenDark)
    }
}
