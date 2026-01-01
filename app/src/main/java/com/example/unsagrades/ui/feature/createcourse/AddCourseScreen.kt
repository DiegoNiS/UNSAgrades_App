package com.example.unsagrades.ui.feature.createcourse

import android.R
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.ui.feature.dashboard.TagIcon
import com.example.unsagrades.ui.theme.StateFailingBg
import com.example.unsagrades.ui.theme.StatePassingBg
import com.example.unsagrades.ui.theme.UNSAGradesTheme
import kotlin.math.abs

@Composable
fun AddCourseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val name by viewModel.courseName.collectAsState()
    val partials by viewModel.partialConfigs.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showWeightWarningDialog by viewModel.showWeightWarningDialog.collectAsState()
    val totalWeight by viewModel.totalWeight.collectAsState()


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
        onDismissWeightWarning = viewModel::onWeightWarningDismissed,
        totalWeight = totalWeight,
        innerPadding = innerPadding
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
    onDismissWeightWarning: () -> Unit,
    totalWeight: Float = 0f,
    innerPadding : PaddingValues
) {
    if (showWeightWarningDialog) {
        AlertDialog(
            onDismissRequest = onDismissWeightWarning,
            title = { Text("Advertencia de Pesos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) },
            text = { Text("La suma de los pesos de las evaluaciones no es igual a 100 %. ¿Deseas continuar de todas formas?", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = onConfirmWeightWarning,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Sí, continuar", color = MaterialTheme.colorScheme.onSecondary)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissWeightWarning,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                ) {
                    Text("No, corregir", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }

    // 1. Obtenemos el administrador de foco actual
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()) // Scrollable por si hay muchos parciales
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crea una nueva asignatura",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // INPUT NOMBRE
            Text(
                text = "Nombre de la asignatura:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                textStyle = MaterialTheme.typography.bodyLarge,
                onValueChange = onNameChange,
                placeholder = { Text("Asignatura 1", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)) },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,imeAction = ImeAction.Next),

                // B. Le decimos: "Al dar Siguiente, mueve el foco hacia abajo"
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true

            )

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN PESOS (CARD VERDE)
            Row {
                Text(
                    text = "Pesos ponderados:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))


                Row(){
                    Text(
                        text = "Peso Actual: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TagIcon(
                        msg = "${totalWeight} %",
                        color = if(abs(totalWeight - 100) <= 0.1) StatePassingBg else StateFailingBg,
                        width = 56.dp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    partials.forEachIndexed { index, config ->
                        // Fila del Parcial
                        Text(
                            text = "Parcial ${config.number}:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
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
                                modifier = Modifier.weight(1f),
                                focusManager = focusManager,
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Input Examen
                            WeightInput(
                                label = "Ev Examen ${config.number}:",
                                value = config.examWeightStr,
                                onValueChange = { onWeightChange(index, true, it) },
                                modifier = Modifier.weight(1f),
                                focusManager = focusManager,
                                isLast = index == partials.size - 1
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
                text = "Asegurate de que los pesos siempre sumen 100%, podras editarlos mas adelante de todas formas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTONES
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), // Un poco más claro
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Curso", color = MaterialTheme.colorScheme.onSecondary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSaveAndDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Curso y Colocar Notas", color = MaterialTheme.colorScheme.onTertiary)
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
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    isLast: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = { Text("0", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)) },
            onValueChange = onValueChange,
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = if (!isLast) ImeAction.Next else ImeAction.Done),

            // B. Le decimos: "Al dar Siguiente, mueve el foco hacia abajo"
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }, onDone = {
                        focusManager.clearFocus()
                }
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddCoursePreview() {
    UNSAGradesTheme(dynamicColor = false) {
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
            onDismissWeightWarning = {},
            innerPadding = PaddingValues(0.dp)
        )
    }
}
