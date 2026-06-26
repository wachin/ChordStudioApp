package com.wachin.chordstudio

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Data class representing font settings for the chord transposer text display.
 */
data class FontSettings(
    val fontFamilyName: String = DEFAULT_FONT_FAMILY,
    val fontSize: TextUnit = DEFAULT_FONT_SIZE,
    val fontWeight: FontWeight = FontWeight.Normal,
    val fontStyle: FontStyle = FontStyle.Normal,
    val isMonospaced: Boolean = true
) {
    companion object {
        const val DEFAULT_FONT_FAMILY = "Monospace"
        val DEFAULT_FONT_SIZE = 18.sp
        val MIN_FONT_SIZE = 10.sp
        val MAX_FONT_SIZE = 36.sp
        
        // Built-in monospaced font options
        val BUILT_IN_MONOSPACED_FONTS = listOf(
            "Monospace",
            "Courier",
            "Courier New"
        )
    }
    
    /**
     * Get the FontFamily object based on the font family name.
     */
    fun getFontFamily(): FontFamily {
        return when (fontFamilyName) {
            "Monospace" -> FontFamily.Monospace
            "Courier", "Courier New" -> FontFamily.Serif // Fallback to serif for courier-like fonts
            else -> FontFamily.Monospace
        }
    }
    
    /**
     * Check if the font weight is bold.
     */
    fun isBold(): Boolean = fontWeight == FontWeight.Bold
    
    /**
     * Check if the font style is italic.
     */
    fun isItalic(): Boolean = fontStyle == FontStyle.Italic
}

/**
 * Represents a font option that can be selected by the user.
 */
data class FontOption(
    val name: String,
    val displayName: String,
    val isMonospaced: Boolean,
    val isCustomFont: Boolean = false,
    val filePath: String? = null
)
