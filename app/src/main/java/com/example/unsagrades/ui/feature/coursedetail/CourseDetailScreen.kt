package com.example.unsagrades.ui.feature.coursedetail

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
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

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.example.unsagrades.ui.common.ColumnAnimated
import com.example.unsagrades.ui.common.ConfigurarBarraEstado
import com.example.unsagrades.ui.common.CourseStatus
import com.example.unsagrades.ui.common.ModalDeIngreso
import com.example.unsagrades.ui.common.RuedaInfinitaHorizontal
import com.example.unsagrades.ui.feature.dashboard.StatusIcon
import com.example.unsagrades.ui.feature.dashboard.TagIcon
import com.example.unsagrades.ui.theme.StateFailingBg
import com.example.unsagrades.ui.theme.StatePassingBg
import com.example.unsagrades.ui.theme.UNSAGradesTheme
import kotlin.math.abs

val SustiBlue = Color(0xFF5C6BC0) // Azul fuerte susti

@Composable
fun CourseDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    innerPadding: PaddingValues
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
        onWeightChange = viewModel::onWeightChange,
        onNavigateBack = onNavigateBack,
        innerPadding = innerPadding
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
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit,
    test: Boolean = false,
    onNavigateBack: () -> Unit = {},
    innerPadding: PaddingValues
) {
    ConfigurarBarraEstado(
        color = MaterialTheme.colorScheme.tertiary,
        iconosOscuros = false
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(), // Ocupa TODA la pantalla
        containerColor = MaterialTheme.colorScheme.tertiary // Tu color azul/verde
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
                //.padding(top = 24.dp)
                //.padding(bottom = innerPadding.calculateBottomPadding())
                .background(color = MaterialTheme.colorScheme.surfaceDim)
        ) {
            Card(
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    //.statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding() // <--- EL PADDING VA AQUÍ ADENTRO
                ) {
                    Box(//hacer que los elementos estén centrados verticalmente, mas no horizontalmente
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Volver atras",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Detalles del curso",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.courseName.uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding() // <--- ¡AQUÍ ESTÁ LA SOLUCIÓN! Empuja el contenido hacia arriba con el teclado.
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceDim),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        StatusIcon(
                                status = state.status,
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(
                                text = "Pesos sumados: ",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.labelSmall
                            )
                            TagIcon(
                                msg = "${state.totalWeight} %",
                                color = if (state.totalWeight == 100) StatePassingBg else StateFailingBg,
                                width = 56.dp
                            )
                        }
                    }


                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Suma:",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = String.format("%.1f", state.average),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                if (abs(state.progress - 100) <= 0.1) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 40.dp,
                                bottomStart = 40.dp,
                                bottomEnd = 24.dp
                            ),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "CURSO COMPLETADO",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                state.partials.forEach { it ->
                    PartialItem(
                        partial = it,
                        onGradeChange = onGradeChange,
                        onGradeStep = onGradeStep,
                        onConfirmationChange = onConfirmationChange,
                        onInclusionChange = onInclusionChange,
                        onWeightChange = onWeightChange,
                        expandedTest = test
                    )
                }
                SustitutorioSection(
                    sustiGrade = state.sustitutorio,
                    onAddSusti = onAddSusti,
                    onRemoveSusti = onRemoveSusti,
                    onUpdateSusti = onUpdateSusti,
                    onConfirmationChange = onConfirmationChange,
                    onInclusionChange = onInclusionChange,
                    onGradeStep = onGradeStep
                )

                Spacer(modifier = Modifier.height(80.dp))
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
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit,
    expandedTest : Boolean = false
) {
    ColumnAnimated(
        label = "Parcial ${partial.config.partialNumber}",
        expandedTest = expandedTest
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
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline
            )
            // Icono de lápiz (decorativo por ahora) y peso
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                InlineEditableWeight(
                    value = weight.toInt(),
                    partialconfig = partialconfig,
                    isExam = isExam,
                    onWeightChange = onWeightChange
                )
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
    Spacer(modifier = Modifier.height(8.dp))
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
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (!isLocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(
                        checked = grade.isIncludedInAverage,
                        onCheckedChange = onInclude,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "Contar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(
                                alpha = 0.7f
                            ), shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    RuedaInfinitaHorizontal(
                        items = (0..20).map { it.toString() },
                        initialIndex = 0,
                        visibleItemsCount = 5,
                        onSelectionChanged = {
                            onGradeChange(grade, it.toString())
                        },
                        tipographySelected = MaterialTheme.typography.titleMedium,
                        tipographyUnselected = MaterialTheme.typography.titleSmall,
                        colorSelected = MaterialTheme.colorScheme.onSurface,
                        colorUnselected = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        height = 40.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

            }
            else {
                Text(
                    text = grade.value.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
        }


        // Switch Confirmación
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Switch(
                checked = grade.isConfirmed,
                onCheckedChange = onConfirm,
                modifier = Modifier
                    .scale(0.8f)
                    .size(32.dp)
            )
            Text(
                text = "Afirmar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun InlineEditableWeight(
    value: Int,
    partialconfig: EvaluationConfigEntity,
    isExam: Boolean,
    onWeightChange: (EvaluationConfigEntity, String, Boolean) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { showModal = true }
    ) {
        Text(
            text = "$value %",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar",
            tint = MaterialTheme.colorScheme.onSurface, // Feedback visual
            modifier = Modifier.size(16.dp)
        )
    }

    if (showModal) {
        ModalDeIngreso(
            onDismiss = { showModal = false },
            onConfirm = {
                onWeightChange(partialconfig, it, isExam)
                showModal = false
            },
            message = "Ingresa el nuevo peso de ${if (isExam) "el examen" else "la continua"}",
            title = "Cambiar Peso en parcial ${partialconfig.partialNumber}",
            actualValue = value,
            label = "Nuevo Peso"
        )
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+ Agregar Sustitutorio", color = MaterialTheme.colorScheme.onSecondary)
        }
    } else {
        // ESTADO 2: Panel de Sustitutorio (Azul oscuro)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ColumnAnimated(
                label = "Sustitutorio",
                expandedTest = true
            ) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Rojo Eliminar
                Button(
                    onClick = onRemoveSusti,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar sustitutorio", color = MaterialTheme.colorScheme.onSecondary)
                }
                Spacer(modifier = Modifier.height(8.dp))
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

    val config3 = EvaluationConfigEntity(
        id = "c3", courseId = "mock", partialNumber = 3, examWeight = 40f, continuousWeight = 60f
    )


    // Parcial 1: Notas confirmadas (Modo lectura)
    val p1Cont = GradeEntity(id = "g1", configId = "c1", type = GradeType.CONTINUOUS, value = 14, isConfirmed = true, isIncludedInAverage = true)
    val p1Exam = GradeEntity(id = "g2", configId = "c1", type = GradeType.EXAM, value = 12, isConfirmed = true, isIncludedInAverage = true)

    // Parcial 2: Notas borrador (Modo edición)
    val p2Cont = GradeEntity(id = "g3", configId = "c2", type = GradeType.CONTINUOUS, value = 16, isConfirmed = false, isIncludedInAverage = true)
    val p2Exam = GradeEntity(id = "g4", configId = "c2", type = GradeType.EXAM, value = 0, isConfirmed = false, isIncludedInAverage = true)

    val p3Cont = GradeEntity(id = "g5", configId = "c3", type = GradeType.CONTINUOUS, value = 16, isConfirmed = false, isIncludedInAverage = true)
    val p3Exam = GradeEntity(id = "g6", configId = "c3", type = GradeType.EXAM, value = 0, isConfirmed = false, isIncludedInAverage = true)

    val mockState = CourseDetailUiState(
        courseName = "Inteligencia Artificial",
        average = 13.0,
        status = CourseStatus.PASSING,
        partials = listOf(
            //PartialUiModel(config1, p1Cont, p1Exam),
            PartialUiModel(config2, p2Cont, p2Exam),
            //PartialUiModel(config3, p3Cont, p3Exam)
        ),
        sustitutorio = null
    )

    UNSAGradesTheme(dynamicColor = false) {
        Scaffold() { innerPadding ->
            Box() {
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
                    test = false,
                    innerPadding = innerPadding
                )
            }
        }
    }
}