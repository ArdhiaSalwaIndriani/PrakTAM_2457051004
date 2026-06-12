package com.example.praktam_2457051004.ui.theme

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

private val FasilinkLightColors = lightColorScheme(
    primary            = PrimaryNavy,
    onPrimary          = Color.White,
    secondary          = SecondaryAmber,
    onSecondary        = Color.White,
    background         = AppBackground,
    onBackground       = TextPrimary,
    surface            = CardBackground,
    onSurface          = TextPrimary,
    error              = ErrorRed,
    onError            = Color.White
)

private val FasilinkDarkColors = darkColorScheme(
    primary            = PrimaryNavy,
    onPrimary          = Color.White,
    secondary          = SecondaryAmber,
    onSecondary        = Color.White,
    background         = Color(0xFF111827),
    onBackground       = Color(0xFFF9FAFB),
    surface            = Color(0xFF1F2937),
    onSurface          = Color(0xFFF9FAFB),
    error              = ErrorRed,
    onError            = Color.White
)

@Composable
fun PrakTAM_2457051004Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        }
        darkTheme -> FasilinkDarkColors
        else      -> FasilinkLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FasilinkTypography,
        content     = content
    )
}