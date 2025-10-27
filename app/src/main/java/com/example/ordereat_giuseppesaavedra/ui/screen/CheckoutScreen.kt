package com.example.ordereat_giuseppesaavedra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ordereat_giuseppesaavedra.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CheckoutScreen(
    viewModel: MenuViewModel,
    onVolverAlCarrito: () -> Unit,
    onFinalizarPedido: () -> Unit
) {
    //Se consume el estado del ViewModel.
    val nombre by viewModel.nombre.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val telefono by viewModel.telefono.collectAsState()

    //Se consume el estado de error.
    val nombreError by viewModel.nombreError.collectAsState()
    val direccionError by viewModel.direccionError.collectAsState()
    val telefonoError by viewModel.telefonoError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos de Entrega") },
                navigationIcon = {
                    IconButton(onClick = onVolverAlCarrito) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al Carrito")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Por favor, completa tus datos para finalizar el pedido.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Campo Nombre.
            OutlinedTextField(
                value = nombre,
                onValueChange = viewModel::onNombreChange, // Referencia al método del ViewModel
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
                supportingText = {
                    if (nombreError != null) Text(nombreError!!)
                },
                singleLine = true
            )

            //Campo Dirección.
            OutlinedTextField(
                value = direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección de Entrega") },
                modifier = Modifier.fillMaxWidth(),
                // --- VINCULACIÓN A ERROR STATEFLOW ---
                isError = direccionError != null,
                supportingText = {
                    if (direccionError != null) Text(direccionError!!)
                },
                singleLine = true
            )

            //Campo Teléfono.
            OutlinedTextField(
                value = telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Teléfono de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                isError = telefonoError != null,
                supportingText = {
                    if (telefonoError != null) Text(telefonoError!!)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            //Botón Finalizar.
            Button(
                onClick = {
                    //La lógica de negocio (validación) se ejecuta en el ViewModel.
                    if (viewModel.validarCheckout()) {
                        onFinalizarPedido() // Navega si es válido.
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Finalizar Pedido")
            }
        }
    }
}