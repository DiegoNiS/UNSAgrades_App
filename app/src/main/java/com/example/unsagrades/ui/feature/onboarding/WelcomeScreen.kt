package com.example.unsagrades.ui.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsagrades.data.local.dao.UserDao
import com.example.unsagrades.domain.usecase.CalculateWeightedAverageUseCase
import com.example.unsagrades.ui.feature.profile.ProfileViewModel
import com.example.unsagrades.R
import com.example.unsagrades.domain.repository.GradeRepository

// Lista de avatares disponibles (Mapeados a IDs enteros 0, 1, 2...)
val AVAILABLE_AVATARS_RES = listOf(
    R.drawable.avatar_monster_1, // ID 0
    R.drawable.avatar_monster_2, // ID 1
    R.drawable.avatar_monster_3, // ID 2
    R.drawable.avatar_monster_4,  // ID 3
    R.drawable.avatar_monster_5,
    R.drawable.avatar_monster_6,
    R.drawable.avatar_monster_7,
    R.drawable.avatar_monster_8,
)

@Composable
fun WelcomeScreen(
    onNavigateToNewSemester: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel(),
    innerPadding : PaddingValues
) {
    val navigationEvent by viewModel.navigationEvent.collectAsState(initial = null)

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is WelcomeNavigationEvent.NavigateToNewSemester -> onNavigateToNewSemester()
            is WelcomeNavigationEvent.NavigateToDashboard -> onNavigateToDashboard()
            null -> {}
        }
    }

    WelcomeScreenContent(
        saveProfile = viewModel::saveProfile,
        innerPadding = innerPadding
    )
}

@Composable
fun WelcomeScreenContent(
    saveProfile : (String, Int) -> Unit,
    innerPadding : PaddingValues
) {
    var name by remember { mutableStateOf("") }
    // Guardamos el ÍNDICE del avatar seleccionado (0, 1, 2, 3)
    var selectedAvatarIndex by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TÍTULO Y BIENVENIDA
            Text(
                text = "¡Hola!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Antes de empezar a controlar tus notas, cuéntanos un poco sobre ti.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // SELECCIÓN DE AVATAR
            Text(
                text = "Elige tu avatar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                //agregar espacio verticalmente entre rows
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(AVAILABLE_AVATARS_RES.indices.toList()) { index ->
                    AvatarItem(
                        // --- CAMBIO 2: Pasamos el ID del recurso ---
                        resId = AVAILABLE_AVATARS_RES[index],
                        isSelected = selectedAvatarIndex == index,
                        onClick = { selectedAvatarIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // INPUT NOMBRE
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text("Tu Nombre") },
                placeholder = { Text("Ej. Dante") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(errorMessage!!)
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // BOTÓN CONTINUAR
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Por favor, ingresa tu nombre."
                    } else {
                        // Guardamos: Nombre y el ÍNDICE del avatar seleccionado
                        saveProfile(name, selectedAvatarIndex)
                        // La navegación ocurrirá via LaunchedEffect cuando el ViewModel emita el evento
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Comenzar", fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun AvatarItem(
    resId: Int, // <-- CAMBIO 3: Recibimos un Int (resource ID)
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    // Si quieres que los monstruos se pinten del color del tema (ej. morado), usa esto.
    // Si quieres que mantengan sus colores originales del SVG, usa null.
    val tintColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    // NOTA: Si tus SVGs ya tienen colores bonitos y no quieres que se pongan morados,
    // cambia la línea de abajo por: val colorFilter = null
    val colorFilter = ColorFilter.tint(tintColor)

    Box(
        modifier = Modifier
            .size(70.dp) // Un poco más grandes para que se luzcan
            .clip(CircleShape)
            .background(containerColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // --- CAMBIO 4: Usamos 'Image' y 'painterResource' en lugar de 'Icon' ---
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Avatar",
            //colorFilter = colorFilter, // Quita esto si quieres los colores originales del SVG
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    WelcomeScreenContent(
        saveProfile = { _, _ -> },
        innerPadding = PaddingValues(0.dp)
    )
}