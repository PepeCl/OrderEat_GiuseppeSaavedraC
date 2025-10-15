package com.example.ordereat_giuseppesaavedra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ordereat_giuseppesaavedra.model.Plato
import com.example.ordereat_giuseppesaavedra.viewmodel.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel, onVerCarrito: () -> Unit) {

    val platos by viewModel.platos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Menú del Restaurant", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(platos) { plato ->
                PlatoItem(
                    plato = plato,
                    onAgregarAlCarrito = { viewModel.agregarPlatoAlCarrito(plato) }
                )
            }
        }

        // Botón al pie de la pantalla
        Button(
            onClick = onVerCarrito,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Muestra la cantidad total de ítems en el carrito
            Text("Ver Carrito (${viewModel.carrito.collectAsState().value.values.sum()} ítems)")
        }
    }
}

@Composable
fun PlatoItem(plato: Plato, onAgregarAlCarrito: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(plato.nombre, style = MaterialTheme.typography.titleMedium)
            // PRECIO CORREGIDO: Muestra el Int directamente sin formato decimal
            Text("$${plato.precio}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Button(onClick = onAgregarAlCarrito) {
            Text("Añadir")
        }
    }
    Divider()
}