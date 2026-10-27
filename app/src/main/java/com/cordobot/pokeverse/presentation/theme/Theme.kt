package com.cordobot.pokeverse.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PokeRed = Color(0xFFE3350D)
val Night = Color(0xFF0B0F19)
val Surface = Color(0xFF151B2B)
val SurfaceHigh = Color(0xFF1E2638)

private val colors = darkColorScheme(
    primary = PokeRed,
    onPrimary = Color.White,
    secondary = Color(0xFFFFCB05),
    background = Night,
    onBackground = Color.White,
    surface = Surface,
    onSurface = Color.White,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = Color(0xFFB4BCD0)
)

private val typography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
)

@Composable
fun PokeVerseTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, content = content)
}
