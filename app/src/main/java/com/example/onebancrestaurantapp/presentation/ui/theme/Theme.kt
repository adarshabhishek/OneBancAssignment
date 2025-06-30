package com.example.onebancrestaurantapp.presentation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary =PrimaryPurple,
    secondary= AccentTeal,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = TextLight,
    onSecondary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)
@Composable
fun OneBancRestaurantAppTheme(
    content:@Composable () -> Unit
) {
    MaterialTheme(
        colorScheme= LightColorScheme,
        typography=Typography,
        content=content
    )
}
