package com.wachin.chordstudio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    private var onTextLoaded: ((String) -> Unit)? = null

    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fontPreferencesManager: FontPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fontPreferencesManager = FontPreferencesManager(this)

        openDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val text = reader.readText()
                        onTextLoaded?.invoke(text)
                    }
                } catch (e: Exception) {
                    onTextLoaded?.invoke("Error al abrir archivo: ${e.message}")
                }
            }
        }

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChordStudioApp(
                        fontPreferencesManager = fontPreferencesManager,
                        onOpenFile = { callback ->
                            onTextLoaded = callback
                            openDocumentLauncher.launch(arrayOf("text/plain"))
                        },
                        onAddCustomFont = { uri ->
                            copyCustomFont(uri)
                        }
                    )
                }
            }
        }
    }

    private fun copyCustomFont(uri: Uri): String? {
        return try {
            val fontsDir = File(getExternalFilesDir(null), "fonts")
            if (!fontsDir.exists()) {
                fontsDir.mkdirs()
            }

            val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex("_display_name")
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "font_${System.currentTimeMillis()}.ttf"

            val destFile = File(fontsDir, fileName)
            
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun ChordStudioApp(
    fontPreferencesManager: FontPreferencesManager,
    onOpenFile: ((String) -> Unit) -> Unit,
    onAddCustomFont: (Uri) -> String?
) {
    var originalText by remember { mutableStateOf("") }
    var displayedText by remember { mutableStateOf("") }
    var semitoneOffset by remember { mutableStateOf(0) }
    var useSharps by remember { mutableStateOf(true) }
    var showFontSettings by remember { mutableStateOf(false) }
    
    // Font settings state
    var fontSettings by remember { mutableStateOf(FontSettings()) }
    var customFonts by remember { mutableStateOf<List<FontOption>>(emptyList()) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load font settings and custom fonts on startup
    LaunchedEffect(Unit) {
        fontPreferencesManager.fontSettings.collect { settings ->
            fontSettings = settings
        }
    }

    LaunchedEffect(Unit) {
        customFonts = loadCustomFonts(context)
    }

    fun reapplyTranspose() {
        displayedText = ChordStudio.transposeText(
            originalText,
            semitoneOffset,
            useSharps
        )
    }

    // Get the actual FontFamily to use
    val currentFontFamily = remember(fontSettings, customFonts) {
        val customFont = customFonts.find { it.name == fontSettings.fontFamilyName }
        if (customFont != null && customFont.filePath != null) {
            try {
                FontFamily(Font(File(customFont.filePath)))
            } catch (e: Exception) {
                FontFamily.Monospace
            }
        } else {
            when (fontSettings.fontFamilyName) {
                "Monospace" -> FontFamily.Monospace
                "Courier", "Courier New" -> FontFamily.Serif
                else -> FontFamily.Monospace
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header row with title and settings button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Transpositor de acordes",
                style = MaterialTheme.typography.headlineSmall
            )
            
            IconButton(onClick = { showFontSettings = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración de fuente"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onOpenFile { text ->
                        originalText = text
                        semitoneOffset = 0
                        displayedText = text
                    }
                }
            ) {
                Text("Abrir TXT")
            }

            Button(
                onClick = {
                    semitoneOffset -= 1
                    reapplyTranspose()
                },
                enabled = originalText.isNotEmpty()
            ) {
                Text("-1")
            }

            Button(
                onClick = {
                    semitoneOffset += 1
                    reapplyTranspose()
                },
                enabled = originalText.isNotEmpty()
            ) {
                Text("+1")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Semitonos: $semitoneOffset")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row {
                RadioButton(
                    selected = useSharps,
                    onClick = {
                        useSharps = true
                        if (originalText.isNotEmpty()) reapplyTranspose()
                    }
                )
                Text(
                    text = "Sostenidos (#)",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Row {
                RadioButton(
                    selected = !useSharps,
                    onClick = {
                        useSharps = false
                        if (originalText.isNotEmpty()) reapplyTranspose()
                    }
                )
                Text(
                    text = "Bemoles (b)",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                semitoneOffset = 0
                displayedText = originalText
            },
            enabled = originalText.isNotEmpty()
        ) {
            Text("Restaurar original")
        }

        Spacer(modifier = Modifier.height(12.dp))

        BasicTextField(
            value = displayedText,
            onValueChange = { newValue ->
                displayedText = newValue
                originalText = newValue
            },
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSettings.fontSize,
                fontFamily = currentFontFamily,
                fontWeight = fontSettings.fontWeight,
                fontStyle = fontSettings.fontStyle,
                lineHeight = (fontSettings.fontSize.value * 1.4).sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }

    // Font Settings Dialog
    if (showFontSettings) {
        FontSettingsDialog(
            currentSettings = fontSettings,
            customFonts = customFonts,
            onSettingsChange = { newSettings ->
                fontSettings = newSettings
                scope.launch {
                    fontPreferencesManager.saveFontSettings(newSettings)
                }
            },
            onAddCustomFont = { uri ->
                scope.launch {
                    val filePath = onAddCustomFont(uri)
                    if (filePath != null) {
                        customFonts = loadCustomFonts(context)
                    }
                }
            },
            onRemoveCustomFont = { fontOption ->
                scope.launch {
                    removeCustomFont(fontOption)
                    customFonts = loadCustomFonts(context)
                }
            },
            onDismiss = { showFontSettings = false }
        )
    }
}

/**
 * Load custom fonts from the app's fonts directory.
 */
private fun loadCustomFonts(context: Context): List<FontOption> {
    return try {
        val fontsDir = File(context.getExternalFilesDir(null), "fonts")
        if (!fontsDir.exists()) {
            emptyList()
        } else {
            fontsDir.listFiles()
                ?.filter { it.extension.lowercase() in listOf("ttf", "otf") }
                ?.map { file ->
                    FontOption(
                        name = file.nameWithoutExtension,
                        displayName = file.nameWithoutExtension,
                        isMonospaced = true,
                        isCustomFont = true,
                        filePath = file.absolutePath
                    )
                } ?: emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Remove a custom font file.
 */
private fun removeCustomFont(fontOption: FontOption) {
    try {
        fontOption.filePath?.let { path ->
            File(path).delete()
        }
    } catch (e: Exception) {
        // Ignore errors
    }
}
