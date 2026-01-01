package com.example.unsagrades.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnsaTopBar(
    userName: String = "Dante" // Placeholder: Luego lo conectaremos a una preferencia de usuario
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Default.AccountCircle,
//                    contentDescription = "Perfil de usuario",
//                    //tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(48.dp)
//                )
//                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Hola, ",
                        style = MaterialTheme.typography.titleMedium,
                        //color = MaterialTheme.colorScheme.onSurface,
                        //modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        //color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
        // Si quisieras agregar un botón de perfil o menú a la derecha, iría aquí en 'actions'
    )
}