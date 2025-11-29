package com.example.unsagrades.ui.feature.createcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.ui.theme.UnsaPurple

// --- COLORES ESPECÍFICOS DE ESTA PANTALLA ---
val MintGreenCard = Color(0xFFD6EADD) // El verde menta de tu diseño
val InputBackground = Color(0xFFFFFFFF)

@Composable
fun AddCourseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel()
) {
    val name by viewModel.courseName.collectAsState()
    val partials by viewModel.partialConfigs.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showWeightWarningDialog by viewModel.showWeightWarningDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Manejo de eventos de navegación
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AddCourseViewModel.NavigationEvent.NavigateBack -> onNavigateBack()
                is AddCourseViewModel.NavigationEvent.NavigateToDetail -> onNavigateToDetail(event.courseId)
            }
        }
    }

    // Manejo de errores (Snackbar)
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    AddCourseContent(
        name = name,
        partials = partials,
        snackbarHostState = snackbarHostState,
        showWeightWarningDialog = showWeightWarningDialog,
        onNameChange = viewModel::onNameChange,
        onWeightChange = viewModel::onWeightChange,
        onSave = { viewModel.saveCourse(goToDetail = false) },
        onSaveAndDetail = { viewModel.saveCourse(goToDetail = true) },
        onConfirmWeightWarning = viewModel::onWeightWarningConfirmed,
        onDismissWeightWarning = viewModel::onWeightWarningDismissed
    )
}

@Composable
fun AddCourseContent(
    name: String,
    partials: List<PartialConfigUiModel>,
    snackbarHostState: SnackbarHostState,
    showWeightWarningDialog: Boolean,
    onNameChange: (String) -> Unit,
    onWeightChange: (Int, Boolean, String) -> Unit, // Index, isExam, Value
    onSave: () -> Unit,
    onSaveAndDetail: () -> Unit,
    onConfirmWeightWarning: () -> Unit,
    onDismissWeightWarning: () -> Unit
) {
    if (showWeightWarningDialog) {
        AlertDialog(
            onDismissRequest = onDismissWeightWarning,
            title = { Text("Advertencia de Pesos") },
            text = { Text("La suma de los pesos de las evaluaciones no es igual a 100. ¿Deseas continuar de todas formas?") },
            confirmButton = {
                Button(
                    onClick = onConfirmWeightWarning,
                    colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple)
                ) {
                    Text("Sí, continuar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissWeightWarning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("No, corregir")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()) // Scrollable por si hay muchos parciales
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nueva Asignatura",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // INPUT NOMBRE
            Text(
                text = "Nombre de la asignatura:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("Asignatura 1", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color(0xFFE0E0E0),
                    focusedBorderColor = UnsaPurple,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN PESOS (CARD VERDE)
            Text(
                text = "Pesos ponderados:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MintGreenCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    partials.forEachIndexed { index, config ->
                        // Fila del Parcial
                        Text(
                            text = "Parcial ${config.number}:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Input Continua
                            WeightInput(
                                label = "Ev Continua ${config.number}:",
                                value = config.continuousWeightStr,
                                onValueChange = { onWeightChange(index, false, it) },
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Input Examen
                            WeightInput(
                                label = "Ev Examen ${config.number}:",
                                value = config.examWeightStr,
                                onValueChange = { onWeightChange(index, true, it) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (index < partials.size - 1) {
                            Spacer(modifier = Modifier.height(16.dp)) // Separador entre parciales
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "*Nota*: Asegurate de que los pesos siempre sumen 100%, podras editarlos mas adelante de todas formas",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTONES
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple), // Un poco más claro
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Curso", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSaveAndDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Curso y Colocar Notas", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Sub-componente para inputs de peso (DRY)
@Composable
fun WeightInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            placeholder = { Text("0", color = Color.Gray) },
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = UnsaPurple,
                unfocusedBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddCoursePreview() {
    AddCourseContent(
        name = "",
        partials = listOf(
            PartialConfigUiModel(1, "15", "15"),
            PartialConfigUiModel(2, "15", "15"),
            PartialConfigUiModel(3, "0", "0")
        ),
        snackbarHostState = SnackbarHostState(),
        showWeightWarningDialog = true, // Show dialog in preview
        onNameChange = {},
        onWeightChange = { _, _, _ -> },
        onSave = {},
        onSaveAndDetail = {},
        onConfirmWeightWarning = {},
        onDismissWeightWarning = {}
    )
}
