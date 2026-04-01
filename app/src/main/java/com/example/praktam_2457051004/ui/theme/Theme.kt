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
    primary            = Red80,
    onPrimary          = OnRed,
    primaryContainer   = RedLight,
    onPrimaryContainer = Red80,

    secondary          = Pink80,
    onSecondary        = OnPink,
    secondaryContainer = Pink40,
    onSecondaryContainer = OnPink,

    background         = SoftWhite,
    onBackground       = OnSoft,
    surface            = PureWhite,
    onSurface          = OnSurface,
    surfaceVariant     = SurfaceVar,
    onSurfaceVariant   = Red60,

    error              = Color(0xFFB00020),
    onError            = OnRed,
)

private val FasilinkDarkColors = darkColorScheme(
    primary            = Red40,
    onPrimary          = Red80,
    primaryContainer   = Red60,
    onPrimaryContainer = SoftWhite,

    secondary          = Pink40,
    onSecondary        = OnPink,
    secondaryContainer = Pink80,
    onSecondaryContainer = OnPink,

    background         = Color(0xFF1A0000),
    onBackground       = SoftWhite,
    surface            = Color(0xFF2A0000),
    onSurface          = RedLight,
    surfaceVariant     = Color(0xFF3B0000),
    onSurfaceVariant   = RedLight,

    error              = Color(0xFFCF6679),
    onError            = Color(0xFF1C0007),
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