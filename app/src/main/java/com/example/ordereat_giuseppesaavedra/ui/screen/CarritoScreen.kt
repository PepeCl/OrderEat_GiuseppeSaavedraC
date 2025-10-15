package com.example.ordereat_giuseppesaavedra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ordereat_giuseppesaavedra.model.Plato
import com.example.ordereat_giuseppesaavedra.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    viewModel: MenuViewModel,
    onVolverAlMenu: () -> Unit,
    onConfirmarPedido: () -> Unit
) {
    val carritoItems by viewModel.carrito.collectAsState()
    val total = viewModel.calcularTotal() // Ahora es Int

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Pedido") },
                navigationIcon = {
                    IconButton(onClick = onVolverAlMenu) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al Menú")
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    // PRECIO CORREGIDO: Muestra Int
                    text = "Total a Pagar: $$total",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onConfirmarPedido,
                    enabled = carritoItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar Pedido")
                }
            }
        }
    ) { paddingValues ->
        if (carritoItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                items(carritoItems.keys.toList()) { plato ->
                    CarritoItem(
                        plato = plato,
                        cantidad = carritoItems[plato] ?: 0,
                        onAumentar = { viewModel.agregarPlatoAlCarrito(plato) },
                        onDisminuir = { viewModel.eliminarPlatoDelCarrito(plato) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CarritoItem(
    plato: Plato,
    cantidad: Int,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(plato.nombre, style = MaterialTheme.typography.bodyLarge)
            // PRECIO CORREGIDO: Muestra Int
            Text("Precio: $${plato.precio}", style = MaterialTheme.typography.bodySmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDisminuir) {
                Icon(Icons.Filled.Remove, contentDescription = "Quitar")
            }
            Text("$cantidad", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onAumentar) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir")
            }
        }
    }
}