package com.example.unsagrades.ui.feature.coursedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.data.local.entity.EvaluationConfigEntity
import com.example.unsagrades.data.local.entity.GradeEntity
import com.example.unsagrades.data.local.entity.GradeType
import com.example.unsagrades.ui.theme.UnsaPurple
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.example.unsagrades.ui.theme.TextGray

// Colores específicos de tu diseño
val AccordionHeader = Color(0xFFB0BCCF) // Gris azulado header
val AccordionBody = Color(0xFF9FA8DA).copy(alpha = 0.3f) // Azul claro cuerpo
val SustiBlue = Color(0xFF5C6BC0) // Azul fuerte susti

@Composable
fun CourseDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    CourseDetailContent(
        state = state,
        onGradeChange = viewModel::onGradeChange,
        onGradeStep = viewModel::onGradeStep,
        onConfirmationChange = viewModel::onConfirmationChange,
        onInclusionChange = viewModel::onInclusionChange,
        onAddSusti = viewModel::addSustitutorio,
        onRemoveSusti = viewModel::removeSustitutorio,
        onUpdateSusti = viewModel::onUpdateSusti,
        onWeightChange = viewModel::onWeightChange
    )
}

@Composable
fun CourseDetailContent(
    state: CourseDetailUiState,
    onGradeChange: (GradeEntity, String) -> Unit,
    onGradeStep: (GradeEntity, Int) -> Unit,
    onConfirmationChange: (GradeEntity, Boolean) -> Unit,
    onInclusionChange: (GradeEntity, Boolean) -> Unit,
    onAddSusti: () -> Unit,
    onRemoveSusti: () -> Unit,
    onUpdateSusti: (GradeEntity, String) -> Unit,
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit
) {
    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // <--- ¡AQUÍ ESTÁ LA SOLUCIÓN! Empuja el contenido hacia arriba con el teclado.
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // HEADER
            Text(
                text = state.courseName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${state.status}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = String.format("%.1f", state.average),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LISTA DE PARCIALES
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
            ) {
                items(state.partials) { partial ->
                    PartialItem(
                        partial = partial,
                        onGradeChange = onGradeChange,
                        onGradeStep = onGradeStep,
                        onConfirmationChange = onConfirmationChange,
                        onInclusionChange = onInclusionChange,
                        onWeightChange = onWeightChange
                    )
                }

                // SECCIÓN SUSTITUTORIO
                item {
                    SustitutorioSection(
                        sustiGrade = state.sustitutorio,
                        onAddSusti = onAddSusti,
                        onRemoveSusti = onRemoveSusti,
                        onUpdateSusti = onUpdateSusti,
                        onConfirmationChange = onConfirmationChange,
                        onInclusionChange = onInclusionChange,
                        onGradeStep = onGradeStep
                    )
                }
            }
        }
    }
}

@Composable
fun PartialItem(
    partial: PartialUiModel,
    onGradeChange: (GradeEntity, String) -> Unit,
    onGradeStep: (GradeEntity, Int) -> Unit,
    onConfirmationChange: (GradeEntity, Boolean) -> Unit,
    onInclusionChange: (GradeEntity, Boolean) -> Unit,
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Estado local del acordeón
    val rotationState by animateFloatAsState(if (expanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccordionHeader.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        // HEADER DEL PARCIAL (Siempre visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Parcial ${partial.config.partialNumber}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expandir",
                modifier = Modifier.rotate(rotationState),
                tint = Color.Black
            )
        }

        // CUERPO EXPANDIBLE
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .background(AccordionBody, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                // FILA CONTINUA
                GradeRow(
                    label = "Ev. Continua",
                    partialconfig = partial.config,
                    isExam = false,
                    //weight = partial.config.continuousWeight,
                    grade = partial.continuousGrade,
                    //valueGrade = partial.continuousGrade,
                    onStep = { onGradeStep(partial.continuousGrade, it) },
                    onConfirm = { onConfirmationChange(partial.continuousGrade, it) },
                    onInclude = { onInclusionChange(partial.continuousGrade, it) },
                    onWeightChange = onWeightChange,
                    onGradeChange = onGradeChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                // FILA EXAMEN
                GradeRow(
                    label = "Ev. Examen",
                    partialconfig = partial.config,
                    isExam = true,
                    //weight = partial.config.examWeight,
                    grade = partial.examGrade,
                    //valueGrade = partial.examGrade,
                    onStep = { onGradeStep(partial.examGrade, it) },
                    onConfirm = { onConfirmationChange(partial.examGrade, it) },
                    onInclude = { onInclusionChange(partial.examGrade, it) },
                    onWeightChange = onWeightChange,
                    onGradeChange = onGradeChange
                )
            }
        }
    }
}

@Composable
fun GradeRow(
    label: String,
    partialconfig: EvaluationConfigEntity,
    isExam: Boolean,
    //weight: Float, // Ej: 60.0
    grade: GradeEntity,
    //valueGrade: GradeEntity,
    onStep: (Int) -> Unit,
    onConfirm: (Boolean) -> Unit,
    onInclude: (Boolean) -> Unit,
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit,
    onGradeChange: (GradeEntity, String) -> Unit
) {
    val isLocked = grade.isConfirmed
    val weight = if (isExam) partialconfig.examWeight else partialconfig.continuousWeight

    Column {
        // Título de fila: "Ev. Continua - 40%"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 14.sp, color = Color.Black)
            // Icono de lápiz (decorativo por ahora) y peso
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                InlineEditableWeight(
                    value = weight.toInt().toString(),
                    partialconfig = partialconfig,
                    isExam = isExam,
                    onWeightChange = onWeightChange
                )
//                Text(text = , fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.width(8.dp))
//                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        inputGradeLayout(
            isLocked = isLocked,
            onStep = onStep,
            grade = grade,
            //valueGrade = valueGrade,
            onGradeChange = onGradeChange,
            onConfirm = onConfirm,
            onInclude = onInclude,
            isDark = false
        )

        // aca ira el input inputGradeLayout
    }
}

@Composable
fun inputGradeLayout(
    isLocked: Boolean,
    onStep: (Int) -> Unit,
    grade: GradeEntity,
    //valueGrade: GradeEntity,
    onGradeChange: (GradeEntity, String) -> Unit,
    onConfirm: (Boolean) -> Unit,
    onInclude: (Boolean) -> Unit,
    isDark: Boolean
) {
    // Controles (+, Input, -)
    Column (
        //verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (!isLocked) {
            // Botón Menos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onStep(-1) }, modifier = Modifier.size(32.dp)) {
                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                }

                // 1. Preparamos el texto visual: Si es 0, mostramos vacío para que el placeholder actúe
                val displayValue = if (grade.value == 0) "" else grade.value.toString()
                OutlinedTextField(
                    value = TextFieldValue(
                        text = displayValue,
                        selection = TextRange(displayValue.length)
                    ),
                    placeholder = { Text("0", color = Color.Gray) },
                    onValueChange = { newTFV ->
                        onGradeChange(grade, newTFV.text)
                    },
                    modifier = Modifier
                        .width(60.dp),
                    //.height(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                )

                // Botón Más
                IconButton(onClick = { onStep(1) }, modifier = Modifier.size(32.dp)) {
                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Checkbox (Incluir)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(
                        checked = grade.isIncludedInAverage,
                        onCheckedChange = onInclude
                    )
                    Text("Promediar", fontSize = 10.sp, color = if (isDark) Color.White else Color.Black)
                }
            }
            //Spacer(modifier = Modifier.weight(1f))
        } else {
            // MODO SOLO LECTURA (Confirmado)
            Text(
                text = grade.value.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
            //Spacer(modifier = Modifier.weight(1f))
        }

        // Switch Confirmación
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = grade.isConfirmed,
                onCheckedChange = onConfirm,
                modifier = Modifier.scale(0.8f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLocked) "Nota Confirmada" else "Nota Borrador",
                fontSize = 10.sp,
                color = if (isDark) Color.LightGray else Color.Gray
            )
        }
    }
}

@Composable
fun InlineEditableWeight(
    value: String,
    partialconfig: EvaluationConfigEntity,
    isExam: Boolean,
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    // 1. EL SEGURO: Esta variable recordará si el campo YA ganó el foco alguna vez.
    var hasGainedFocus by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val textStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {

        if (isEditing) {
            // 1. PREPARAR EL TEXTO INICIAL (Lógica del "0")
            // Si es "0", lo volvemos vacío para que sea fácil escribir algo nuevo
            val initialText = if (value == "0") "" else value

            // 2. ESTADO LOCAL AVANZADO
            // Usamos TextFieldValue en lugar de String.
            // 'selection = TextRange(initialText.length)' pone el cursor AL FINAL.
            var textFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = initialText,
                        selection = TextRange(initialText.length)
                    )
                )
            }

            BasicTextField(
                // 3. Pasamos el OBJETO COMPLETO (texto + cursor)
                value = textFieldValue,

                onValueChange = { newTFV ->
                    // Actualizamos el estado local (para que el cursor se mueva al escribir)
                    textFieldValue = newTFV

                    // Enviamos SOLO EL TEXTO a tu lógica de negocio
                    onWeightChange(partialconfig, newTFV.text, isExam)
                },

                textStyle = textStyle.copy(color = UnsaPurple),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isEditing = false
                        hasGainedFocus = false
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .width(40.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        // Tu lógica del SEGURO (intacta, funciona perfecto)
                        if (focusState.isFocused) {
                            hasGainedFocus = true
                        }
                        if (!focusState.isFocused && hasGainedFocus) {
                            isEditing = false
                            hasGainedFocus = false
                        }
                    }
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

        } else {
            Text(
                text = "$value%",
                style = textStyle
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Botón de editar
        IconButton(
            onClick = {
                isEditing = !isEditing
                // Nota: No tocamos hasGainedFocus aquí, dejamos que el TextField lo maneje
            },
            modifier = Modifier.size(16.dp) // Un poquito más grande para el dedo (12dp es muy poco)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = if (isEditing) UnsaPurple else Color.Gray, // Feedback visual
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun SustitutorioSection(
    sustiGrade: GradeEntity?,
    onAddSusti: () -> Unit,
    onRemoveSusti: () -> Unit,
    onUpdateSusti: (GradeEntity, String) -> Unit,
    onConfirmationChange: (GradeEntity, Boolean) -> Unit,
    onInclusionChange: (GradeEntity, Boolean) -> Unit,
    onGradeStep: (GradeEntity, Int) -> Unit
) {
    if (sustiGrade == null) {
        // ESTADO 1: Botón para agregar
        Button(
            onClick = onAddSusti,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SustiBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+ Agregar Sustitutorio", color = Color.White)
        }
    } else {
        // ESTADO 2: Panel de Sustitutorio (Azul oscuro)
        Card(
            colors = CardDefaults.cardColors(containerColor = SustiBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sustitutorio", color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                // Reutilizamos GradeRow pero con colores custom si quisiéramos
                // Por simplicidad, usamos una fila manual parecida

                inputGradeLayout(
                    isLocked = sustiGrade.isConfirmed,
                    onStep = { onGradeStep(sustiGrade, it) },
                    grade = sustiGrade,
                    //valueGrade = valueGrade,
                    onGradeChange = onUpdateSusti,
                    onConfirm = { onConfirmationChange(sustiGrade, it) },
                    onInclude = { onInclusionChange(sustiGrade, it) },
                    isDark = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Rojo Eliminar
                Button(
                    onClick = onRemoveSusti,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar sustitutorio", color = Color.White)
                }
            }
        }
    }
}

// Helper para escalar el Switch
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CourseDetailPreview() {
    // --- MOCK DATA PARA PREVIEW ---
    val config1 = EvaluationConfigEntity(
        id = "c1", courseId = "mock", partialNumber = 1, examWeight = 60f, continuousWeight = 40f
    )
    val config2 = EvaluationConfigEntity(
        id = "c2", courseId = "mock", partialNumber = 2, examWeight = 50f, continuousWeight = 50f
    )

    // Parcial 1: Notas confirmadas (Modo lectura)
    val p1Cont = GradeEntity(id = "g1", configId = "c1", type = GradeType.CONTINUOUS, value = 14, isConfirmed = true, isIncludedInAverage = true)
    val p1Exam = GradeEntity(id = "g2", configId = "c1", type = GradeType.EXAM, value = 12, isConfirmed = true, isIncludedInAverage = true)

    // Parcial 2: Notas borrador (Modo edición)
    val p2Cont = GradeEntity(id = "g3", configId = "c2", type = GradeType.CONTINUOUS, value = 16, isConfirmed = false, isIncludedInAverage = true)
    val p2Exam = GradeEntity(id = "g4", configId = "c2", type = GradeType.EXAM, value = 0, isConfirmed = false, isIncludedInAverage = true)

    val mockState = CourseDetailUiState(
        courseName = "Inteligencia Artificial",
        average = 13.0,
        status = "Aprobado",
        partials = listOf(
            PartialUiModel(config1, p1Cont, p1Exam),
            PartialUiModel(config2, p2Cont, p2Exam)
        ),
        sustitutorio = null
    )

    CourseDetailContent(
        state = mockState,
        onGradeChange = { _, _ -> },
        onGradeStep = { _, _ -> },
        onConfirmationChange = { _, _ -> },
        onInclusionChange = { _, _ -> },
        onAddSusti = {},
        onRemoveSusti = {},
        onUpdateSusti = { _, _ -> },
        onWeightChange = { _, _, _ -> },
    )
}