package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class LuminaPaletteExtras(
    val isOledDark: Boolean,
    val glassSurface: Color,
    val cardBorder: Color,
    val subtleSurface: Color,
    val mutedInk: Color,
    val brandGradient: Brush,
    val storyRingGradient: Brush,
    val wordmarkGradient: Brush
)

val LocalLuminaExtras = staticCompositionLocalOf {
    LuminaPaletteExtras(
        isOledDark = false,
        glassSurface = LightSurface.copy(alpha = 0.88f),
        cardBorder = LightBorder.copy(alpha = 0.75f),
        subtleSurface = LightSurfaceVariant,
        mutedInk = LightTextMuted,
        brandGradient = Brush.linearGradient(listOf(BrandRose500, BrandPink500, BrandAmber400)),
        storyRingGradient = Brush.linearGradient(
            listOf(StoryOrange, StoryCoral, StoryCrimson, StoryMagenta, StoryViolet)
        ),
        wordmarkGradient = Brush.linearGradient(listOf(LightTextPrimary, BrandRose600, BrandAmber500))
    )
}

private val LuminaLightColorScheme = lightColorScheme(
    primary = BrandRose500,
    onPrimary = Color.White,
    primaryContainer = BrandRose100,
    onPrimaryContainer = BrandRose700,
    secondary = BrandAmber500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = BrandPink500,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White
)

private val LuminaOledDarkColorScheme = darkColorScheme(
    primary = BrandRose400,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3F121D),
    onPrimaryContainer = BrandRose100,
    secondary = BrandAmber400,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B2806),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = BrandPink500,
    onTertiary = Color.White,
    background = OledBlack,
    onBackground = DarkTextPrimary,
    surface = OledSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = OledSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = OledBorder,
    outlineVariant = Color(0xFF333333),
    error = Color(0xFFF87171),
    onError = Color.Black
)

val LuminaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun LuminaTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LuminaOledDarkColorScheme else LuminaLightColorScheme
    val extras = LuminaPaletteExtras(
        isOledDark = darkTheme,
        glassSurface = if (darkTheme) OledBlack.copy(alpha = 0.86f) else LightSurface.copy(alpha = 0.88f),
        cardBorder = if (darkTheme) OledBorder else LightBorder.copy(alpha = 0.75f),
        subtleSurface = if (darkTheme) OledSurfaceVariant else LightSurfaceVariant,
        mutedInk = if (darkTheme) DarkTextMuted else LightTextMuted,
        brandGradient = Brush.linearGradient(listOf(BrandRose500, BrandPink500, BrandAmber400)),
        storyRingGradient = Brush.linearGradient(
            listOf(StoryOrange, StoryCoral, StoryCrimson, StoryMagenta, StoryViolet)
        ),
        wordmarkGradient = if (darkTheme) {
            Brush.linearGradient(listOf(Color.White, BrandRose400, BrandAmber400))
        } else {
            Brush.linearGradient(listOf(LightTextPrimary, BrandRose600, BrandAmber500))
        }
    )

    CompositionLocalProvider(LocalLuminaExtras provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LuminaTypography,
            shapes = LuminaShapes,
            content = content
        )
    }
}
