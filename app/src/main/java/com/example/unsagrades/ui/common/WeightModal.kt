package com.example.unsagrades.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ModalDeIngreso(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    message: String,
    title: String,
    label: String,
    actualValue : Int,
) {
    // Estado interno del modal (lo que el usuario escribe)
    var newWeight by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() }, // Click fuera del modal
        title = { Text(text = title) },
        text = {
            Column {
                Text(message)
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onConfirm(newWeight)
                    }
                ),
                )
            }
        },
        // Botón de Confirmar (OK)
        confirmButton = {
            if (newWeight.isNotEmpty()) {
                if (newWeight.toInt() in 0..100) {
                    Button(
                        onClick = { onConfirm(newWeight) } // Enviamos el valor de regreso
                    ) {
                        Text("Cambiar Peso")
                    }
                }
            }
        },
        // Botón de Cancelar
        dismissButton = {
            if (newWeight.isNotEmpty()) {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Preview
@Composable
fun ModalDeIngresoPreview() {
    ModalDeIngreso(
        onDismiss = {},
        onConfirm = {},
        message = "Ingresa el nuevo peso",
        title = "Cambiar Peso",
        actualValue = 20,
        label = "Nuevo Peso"
    )
}