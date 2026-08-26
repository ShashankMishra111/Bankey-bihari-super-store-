package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GroceryGreenLight,
    onPrimary = GroceryGreenDark,
    primaryContainer = GroceryGreenDark,
    onPrimaryContainer = GroceryGreenContainer,
    secondary = GroceryOfferYellow,
    onSecondary = GroceryOnAmberContainer,
    secondaryContainer = GroceryAmberSecondary,
    onSecondaryContainer = GroceryAmberContainer,
    background = Color(0xFF111827),
    surface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFF374151),
    outline = Color(0xFF4B5563)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GroceryGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GroceryGreenContainer,
    onPrimaryContainer = GroceryOnGreenContainer,
    secondary = GroceryAmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = GroceryAmberContainer,
    onSecondaryContainer = GroceryOnAmberContainer,
    background = GroceryBackground,
    surface = GrocerySurface,
    surfaceVariant = GrocerySurfaceVariant,
    onBackground = GroceryTextPrimary,
    onSurface = GroceryTextPrimary,
    outline = GroceryOutline,
    outlineVariant = GroceryOutlineStrong,
    error = GroceryDiscountBadge
  )

@Composable
fun GroceryTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  GroceryTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
