package com.example.unsagrades.ui.feature.profile

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.R
import com.example.unsagrades.ui.theme.UNSAGradesTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val state by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = state.userName,
            currentAvatar = state.avatar,
            onDismiss = { showEditDialog = false },
            onSave = { name, avatar ->
                viewModel.saveProfile(name, avatar)
                showEditDialog = false
            }
        )
    }

    ProfileContent(
        state = state,
        onEditClick = { showEditDialog = true },
        onGoToDashboard = onNavigateToDashboard,
        innerPadding = innerPadding
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun ProfileContent(
    state: ProfileUiState,
    onEditClick: () -> Unit,
    onGoToDashboard: () -> Unit,
    innerPadding : PaddingValues
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // AVATAR Y NOMBRE
            Box(contentAlignment = Alignment.BottomEnd) {
                // Avatar Placeholder (Círculo con icono)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        //.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
                        .border(2.dp, MaterialTheme.colorScheme.surfaceContainerLow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(state.avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(60.dp),
                        //tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Botón editar
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Estudiante UNSA", // Podríamos guardarlo en BD también
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ESTADÍSTICAS GRID
                StatCard(
                    "Promedio Global",
                    String.format("%.1f", state.careerAverage),
                    Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                StatCard("Mejor Semestre", state.bestSemesterName, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatCard(
                    "Cursos Aprobados",
                    "${state.totalApprovedCourses}",
                    Modifier.weight(1f),
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(12.dp))
                StatCard(
                    "Cursos Jalados",
                    "${state.totalFailedCourses}",
                    Modifier.weight(1f),
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // GRÁFICO
            Text(
                "Tu Evolución",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.historyPoints.isNotEmpty()) {
                SimpleLineChart(
                    points = state.historyPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            } else {
                Text(
                    "Registra notas para ver tu gráfico",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN: VOLVER AL SEMESTRE ACTUAL
            Button(
                onClick = onGoToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Semestre Actual")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SimpleLineChart(points: List<Double>, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(points) {
        isLoading = true
        // Simulamos un pequeño retraso o simplemente la carga de datos
        // En un caso real, esto dependería de cuándo el modelProducer termina de procesar
        // Para Vico, la carga suele ser muy rápida, pero si los datos vienen de fuera o son muchos:
        try {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(points)
                }
            }
        } finally {
            isLoading = false
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun EditProfileDialog(currentName: String, currentAvatar: Int, onDismiss: () -> Unit, onSave: (String, Int) -> Unit) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tu Nombre") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text, currentAvatar) }) { Text("Guardar") }
        }
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ProfileScreenPreview() {
    // Create a mock state object simulating a user with some history
    val mockState = ProfileUiState(
        userName = "Diego Nina",
        avatar = R.drawable.avatar_monster_1,
        totalSemesters = 5,
        careerAverage = 14.5,
        totalApprovedCourses = 12,
        totalFailedCourses = 2,
        bestSemesterName = "2023-B",
        historyPoints = listOf(13.0, 14.5, 12.0, 16.0, 15.5),
    )

    // Apply the theme (if you have a custom theme wrapper, use that instead of MaterialTheme)
    UNSAGradesTheme(
        dynamicColor = false
    ) {
        ProfileContent(
            state = mockState,
            onEditClick = {},
            onGoToDashboard = {},
            innerPadding = PaddingValues(0.dp)
        )
    }
}