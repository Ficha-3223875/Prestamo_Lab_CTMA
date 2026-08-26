package com.ctma.prestamolab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tamaños base pensados para admitir escalado de fuente del sistema
// (criterio de accesibilidad 10.5 / TC-18), usando sp en lugar de dp.
val Typography = Typography(
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp)
)
