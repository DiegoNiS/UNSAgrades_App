package com.example.unsagrades.ui.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.ui.theme.StateFailingBg
import com.example.unsagrades.ui.theme.StateFailingText
import com.example.unsagrades.ui.theme.StatePassingBg
import com.example.unsagrades.ui.theme.StatePassingText
import com.example.unsagrades.ui.theme.StateWarningBg
import com.example.unsagrades.ui.theme.StateWarningText
import com.example.unsagrades.ui.theme.UnsaPurple

// 1. STATEFUL (Conectado)
@Composable
fun DashboardScreen(
    onNavigateToCreateCourse: () -> Unit,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val semesterName by viewModel.semesterName.collectAsState()
    val courses by viewModel.courseCards.collectAsState()

    DashboardContent(
        semesterName = semesterName,
        courses = courses,
        onAddCourseClick = onNavigateToCreateCourse,
        onCourseClick = onNavigateToCourseDetail,
        onHistoryClick = onNavigateToHistory
    )
}

// 2. STATELESS (UI Pura)
@Composable
fun DashboardContent(
    semesterName: String,
    courses: List<CourseCardUiModel>,
    onAddCourseClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    onHistoryClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = semesterName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Historial",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN AGREGAR ASIGNATURA
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = onAddCourseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ Agregar asignatura", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA DE CURSOS
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Espacio para footer
            ) {
                items(courses) { course ->
                    CourseCard(course = course, onClick = { onCourseClick(course.id) })
                }

//                item {
//                    Spacer(modifier = Modifier.height(16.dp))
//                    // Footer: Terminar Semestre
//                    Button(
//                        onClick = { /* TODO: Lógica terminar */ },
//                        modifier = Modifier.fillMaxWidth().height(50.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple.copy(alpha = 0.8f)),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text("Terminar Semestre", color = Color.White)
//                    }
//                }
            }
        }
    }
}

// 3. COMPONENTE CARD (Basado en tus Colores)
@Composable
fun CourseCard(
    course: CourseCardUiModel,
    onClick: () -> Unit
) {
    // Determinar colores según estado
    val (bgColor, textColor, msg) = when (course.status) {
        CourseStatus.PASSING -> Triple(
            StatePassingBg,
            StatePassingText,
            "Ya lo aprobaste, te faltan registrar notas"
        )
        CourseStatus.PROJECTED -> Triple(
            StateWarningBg,
            StateWarningText,
            "Aprobarías si confirmas tus notas"
        )
        CourseStatus.FAILING -> Triple(
            StateFailingBg,
            StateFailingText,
            "Aún no aprobaste, tienes notas por confirmar."
        )
        CourseStatus.UNKNOWN -> Triple(Color.LightGray, Color.Black, "Sin información")
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Título
            Text(
                text = course.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progreso (Azul y Gris)
            Row(verticalAlignment = Alignment.CenterVertically) {
//                // Punto Azul
//                Box(
//                    modifier = Modifier
//                        .size(8.dp)
//                        .clip(CircleShape)
//                        .background(Color(0xFF2196F3)) // Azul
//                )
//                Spacer(modifier = Modifier.width(4.dp))

                // Barra
                LinearProgressIndicator(
                    progress = { course.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF2196F3), // Azul progreso
                    trackColor = Color.Black.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nota Grande y Mensaje
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f", course.average),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = msg,
                    fontSize = 12.sp,
                    color = textColor, // Color dinámico (Rojo oscuro, Verde oscuro)
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// 4. PREVIEW
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val sampleCourses = listOf(
        CourseCardUiModel("1", "Aspectos Formales", 10.4, 0.4f, CourseStatus.FAILING),
        CourseCardUiModel("2", "Calidad de Software", 9.7, 0.3f, CourseStatus.FAILING),
        CourseCardUiModel("3", "Diseño de Arquitectura", 8.5, 0.5f, CourseStatus.PROJECTED),
        CourseCardUiModel("4", "Nuevas Plataformas", 15.5, 0.8f, CourseStatus.PASSING),
    )

    DashboardContent(
        semesterName = "Sexto Semestre",
        courses = sampleCourses,
        onAddCourseClick = {},
        onCourseClick = {},
        onHistoryClick = {}
    )
}