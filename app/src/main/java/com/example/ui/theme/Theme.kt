package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AetherColorScheme = darkColorScheme(
    primary = PlasmaCyan,
    onPrimary = SpaceBlack,
    primaryContainer = PlasmaCyanDim,
    onPrimaryContainer = TextPrimary,
    secondary = QuantumViolet,
    onSecondary = SpaceBlack,
    secondaryContainer = QuantumVioletDim,
    onSecondaryContainer = TextPrimary,
    tertiary = FusionAmber,
    onTertiary = SpaceBlack,
    tertiaryContainer = StellarGold,
    onTertiaryContainer = SpaceBlack,
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = PanelDark,
    onSurfaceVariant = TextSecondary,
    outline = PanelBorder,
    error = ScramRed,
    onError = SpaceBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Enforce our custom sci-fi palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AetherColorScheme,
        typography = Typography,
        content = content
    )
}
