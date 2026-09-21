package ru.myitschool.work.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class WorkColors(
    val background: Color,
    val sheet: Color,
    val card: Color,
    val onCard: Color,
    val cardHint: Color,
    val field: Color,
    val onField: Color,
    val chipBar: Color,
    val chipSelected: Color,
    val onChipSelected: Color,
    val chipText: Color,
    val accent: Color,
    val onAccent: Color,
    val alertBackground: Color,
    val onAlert: Color,
    val alertButton: Color,
    val onAlertButton: Color,
    val text: Color,
    val textMuted: Color,
    val error: Color,
)

val LightWorkColors = WorkColors(
    background = Color(0xFF65558F),
    sheet = Color(0xFF625B71),
    card = Color(0xFFFFFFFF),
    onCard = Color(0xFF4F378B),
    cardHint = Color(0xFF625B71),
    field = Color(0xFF4F378B),
    onField = Color(0xFFFFFFFF),
    chipBar = Color(0xFFEADDFF),
    chipSelected = Color(0xFF4F378B),
    onChipSelected = Color(0xFFFFFFFF),
    chipText = Color(0xFF625B71),
    accent = Color(0xFF4F378B),
    onAccent = Color(0xFFFFFFFF),
    alertBackground = Color(0xFFEADDFF),
    onAlert = Color(0xFF000000),
    alertButton = Color(0xFF4A4458),
    onAlertButton = Color(0xFFFFFFFF),
    text = Color(0xFFFFFFFF),
    textMuted = Color(0x66FFFFFF),
    error = Color(0xFFFFB4AB),
)

val DarkWorkColors = WorkColors(
    background = Color(0xFF141218),
    sheet = Color(0xFF211F26),
    card = Color(0xFF4F378B),
    onCard = Color(0xFFEADDFF),
    cardHint = Color(0xFFCCC2DC),
    field = Color(0xFF2B2930),
    onField = Color(0xFFE6E0E9),
    chipBar = Color(0xFF2B2930),
    chipSelected = Color(0xFFD0BCFF),
    onChipSelected = Color(0xFF381E72),
    chipText = Color(0xFFCCC2DC),
    accent = Color(0xFFD0BCFF),
    onAccent = Color(0xFF381E72),
    alertBackground = Color(0xFF2B2930),
    onAlert = Color(0xFFE6E0E9),
    alertButton = Color(0xFFD0BCFF),
    onAlertButton = Color(0xFF381E72),
    text = Color(0xFFE6E0E9),
    textMuted = Color(0x66E6E0E9),
    error = Color(0xFFFFB4AB),
)

val LocalWorkColors = staticCompositionLocalOf { LightWorkColors }
