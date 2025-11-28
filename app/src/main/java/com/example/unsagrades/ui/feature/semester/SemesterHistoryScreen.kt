package com.example.unsagrades.ui.feature.semester

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.unsagrades.ui.theme.StateFailingBg
import com.example.unsagrades.ui.theme.StatePassingBg
import com.example.unsagrades.ui.theme.StateWarningBg
import com.example.unsagrades.ui.theme.UnsaPurple
import androidx.compose.material.icons.filled.Settings

@Composable
fun SemesterHistoryScreen(
    onNavigateToNewSemester: () -> Unit,
    onSemesterClick: (String) -> Unit, // Callback nuevo
    viewModel: SemesterHistoryViewModel = hiltViewModel()
) {
    val historyList by viewModel.uiState.collectAsState()

    SemesterHistoryContent(
        historyList = historyList,
        onAddSemester = onNavigateToNewSemester,
        onSemesterClick = onSemesterClick
    )
}

@Composable
fun SemesterHistoryContent(
    historyList: List<SemesterHistoryUiModel>,
    onAddSemester: () -> Unit,
    onSemesterClick: (String) -> Unit
) {
    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            //
            Row {
                Text(
                    text = "Semestres UNSA",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = onAddSemester,
                    colors = ButtonDefaults.buttonColors(containerColor = UnsaPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ Agregar Semestre")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(historyList) { semester ->
                    SemesterHistoryCard(semester, onClick = { onSemesterClick(semester.id) })
                }
            }
        }
    }
}

@Composable
fun SemesterHistoryCard(semester: SemesterHistoryUiModel, onClick: () -> Unit) {
    val backgroundColor = when {
        semester.failedCount > 0 -> StateFailingBg
        semester.average >= 14 -> StatePassingBg
        else -> StateWarningBg
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Click aquí
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(semester.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Aprobados:", fontSize = 14.sp)
                Text("${semester.approvedCount}", fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Desaprobados:", fontSize = 14.sp)
                Text("${semester.failedCount}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Text(String.format("%.1f", semester.average), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(semester.message, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val sampleList = listOf(
        SemesterHistoryUiModel("1", "Primer Semestre", 12, 0, 18.4, "Buen semestre, buenas notas", false),
        SemesterHistoryUiModel("2", "Tercer Semestre", 10, 3, 12.4, "Hubo problemas al desaprobar un curso", false),
        SemesterHistoryUiModel("3", "Cuarto Semestre", 10, 6, 8.3, "Semestre complicado", false)
    )

    SemesterHistoryContent(historyList = sampleList, onAddSemester = {}, onSemesterClick = {})
}