package com.ctma.prestamolab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    secondary = VerdeSecundario,
    background = FondoClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeSecundario,
    secondary = AzulPrimario
)

@Composable
fun PrestamoLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colores = if (darkTheme) EsquemaOscuro else EsquemaClaro
    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        content = content
    )
}
