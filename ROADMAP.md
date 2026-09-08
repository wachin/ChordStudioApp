# ROADMAP.md

Documento de continuidad para retomar `ChordStudioApp` después de formatear el equipo.

Fecha de referencia: 27 de julio de 2026.

> **Actualización (8 de septiembre de 2026):** el entorno fue restaurado después del formateo y el
> proyecto vuelve a compilar (`BUILD SUCCESSFUL`) con Gradle 9.5, AGP 9.3.2, Kotlin 2.2.10
> (built-in Kotlin de AGP 9), Compose BOM 2026.06.01 y `compileSdk = 36`.
> La guía completa de instalación quedó en `README.md`. La **Fase 5** ya está implementada
> (septiembre de 2026); lo único pendiente es la **verificación en celular real**
> (checklist y criterios de aceptación de abajo).

> ### ⏳ Nota sobre versiones (importante si el proyecto queda inactivo unos meses)
>
> Las versiones mencionadas arriba (SDK 36, Gradle 9.5, AGP 9.3.2, Kotlin 2.2.10,
> Compose BOM 2026.06.01) son las verificadas en septiembre de 2026. **Android actualiza
> estas versiones constantemente**: cada año salen nuevas plataformas (compileSdk/targetSdk),
> nuevas versiones de AGP, de Kotlin, del Compose BOM y de las librerías androidx.
>
> Si retomas el proyecto tiempo después y algo falla o aparece desactualizado, no es un error
> del proyecto: simplemente hay que subir las versiones. Dónde mirar:
>
> - **Android Studio** avisa de actualizaciones y ofrece el **AGP Upgrade Assistant**
>   (menú `Tools → AGP Upgrade Assistant`) para migrar el proyecto automáticamente.
> - El **Compose BOM vigente** se consulta en:
>   <https://developer.android.com/develop/ui/compose/bom/bom-mapping>
> - Las versiones del proyecto están **centralizadas en `gradle/libs.versions.toml`**:
>   actualizar ahí (no en los archivos build) y ajustar `compileSdk` en `app/build.gradle.kts`
>   si la plataforma cambia.
> - Después de actualizar, verificar siempre con:
>   `./gradlew assembleDebug` y `./gradlew testDebugUnitTest`.
>
> Lo mismo aplica al `README.md`: sus versiones son una foto del momento en que se escribió;
> el paso a paso sigue siendo válido aunque los números cambien.

## Objetivo inmediato

La app ya funciona como transpositor de acordes, pero la UI todavía necesita mejoras importantes para usarse bien en celular.

Lo prioritario es:

1. [x] mejorar el scroll del contenido
2. [x] agregar un modo editor que sólo se active con un botón
3. [x] evitar que el teclado aparezca mientras no se esté editando
4. [x] liberar espacio vertical, porque los controles actuales ocupan demasiado

## Estado actual del proyecto

La pantalla principal está implementada en:

- [app/src/main/java/com/wachin/chordstudio/MainActivity.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/MainActivity.kt:1)

La lógica actual relevante es:

- `ChordStudioApp(...)` contiene toda la UI principal
- `displayedText` es el texto visible
- `originalText` se usa como base para restaurar y retransponer
- el área de texto es un `BasicTextField`
- el `BasicTextField` está siempre editable
- al estar siempre editable, el teclado puede aparecer cuando no conviene
- los controles están apilados arriba del editor y consumen bastante alto de pantalla

También existe el diálogo de fuentes en:

- [app/src/main/java/com/wachin/chordstudio/FontSettingsDialog.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/FontSettingsDialog.kt:1)

La lógica de transposición está en:

- [app/src/main/java/com/wachin/chordstudio/ChordStudio.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/ChordStudio.kt:1)

## Problemas actuales detectados

### 1. El editor ocupa demasiado protagonismo funcional

Hoy el texto siempre está en `BasicTextField`, así que la app se comporta como editor aunque el objetivo principal normalmente es leer y transponer.

Consecuencia:

- el teclado puede aparecer cuando no se desea
- la UX de lectura se siente más pesada

### 2. El scroll está resuelto de forma básica

Actualmente el `BasicTextField` tiene `verticalScroll(scrollState)`.

Eso sirve, pero no es la mejor base para dos modos distintos:

- modo lectura
- modo edición

Además, conviene separar mejor el scroll del panel de controles y el scroll del contenido.

### 3. Los controles superiores consumen demasiado espacio

Actualmente se muestran al mismo tiempo:

- título
- botón de configuración
- botón de abrir TXT
- botones `-1` y `+1`
- texto de semitonos
- radio buttons para sostenidos/bemoles
- botón de restaurar original

En una pantalla pequeña eso reduce demasiado el área útil del texto.

## Dirección recomendada

La pantalla principal debería dividirse en dos conceptos claros:

### Modo lectura

Estado por defecto.

Comportamiento:

- el contenido se muestra sin foco de edición
- no aparece el teclado
- el usuario puede hacer scroll libremente
- los controles ocupan el mínimo espacio posible

### Modo edición

Se activa únicamente con un botón explícito.

Comportamiento:

- el área de texto pasa a ser editable
- se solicita foco sólo en ese momento
- recién ahí puede aparecer el teclado
- al salir de edición, se limpia foco y se oculta teclado

## Plan técnico recomendado

## Fase 1: separar lectura y edición

- [x] **Implementada** en `MainActivity.kt` (lectura y edición usan representaciones separadas)

### Meta

Evitar que el teclado aparezca mientras el usuario sólo está leyendo o transponiendo.

### Cambios propuestos

Agregar un estado nuevo en `ChordStudioApp(...)`:

```kotlin
var isEditMode by remember { mutableStateOf(false) }
```

Usar dos representaciones distintas del contenido:

- si `isEditMode == false`, mostrar el texto con `Text` o `BasicText`
- si `isEditMode == true`, mostrar `BasicTextField`

### Archivos a tocar

- [app/src/main/java/com/wachin/chordstudio/MainActivity.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/MainActivity.kt:1)

### Resultado esperado

- en lectura: sin teclado
- en edición: editable de verdad

## Fase 2: controlar foco y teclado correctamente

- [x] **Implementada** (`FocusRequester`, `LocalFocusManager`, `LocalSoftwareKeyboardController`)

### Meta

Que el teclado sólo aparezca cuando el usuario pulsa “Editar”.

### Implementación sugerida

En Compose, usar:

- `FocusRequester`
- `LocalFocusManager`
- `LocalSoftwareKeyboardController`

Flujo sugerido:

1. El usuario pulsa `Editar`
2. `isEditMode = true`
3. se solicita foco al `BasicTextField`
4. se muestra teclado

Para salir:

1. el usuario pulsa `Listo`, `Guardar`, o `Cerrar edición`
2. `isEditMode = false`
3. limpiar foco
4. ocultar teclado

### Nota importante

No conviene dejar el `BasicTextField` montado permanentemente con `readOnly = true` como única solución, porque eso suele dejar comportamiento ambiguo de foco y selección. Es mejor separar claramente la vista de lectura de la de edición.

## Fase 3: mejorar el scroll

- [x] **Implementada** (el scroll vive en el contenido, no en el layout completo)

### Meta

Que el texto se pueda recorrer cómodamente en pantalla pequeña, sin mezclar el scroll del contenido con el de los controles.

### Recomendación

Mantener una estructura parecida a:

- contenedor general
- barra compacta de acciones arriba
- área de contenido ocupando el resto de la pantalla

El scroll debe vivir principalmente en el contenido, no en todo el layout completo.

### Implementación recomendada

Opción simple y suficiente para este proyecto:

- controles arriba con alto mínimo
- contenido en un `Box` o `Surface` con `Modifier.weight(1f)`
- dentro del contenido:
  - `Text`/`BasicText` con `verticalScroll(...)` en modo lectura
  - `BasicTextField` con `verticalScroll(...)` en modo edición

### Qué evitar

- que todo el `Column` principal haga scroll
- que los controles se desplacen junto con el texto principal

## Fase 4: compactar la barra de controles

- [x] **Implementada**:
  - [x] barra compacta de controles
  - [x] espaciados reducidos
  - [x] título reducido
  - [x] selector compacto de `# / b` (en lugar de radio buttons)

### Meta

Recuperar espacio vertical real en el celular.

### Cambios recomendados

#### 1. Convertir los controles en una barra compacta

En vez de varios bloques verticales separados, usar una fila o dos filas compactas.

Ejemplo de organización:

- fila 1:
  - `Abrir`
  - `Editar`
  - `-1`
  - `+1`
  - `0`
  - `Ajustes`

- fila 2:
  - selector breve de `# / b`
  - texto pequeño de `Semitonos: N`

#### 2. Reducir espaciados

Hoy hay varios `Spacer(height = 12.dp)` y `padding(16.dp)`.

Revisar especialmente:

- `padding(16.dp)` del contenedor principal
- `Spacer(height = 12.dp)`
- `Spacer(height = 8.dp)`

Posible ajuste:

- padding general a `8.dp` o `10.dp`
- spacers más pequeños

#### 3. Reemplazar texto grande del encabezado

El título `Transpositor de acordes` ocupa espacio útil.

Opciones:

- quitar el título de la pantalla principal
- o reducirlo a una línea más pequeña
- o moverlo a `TopAppBar` compacta

#### 4. Reemplazar radio buttons grandes

Los `RadioButton` para sostenidos/bemoles consumen más alto del necesario.

Opciones mejores:

- `SegmentedButton`
- dos `TextButton` compactos
- un `SingleChoiceSegmentedButtonRow`

## Fase 5: proteger la lógica entre texto original y texto editado

- [x] **Implementada** (septiembre de 2026). Estado separado en `ChordStudioApp`:
  - [x] `loadedText` — el archivo cargado originalmente (inmutable, para "Restaurar original")
  - [x] `sourceText` — la base actual para la transposición (se actualiza con ediciones manuales)
  - [x] `displayedText` — el texto visible/renderizado
  - [x] `hasManualEdits` — se activa al editar; muestra el botón "Restaurar original"
  - Nota: la verificación en el celular sigue pendiente

### Problema actual

Hoy, en `onValueChange`, se hace:

```kotlin
displayedText = newValue
originalText = newValue
```

Eso simplifica la edición, pero mezcla dos responsabilidades:

- texto fuente cargado o editado
- texto transpuesto visible

### Riesgo

Cuando se mejore el modo editor, esta mezcla puede volver más confuso:

- restaurar original
- volver a transponer
- editar manualmente y luego transponer

### Recomendación

Evaluar separar mejor:

- `sourceText`
- `displayedText`
- `isDirty` o `hasManualEdits`

No es obligatorio hacerlo primero, pero sí conviene dejarlo previsto.

## Propuesta concreta de implementación

Orden recomendado de trabajo:

1. [x] agregar `isEditMode`
2. [x] separar vista lectura y vista edición
3. [x] controlar foco/teclado
4. [x] compactar controles
5. [x] mover el contenido a un área con `weight(1f)`
6. [x] revisar luego la separación entre texto fuente y texto mostrado (Fase 5)

## Boceto de UX recomendado

### Estado normal

- arriba una barra compacta
- abajo el texto ocupando casi toda la pantalla
- scroll activo
- teclado oculto

### Al pulsar Editar

- entra en modo edición
- aparece el teclado
- aparece un botón `Listo` o `Cerrar edición`

### Al salir de edición

- se oculta teclado
- vuelve a vista lectura
- se mantiene el texto actualizado

## Checklist de continuación

Cuando retomes el proyecto después del formateo:

1. [x] clonar o abrir el repo
2. [x] revisar este archivo
3. [x] abrir `MainActivity.kt`
4. [x] localizar `ChordStudioApp(...)`
5. [x] revisar la implementación existente de `isEditMode`
6. [ ] probar en celular real
7. [ ] verificar que el teclado no aparezca fuera de modo edición
8. [ ] verificar que el scroll siga funcionando bien
9. [x] continuar con la separación entre texto fuente y texto mostrado (Fase 5)

## Criterios de aceptación

El trabajo se puede considerar bien resuelto cuando (ya está hecho en código; marcar cada punto al verificarlo en el celular):

- [ ] el teclado no aparece al abrir la app
- [ ] el texto se puede leer y desplazar cómodamente
- [ ] la edición sólo ocurre tras pulsar un botón
- [ ] al salir de edición, el teclado desaparece
- [ ] los controles ocupan menos espacio vertical que ahora
- [ ] la zona de texto visible en celular es claramente mayor

## Estado de la implementación

- [x] Las fases 1 a 4 ya están implementadas en `MainActivity.kt`:
  - lectura y edición usan representaciones separadas
  - el foco y el teclado se controlan al entrar y salir de edición
  - el scroll está limitado al área de contenido
  - la barra superior y el selector de alteraciones son compactos
- [x] La Fase 5 (separar `loadedText` / `sourceText` / `displayedText` / `hasManualEdits`)
  está implementada, con botón "Restaurar original" visible cuando hay ediciones manuales

La compilación local debe ejecutarse en un equipo con Android SDK API 36 (`compileSdk = 36`).
Consulta `README.md` y `local.properties.example` para configurar el entorno.

El entorno ya fue restaurado y verificado tras el formateo (8 de septiembre de 2026):
`./gradlew assembleDebug` y `./gradlew testDebugUnitTest` terminan en `BUILD SUCCESSFUL`.
Lo que sigue es probar la app en el celular (`adb install -r app/build/outputs/apk/debug/app-debug.apk`),
marcar los criterios de aceptación y luego afrontar la Fase 5.

## Archivos más probables a modificar

- [app/src/main/java/com/wachin/chordstudio/MainActivity.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/MainActivity.kt:1)
- opcionalmente:
  - [app/src/main/java/com/wachin/chordstudio/FontSettingsDialog.kt](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/java/com/wachin/chordstudio/FontSettingsDialog.kt:1)
  - [app/src/main/res/values/strings.xml](/home/wachin/AndroidStudioProjects/ChordStudioApp/app/src/main/res/values/strings.xml:1)

## Nota final importante

Antes de formatear, conviene dejar subidos al repositorio también:

- [x] este `ROADMAP.md`
- [x] cualquier cambio pendiente en `README.md`
- [x] cualquier cambio pendiente en `docs/`

Si algo queda sin commit y sin push, se va a perder con el formateo.

(Esto ya se cumplió: tras el formateo el repositorio se clonó de nuevo y estos archivos están en Git.)
