package com.argentspirit.quickvault.utility

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ColorPalette(
    val baseColor: Color,
    val gradientStartColor: Color,
    val gradientEndColor: Color,
    val textColor: Color
)

object BaseColorGenerator {

    private val colorPalettes = listOf(
        ColorPalette(Color(0xFFE57373), Color(0xFFFFCDD2), Color(0xFFEF9A9A), Color(0xFFFFFFFF)), // Light Red
        ColorPalette(Color(0xFF81C784), Color(0xFFC8E6C9), Color(0xFFA5D6A7), Color(0xFF000000)), // Light Green
        ColorPalette(Color(0xFF64B5F6), Color(0xFFBBDEFB), Color(0xFF90CAF9), Color(0xFFFFFFFF)), // Light Blue
        ColorPalette(Color(0xFFFFB74D), Color(0xFFFFE0B2), Color(0xFFFFCC80), Color(0xFF000000)), // Light Orange
        ColorPalette(Color(0xFFBA68C8), Color(0xFFE1BEE7), Color(0xFFCE93D8), Color(0xFFFFFFFF)), // Light Purple
        ColorPalette(Color(0xFF4DB6AC), Color(0xFFB2DFDB), Color(0xFF80CBC4), Color(0xFF000000)), // Light Teal
        ColorPalette(Color(0xFFFFF176), Color(0xFFFFF9C4), Color(0xFFFFECB3), Color(0xFF000000)), // Light Yellow
        ColorPalette(Color(0xFFA1887F), Color(0xFFD7CCC8), Color(0xFFBCAAA4), Color(0xFFFFFFFF)), // Light Brown
        ColorPalette(Color(0xFF90A4AE), Color(0xFFCFD8DC), Color(0xFFB0BEC5), Color(0xFF000000)), // Light Blue Grey
        ColorPalette(Color(0xFFFF8A65), Color(0xFFFFCCBC), Color(0xFFFFAB91), Color(0xFFFFFFFF)), // Light Deep Orange
        ColorPalette(Color(0xFF7986CB), Color(0xFFC5CAE9), Color(0xFF9FA8DA), Color(0xFFFFFFFF)), // Light Indigo
        ColorPalette(Color(0xFF4DD0E1), Color(0xFFB2EBF2), Color(0xFF80DEEA), Color(0xFF000000)), // Light Cyan
        ColorPalette(Color(0xFFAED581), Color(0xFFDCEDC8), Color(0xFFC5E1A5), Color(0xFF000000)), // Light Lime
        ColorPalette(Color(0xFFFFD54F), Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFF000000)), // Light Amber
        ColorPalette(Color(0xFFF06292), Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFFFFFFF))  // Light Pink
    )

    private var lastSelectedIndex = -1

    fun getRandomColorPalette(): ColorPalette {
        if (colorPalettes.isEmpty()) {
            // Fallback in case the list is somehow empty, though it shouldn't be.
            return ColorPalette(Color.Gray, Color.LightGray, Color.DarkGray, Color.Black)
        }
        if (colorPalettes.size == 1) {
            return colorPalettes.first()
        }

        var randomIndex: Int
        do {
            randomIndex = Random.nextInt(colorPalettes.size)
        } while (randomIndex == lastSelectedIndex)

        lastSelectedIndex = randomIndex
        return colorPalettes[randomIndex]
    }

    fun getColorPaletteFromString(input: String): ColorPalette {
        if (colorPalettes.isEmpty()) {
            // Fallback in case the list is somehow empty, though it shouldn't be.
            return ColorPalette(Color.Gray, Color.LightGray, Color.DarkGray, Color.Black)
        }

        // Generate a hash code from the string and use it to select a palette.
        // This ensures that the same string will always produce the same color palette.
        val hashCode = input.hashCode()
        val index = (hashCode % colorPalettes.size + colorPalettes.size) % colorPalettes.size // Ensure positive index

        lastSelectedIndex = index // Update lastSelectedIndex to prevent immediate reuse by getRandomColorPalette if called next
        return colorPalettes[index]
    }
}