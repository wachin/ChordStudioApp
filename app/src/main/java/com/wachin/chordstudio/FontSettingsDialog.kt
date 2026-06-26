package com.wachin.chordstudio

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.File
import java.io.FileOutputStream

/**
 * Dialog for configuring font settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsDialog(
    currentSettings: FontSettings,
    customFonts: List<FontOption>,
    onSettingsChange: (FontSettings) -> Unit,
    onAddCustomFont: (Uri) -> Unit,
    onRemoveCustomFont: (FontOption) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFontName by remember { mutableStateOf(currentSettings.fontFamilyName) }
    var selectedFontSize by remember { mutableStateOf(currentSettings.fontSize.value) }
    var isBold by remember { mutableStateOf(currentSettings.isBold()) }
    var isItalic by remember { mutableStateOf(currentSettings.isItalic()) }
    
    val context = LocalContext.current
    val fontPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onAddCustomFont(it) }
    }
    
    // All available fonts: built-in + custom
    val allFonts = remember(customFonts) {
        FontSettings.BUILT_IN_MONOSPACED_FONTS.map { name ->
            FontOption(
                name = name,
                displayName = name,
                isMonospaced = true,
                isCustomFont = false
            )
        } + customFonts
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Configuración de Fuente",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Font Family Selection
                Text(
                    text = "Tipo de Fuente",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    allFonts.forEach { fontOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedFontName == fontOption.name,
                                    onClick = { selectedFontName = fontOption.name }
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFontName == fontOption.name,
                                onClick = { selectedFontName = fontOption.name }
                            )
                            Text(
                                text = fontOption.displayName,
                                modifier = Modifier.padding(start = 8.dp),
                                style = TextStyle(
                                    fontFamily = getFontFamilyForPreview(fontOption, context)
                                )
                            )
                            if (fontOption.isCustomFont) {
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { onRemoveCustomFont(fontOption) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar fuente",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add Custom Font Button
                OutlinedButton(
                    onClick = { fontPickerLauncher.launch(arrayOf("font/ttf", "font/otf", "application/x-font-ttf", "application/x-font-otf", "*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar fuente personalizada (.ttf/.otf)")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Font Size Slider
                Text(
                    text = "Tamaño: ${selectedFontSize.toInt()}sp",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Slider(
                    value = selectedFontSize,
                    onValueChange = { selectedFontSize = it },
                    valueRange = FontSettings.MIN_FONT_SIZE.value..FontSettings.MAX_FONT_SIZE.value,
                    steps = 25,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bold and Italic Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isBold,
                            onCheckedChange = { isBold = it }
                        )
                        Text(
                            text = "Negrita",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isItalic,
                            onCheckedChange = { isItalic = it }
                        )
                        Text(
                            text = "Cursiva",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Preview
                Text(
                    text = "Vista previa:",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    val previewFontFamily = remember(selectedFontName, allFonts, context) {
                        val fontOption = allFonts.find { it.name == selectedFontName }
                        getFontFamilyForPreview(fontOption ?: allFonts.first(), context)
                    }
                    
                    BasicText(
                        text = "Am      G       C       F\nLetra de ejemplo con acordes",
                        modifier = Modifier.padding(8.dp),
                        style = TextStyle(
                            fontFamily = previewFontFamily,
                            fontSize = selectedFontSize.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSettingsChange(
                                FontSettings(
                                    fontFamilyName = selectedFontName,
                                    fontSize = selectedFontSize.sp,
                                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                    isMonospaced = true
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Aplicar")
                    }
                }
            }
        }
    }
}

/**
 * Get FontFamily for preview in the dialog.
 */
private fun getFontFamilyForPreview(fontOption: FontOption, context: Context): FontFamily {
    return if (fontOption.isCustomFont && fontOption.filePath != null) {
        try {
            FontFamily(Font(File(fontOption.filePath)))
        } catch (e: Exception) {
            FontFamily.Monospace
        }
    } else {
        when (fontOption.name) {
            "Monospace" -> FontFamily.Monospace
            "Courier", "Courier New" -> FontFamily.Serif
            else -> FontFamily.Monospace
        }
    }
}
