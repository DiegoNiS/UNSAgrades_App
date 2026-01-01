package com.example.unsagrades.ui.feature.dashboard

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.unsagrades.ui.common.CustomProgressBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.unsagrades.ui.common.CourseStatus
import com.example.unsagrades.ui.common.ItemCard
import com.example.unsagrades.ui.common.ItemSortChip
import com.example.unsagrades.ui.common.SortDirection
import com.example.unsagrades.ui.common.UnsaBottomBar
import com.example.unsagrades.ui.common.UnsaTopBar
import com.example.unsagrades.ui.navigation.Routes
import com.example.unsagrades.ui.theme.StateFailingBg
import com.example.unsagrades.ui.theme.StateWarningBg
import com.example.unsagrades.ui.theme.StatePassingBg
import com.example.unsagrades.ui.theme.UNSAGradesTheme
import kotlin.math.roundToInt

// 1. STATEFUL (Conectado)
@Composable
fun DashboardScreen(
    onNavigateToCreateCourse: () -> Unit,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val semesterName by viewModel.semesterName.collectAsState()
    val courses by viewModel.courseCards.collectAsState()
    val sortState by viewModel.sortState.collectAsState()  // Observamos el orden
    DashboardContent(
        semesterName = semesterName,
        courses = courses,
        sortState = sortState,
        onSortChange = viewModel::onSortChange,
        onAddCourseClick = onNavigateToCreateCourse,
        onCourseClick = onNavigateToCourseDetail,
        //onHistoryClick = onNavigateToHistory,
        innerPadding = innerPadding
    )
}

// 2. STATELESS (UI Pura)
@Composable
fun DashboardContent(
    semesterName: String,
    courses: List<CourseCardUiModel>,
    sortState: SortState, // Recibimos el estado complejo
    onSortChange: (SortType) -> Unit,
    onAddCourseClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    innerPadding : PaddingValues
) {
    val scrollState = rememberScrollState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim//background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState) // <--- ESTO activa el scroll para toda la pantalla
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.titleSmall.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append("Este es tu  ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.titleLarge.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append(semesterName)
                        }
                    }
                )
            }


            // BOTÓN AGREGAR ASIGNATURA
            ButtomAddCourse(onClick = onAddCourseClick)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ordenar por: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.width(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SortType.values()) { sortOption ->
                        val isSelected = sortOption == sortState.type
                        ItemSortChip(
                            text = sortOption.displayName,
                            isSelected = isSelected,
                            // Si está seleccionado, pasamos la dirección actual, si no, null
                            direction = if (isSelected) sortState.direction else null,
                            onClick = { onSortChange(sortOption) }
                        )
                    }
                }
            }

            // ASIGNATURAS
            courses.forEach { course ->
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course.id) }
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ButtomAddCourse(
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+ Agregar nuevo", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

// 3. COMPONENTE CARD (Basado en tus Colores)
@Composable
fun CourseCard(
    course: CourseCardUiModel,
    onClick: () -> Unit
) {
    ItemCard(onClick = onClick) {
        // Título
        Text(
            text = course.name.toUpperCase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusIcon(status = course.status)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = String.format("%.1f", course.average),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de Progreso (Azul y Gris)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(104.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = "${(course.progress * 100).roundToInt()} % completado",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            CustomProgressBar(
                progress = course.progress,
                modifier = Modifier.weight(1f),
                colorProgreso = MaterialTheme.colorScheme.secondary,
                colorFondo = MaterialTheme.colorScheme.surfaceDim,
            )
        }
    }
}

@Composable
fun StatusIcon(status: CourseStatus) {
    val (color, msg) = when (status) {
        CourseStatus.PASSING -> Pair(StatePassingBg, "Aprobado")
        CourseStatus.PROJECTED -> Pair(StateWarningBg, "En Progreso")
        CourseStatus.FAILING -> Pair(StateFailingBg, "Reprobado")
        CourseStatus.UNKNOWN -> Pair(Color.Gray, "Sin información")
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Estado: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.width(4.dp))
        TagIcon(msg = msg, color = color)
    }
}

@Composable
fun TagIcon( msg: String, color: Color,  width : Dp = 96.dp) {
    Box(
        modifier = Modifier
            .height(20.dp)
            .width(width)
            .clip(RoundedCornerShape(16.dp))
            .background(color = color)
    ) {
        Text(text = msg, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CourseCardPreview() {
//    Column(modifier = Modifier.padding(all = 16.dp)) {
//        CourseCard(
//            course = CourseCardUiModel(
//                id = "1",
//                name = "Aspectos Formales de especificacion y verificación",
//                average = 15.0,
//                progress = 0.6f,
//                status = CourseStatus.PASSING
//            ),
//            onClick = {}
//        )
//    }
//}

//// 4. PREVIEW SIMPLE (Solo contenido)
//@Preview(showBackground = true)
//@Composable
//fun DashboardContentPreview() {
//    val sampleCourses = getMockCourses()
//    UNSAGradesTheme(dynamicColor = false) {
//        DashboardContent(
//            semesterName = "Sexto Semestre",
//            courses = sampleCourses,
//            onAddCourseClick = {},
//            onCourseClick = {},
//            onHistoryClick = {}
//        )
//    }
//}

// 5. PREVIEW COMPLETO (Con Header y Footer) - ¡NUEVO! 🌟
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardFullFramePreview() {
    val sampleCourses = getMockCourses()
    UNSAGradesTheme(dynamicColor = false) {
        // Simulamos un NavController para que la barra no falle
        val fakeNavController = rememberNavController()

        // Creamos un Scaffold falso solo para el preview
        Scaffold(
            topBar = { UnsaTopBar(userName = "Dante") },
            bottomBar = { UnsaBottomBar(
                navController = fakeNavController,
                testRoute = "dashboard",
                currentFabIcon = Icons.Default.Home,
                onFabClick = {}
            ) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    // elevation = FloatingActionButtonDefaults.elevation(8.dp), // Sombra fuerte
                    modifier = Modifier
                        .offset(y = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Curso")
                }
            },
            floatingActionButtonPosition =  FabPosition.Center // Posición central
        ) { innerPadding ->
            // Simulamos el padding que MainScreen le pasaría
            Box(modifier = Modifier.padding(innerPadding)) {
                DashboardContent(
                    semesterName = "Sexto Semestre",
                    courses = sampleCourses,
                    onAddCourseClick = {},
                    onCourseClick = {},
                    sortState = SortState(type = SortType.AVERAGE, direction = SortDirection.DESCENDING),
                    onSortChange = {},
                    innerPadding = innerPadding
                )
            }
        }
    }
}

// Helper para mock data
fun getMockCourses() = listOf(
    CourseCardUiModel("1", "Aspectos Formales", 10.4, 0.4f, CourseStatus.FAILING),
    CourseCardUiModel("2", "Calidad de Software", 9.7, 0.3f, CourseStatus.FAILING),
    CourseCardUiModel("3", "Diseño de Arquitectura", 8.5, 0.5f, CourseStatus.PROJECTED),
    CourseCardUiModel("4", "Nuevas Plataformas", 15.5, 0.8f, CourseStatus.PASSING),
)