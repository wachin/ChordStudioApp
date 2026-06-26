package com.wachin.chordstudio

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "font_settings")

/**
 * Manages font settings persistence using DataStore.
 */
class FontPreferencesManager(private val context: Context) {
    
    companion object {
        private val FONT_FAMILY_KEY = stringPreferencesKey("font_family")
        private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
        private val FONT_WEIGHT_KEY = stringPreferencesKey("font_weight")
        private val FONT_STYLE_KEY = stringPreferencesKey("font_style")
        private val IS_MONOSPACED_KEY = booleanPreferencesKey("is_monospaced")
    }
    
    /**
     * Flow of FontSettings that emits whenever settings change.
     */
    val fontSettings: Flow<FontSettings> = context.dataStore.data.map { preferences ->
        FontSettings(
            fontFamilyName = preferences[FONT_FAMILY_KEY] ?: FontSettings.DEFAULT_FONT_FAMILY,
            fontSize = (preferences[FONT_SIZE_KEY] ?: FontSettings.DEFAULT_FONT_SIZE.value).sp,
            fontWeight = parseFontWeight(preferences[FONT_WEIGHT_KEY]),
            fontStyle = parseFontStyle(preferences[FONT_STYLE_KEY]),
            isMonospaced = preferences[IS_MONOSPACED_KEY] ?: true
        )
    }
    
    /**
     * Save font settings to DataStore.
     */
    suspend fun saveFontSettings(settings: FontSettings) {
        context.dataStore.edit { preferences ->
            preferences[FONT_FAMILY_KEY] = settings.fontFamilyName
            preferences[FONT_SIZE_KEY] = settings.fontSize.value
            preferences[FONT_WEIGHT_KEY] = serializeFontWeight(settings.fontWeight)
            preferences[FONT_STYLE_KEY] = serializeFontStyle(settings.fontStyle)
            preferences[IS_MONOSPACED_KEY] = settings.isMonospaced
        }
    }
    
    private fun parseFontWeight(weightStr: String?): FontWeight {
        return when (weightStr) {
            "Bold" -> FontWeight.Bold
            "Light" -> FontWeight.Light
            "Medium" -> FontWeight.Medium
            "SemiBold" -> FontWeight.SemiBold
            "Thin" -> FontWeight.Thin
            else -> FontWeight.Normal
        }
    }
    
    private fun serializeFontWeight(weight: FontWeight): String {
        return when (weight) {
            FontWeight.Bold -> "Bold"
            FontWeight.Light -> "Light"
            FontWeight.Medium -> "Medium"
            FontWeight.SemiBold -> "SemiBold"
            FontWeight.Thin -> "Thin"
            else -> "Normal"
        }
    }
    
    private fun parseFontStyle(styleStr: String?): FontStyle {
        return when (styleStr) {
            "Italic" -> FontStyle.Italic
            else -> FontStyle.Normal
        }
    }
    
    private fun serializeFontStyle(style: FontStyle): String {
        return when (style) {
            FontStyle.Italic -> "Italic"
            else -> "Normal"
        }
    }
}
