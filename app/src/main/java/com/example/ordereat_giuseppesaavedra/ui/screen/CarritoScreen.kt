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

// Imports para animaciones de Compose
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

//Con esto habilitamos el uso de APIs experimentales de Material 3 y Animación
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CarritoScreen(
    viewModel: MenuViewModel,
    onVolverAlMenu: () -> Unit,
    onConfirmarPedido: () -> Unit
) {
    //Observa el estado del carrito. La UI se recompone automáticamente si cambia.
    val carritoItems by viewModel.carrito.collectAsState()
    val total by viewModel.total.collectAsState()

    //Creamos un estado de animación para una transición "suave".
    val totalAnimado by animateIntAsState(
        targetValue = total,
        label = "TotalAnimado"
    )
    //Definimos la estructura principal de la pantalla con Material 3.
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
                    //Mostramos el valor animado del total.
                    text = "Total a Pagar: $$totalAnimado",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onConfirmarPedido,
                    //El estado de este boton depende del estado del carrito.
                    enabled = carritoItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar Pedido")
                }
            }
        }
    ) { paddingValues ->
        //Definimos un contenedor para gestionar la visivilidad condicional de nuestro carrito.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Mostramos la lista solo si hay ítems con animación "fundido"
            AnimatedVisibility(
                visible = carritoItems.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //Renderizamos la lista de platos.
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                //Mostramos el estado "vacío" cuando la lista está vacía.
                AnimatedVisibility(
                    visible = carritoItems.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center) // Centra el texto
                ) {
                Text("Tu carrito está vacío.", style = MaterialTheme.typography.bodyLarge)
                }

            }
        }
    }

@Composable
//Definimos nuestro carrito con sus funciones de aumentar o disminuir items.
fun CarritoItem(
    plato: Plato,
    cantidad: Int,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit
) {
    //Damos los parámetros para mostrar nuestro carrito de forma ordenada.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(plato.nombre, style = MaterialTheme.typography.bodyLarge)
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