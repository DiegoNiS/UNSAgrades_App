package com.example.unsagrades.ui.feature.semester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.ui.theme.UnsaPurple

@Composable
fun NewSemesterScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: NewSemesterViewModel = hiltViewModel()
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
        onSaveClick = viewModel::saveSemester
    )
}

@Composable
fun NewSemesterContent(
    name: String,
    isCurrent: Boolean,
    onNameChange: (String) -> Unit,
    onIsCurrentChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White // Fondo blanco limpio
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Título
            Text(
                text = "Nuevo Semestre",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Label
            Text(
                text = "Nombre del semestre:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Primer Semestre", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color(0xFFE0E0E0), // Gris suave
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedBorderColor = UnsaPurple,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = UnsaPurple
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Semestre actual:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Switch(
                    checked = isCurrent,
                    onCheckedChange = onIsCurrentChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Black, // Como en tu diseño (Negro/Blanco)
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

            // Botón Guardar
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnsaPurple // Morado UNSA
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Guardar Semestre",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewSemesterScreenPreview() {
    NewSemesterContent(
        name = "2024-I",
        isCurrent = true,
        onNameChange = {},
        onIsCurrentChange = {},
        onSaveClick = {}
    )
}
