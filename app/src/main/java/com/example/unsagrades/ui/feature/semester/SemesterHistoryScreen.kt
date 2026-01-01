package com.example.unsagrades.ui.feature.semester

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.navigation.compose.rememberNavController
import com.example.unsagrades.ui.common.ItemCard
import com.example.unsagrades.ui.common.ItemSortChip
import com.example.unsagrades.ui.common.SortDirection
import com.example.unsagrades.ui.common.UnsaBottomBar
import com.example.unsagrades.ui.common.UnsaTopBar
import com.example.unsagrades.ui.theme.UNSAGradesTheme

@Composable
fun SemesterHistoryScreen(
    onNavigateToNewSemester: () -> Unit,
    onSemesterClick: (String) -> Unit, // Callback nuevo
    viewModel: SemesterHistoryViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val historyList by viewModel.uiState.collectAsState()
    val sortState by viewModel.sortState.collectAsState()

    SemesterHistoryContent(
        historyList = historyList,
        sortState = sortState,
        onSortChange = viewModel::onSortChange,
        onAddSemester = onNavigateToNewSemester,
        onSemesterClick = onSemesterClick,
        innerPadding = innerPadding
    )
}

@Composable
fun SemesterHistoryContent(
    historyList: List<SemesterHistoryUiModel>,
    sortState: HistorySortState,
    onSortChange: (HistorySortType) -> Unit,
    onAddSemester: () -> Unit,
    onSemesterClick: (String) -> Unit,
    innerPadding : PaddingValues
) {
    val scrollState = rememberScrollState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceDim//background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            //
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = buildAnnotatedString {
                        // 1. Primera parte (Texto pequeño)
                        withStyle(
                            style = MaterialTheme.typography.titleSmall.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append("Échale un vistazo a tu  ")
                        }

                        // 2. Segunda parte (Texto grande)
                        withStyle(
                            style = MaterialTheme.typography.titleLarge.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append("Historial de semestres")
                        }
                    }
                )
            }

            ButtonAddSemester(onClick = onAddSemester)

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
                    items(HistorySortType.values()) { type ->
                        val isSelected = type == sortState.type
                        ItemSortChip(
                            text = type.displayName,
                            isSelected = isSelected,
                            direction = if (isSelected) sortState.direction else null,
                            onClick = { onSortChange(type) }
                        )
                    }
                }
            }
            historyList.forEach { semester ->
                SemesterHistoryCard(
                    semester = semester,
                    onClick = { onSemesterClick(semester.id) }
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ButtonAddSemester(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+ Agregar Semestre", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Composable
fun SemesterHistoryCard(semester: SemesterHistoryUiModel, onClick: () -> Unit) {
    ItemCard(onClick = onClick, heigh = 120.dp) {
        Text(
            text = semester.name.toUpperCase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aprobados:",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${semester.approvedCount}",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Desaprobados:",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${semester.failedCount}",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(
                text = String.format("%.1f", semester.average),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = semester.message,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryPreview() {
    val sampleList = listOf(
        SemesterHistoryUiModel("1", "Primer Semestre", 12, 0, 18.4, "Buen semestre, buenas notas", false),
        SemesterHistoryUiModel("2", "Tercer Semestre", 10, 3, 12.4, "Hubo problemas al desaprobar un curso", false),
        SemesterHistoryUiModel("3", "Cuarto Semestre", 10, 6, 8.3, "Semestre complicado", false),
        SemesterHistoryUiModel("4", "Quinto Semestre", 10, 6, 8.3, "Semestre complicado", false)
    )

    UNSAGradesTheme(
        dynamicColor = false
    ) {
        val fakeNavController = rememberNavController()
        Scaffold(
            topBar = { UnsaTopBar(userName = "Dante") },
            bottomBar = {
                UnsaBottomBar(
                    navController = fakeNavController,
                    testRoute = "semester_history",
                    currentFabIcon = Icons.Default.Home,
                    onFabClick = {}
                ) }
        ) { innerPadding ->
            Box()
            {
                SemesterHistoryContent(
                    historyList = sampleList,
                    onAddSemester = {},
                    sortState = HistorySortState(type = HistorySortType.AVERAGE, direction = SortDirection.DESCENDING),
                    onSortChange = {},
                    onSemesterClick = {},
                    innerPadding = innerPadding
                )
            }
        }
    }
}