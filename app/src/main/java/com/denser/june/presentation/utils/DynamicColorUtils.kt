package com.denser.june.presentation.utils

import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DynamicThemeColors(
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color
)

@Composable
fun rememberDynamicThemeColors(
    model: Any?
): DynamicThemeColors {
    val context = LocalContext.current

    val defaultColors = DynamicThemeColors(
        surface = MaterialTheme.colorScheme.surface,
        onSurface = MaterialTheme.colorScheme.onSurface,
        onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
        primaryContainer = MaterialTheme.colorScheme.primaryContainer,
        onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer,
        secondaryContainer = MaterialTheme.colorScheme.secondaryContainer,
        onSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer
    )

    var themeColors by remember { mutableStateOf(defaultColors) }

    LaunchedEffect(model) {
        if (model == null) return@LaunchedEffect

        withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(model)
                .allowHardware(false)
                .build()

            val bitmap = when (val result = loader.execute(request)) {
                is SuccessResult -> (result.drawable as? BitmapDrawable)?.bitmap
                else -> null
            }

            if (bitmap != null) {
                val palette = Palette.from(bitmap).generate()

                val dominantSwatch = palette.dominantSwatch
                val vibrantSwatch = palette.vibrantSwatch
                    ?: palette.lightVibrantSwatch
                    ?: palette.darkVibrantSwatch
                    ?: dominantSwatch

                val bgHsl = floatArrayOf(0f, 0f, 0f)
                if (dominantSwatch != null) {
                    ColorUtils.colorToHSL(dominantSwatch.rgb, bgHsl)
                } else {
                    ColorUtils.colorToHSL(android.graphics.Color.DKGRAY, bgHsl)
                }
                bgHsl[1] = (bgHsl[1] * 0.4f).coerceAtMost(0.4f)
                bgHsl[2] = 0.12f
                val surfaceColor = Color(ColorUtils.HSLToColor(bgHsl))

                val accentHsl = floatArrayOf(0f, 0f, 0f)
                if (vibrantSwatch != null) {
                    ColorUtils.colorToHSL(vibrantSwatch.rgb, accentHsl)
                } else {
                    ColorUtils.colorToHSL(android.graphics.Color.LTGRAY, accentHsl)
                }

                accentHsl[1] = accentHsl[1].coerceAtLeast(0.85f)
                accentHsl[2] = 0.65f
                val primaryContainerColor = Color(ColorUtils.HSLToColor(accentHsl))

                val onAccentHsl = accentHsl.copyOf()
                onAccentHsl[1] = (onAccentHsl[1] * 0.8f).coerceAtMost(0.5f)
                onAccentHsl[2] = 0.15f
                val onPrimaryContainerColor = Color(ColorUtils.HSLToColor(onAccentHsl))

                themeColors = DynamicThemeColors(
                    surface = surfaceColor,
                    onSurface = Color.White,
                    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
                    primaryContainer = primaryContainerColor,
                    onPrimaryContainer = onPrimaryContainerColor,
                    secondaryContainer = Color.White.copy(alpha = 0.15f),
                    onSecondaryContainer = Color.White
                )
            }
        }
    }

    return themeColors
}