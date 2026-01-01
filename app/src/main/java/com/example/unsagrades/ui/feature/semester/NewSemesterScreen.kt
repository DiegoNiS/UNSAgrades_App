package com.example.unsagrades.ui.feature.semester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.ui.theme.UNSAGradesTheme

//import com.example.unsagrades.ui.theme.UnsaPurple

@Composable
fun NewSemesterScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: NewSemesterViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val name by viewModel.semesterName.collectAsState()
    val isCurrent by viewModel.isCurrent.collectAsState()

    // Escuchar el evento de navegación cuando se guarde exitosamente
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateToDashboard()
        }
    }

    NewSemesterContent(
        name = name,
        isCurrent = isCurrent,
        onNameChange = viewModel::onNameChange,
        onIsCurrentChange = viewModel::onIsCurrentChange,
        onSaveClick = viewModel::saveSemester,
        innerPadding = innerPadding
    )
}

@Composable
fun NewSemesterContent(
    name: String,
    isCurrent: Boolean,
    onNameChange: (String) -> Unit,
    onIsCurrentChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    innerPadding : PaddingValues
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .imePadding() // <--- ¡ESTE ES EL TRUCO!
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "Crea un nuevo semestre",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Label
            Text(
                text = "Nombre del semestre:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Primer Semestre", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedBorderColor = Color.Transparent
                ),
                // al dar enter desplazar al siguiente input
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
//
//                // B. Le decimos: "Al dar Siguiente, mueve el foco hacia abajo"
//                keyboardActions = KeyboardActions(
//                    onDone = { // remover el teclado
//                    }
//                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Semestre actual:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Switch(
                    checked = isCurrent,
                    onCheckedChange = onIsCurrentChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                        checkedTrackColor = MaterialTheme.colorScheme.primary, // Como   en tu diseño (Negro/Blanco)
                        uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.tertiaryContainer // Como en tu diseño (Negro/Blanco)
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

            // Botón Guardar
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary // Morado UNSA
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Guardar Semestre", color = MaterialTheme.colorScheme.onSecondary)}

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewSemesterScreenPreview() {
    UNSAGradesTheme(dynamicColor = false) {
        NewSemesterContent(
            name = "2024-I",
            isCurrent = true,
            onNameChange = {},
            onIsCurrentChange = {},
            onSaveClick = {},
            innerPadding = PaddingValues(0.dp)
        )
    }
}
