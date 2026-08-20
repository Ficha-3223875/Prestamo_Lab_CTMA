package com.example.prestamo_lab_ctma.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.ui.viewmodel.FormularioSolicitudState

@OptIn(ExperimentalMaterial3Api::)
@Composable
fun SolicitarPrestamoScreen(
    equipo: Equipo?,
    formState: FormularioSolicitudState,
    guardando: Boolean,
    onAmbienteChange: (String) -> Unit,
    onPropositoChange: (String) -> Unit,
    onDuracionChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (equipo == null) {
                Text("Error: Equipo no encontrado.")
            } else {
                Text("Solicitando: ${equipo.nombre}", style = MaterialTheme.typography.titleLarge)
                
                OutlinedTextField(
                    value = formState.ambienteDestino,
                    onValueChange = onAmbienteChange,
                    label = { Text("Ambiente o destino") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorAmbiente != null,
                    supportingText = { formState.errorAmbiente?.let { Text(it) } }
                )
                
                OutlinedTextField(
                    value = formState.proposito,
                    onValueChange = onPropositoChange,
                    label = { Text("Propósito") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorProposito != null,
                    supportingText = { formState.errorProposito?.let { Text(it) } },
                    minLines = 3
                )
                
                OutlinedTextField(
                    value = formState.duracionHoras,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración en horas (1-8)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorDuracion != null,
                    supportingText = { formState.errorDuracion?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = onGuardar,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.puedeGuardar && !guardando
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar solicitud")
                    }
                }
            }
        }
    }
}
