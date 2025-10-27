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
    // Observamos los datos del formulario desde el ViewModel
    val nombre by viewModel.nombre.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val telefono by viewModel.telefono.collectAsState()

    // Estados locales para manejar si se ha intentado enviar y si hay errores de validación.
    var isNombreError by remember { mutableStateOf(false) }
    var isDireccionError by remember { mutableStateOf(false) }
    var isTelefonoError by remember { mutableStateOf(false) }

    fun validarFormulario(): Boolean {
        // Ejecutamos la validación.
        isNombreError = nombre.isBlank()
        isDireccionError = direccion.isBlank()
        isTelefonoError = telefono.length < 9 // Ejemplo: teléfono debe tener al menos 9 dígitos

        // Retorna true si no hay errores.
        return !isNombreError && !isDireccionError && !isTelefonoError
    }
    //Definimos la estructura de la pantalla con barra superior.
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

            // Campo Nombre con verificación dicho campo.
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    viewModel.onNombreChange(it)
                    if (isNombreError) isNombreError = it.isBlank() // Re-valida al escribir
                },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = isNombreError,
                supportingText = {
                    if (isNombreError) Text("El nombre es requerido")
                },
                singleLine = true
            )

            // Campo Dirección con verificación dicho campo.
            OutlinedTextField(
                value = direccion,
                onValueChange = {
                    viewModel.onDireccionChange(it)
                    if (isDireccionError) isDireccionError = it.isBlank()
                },
                label = { Text("Dirección de Entrega") },
                modifier = Modifier.fillMaxWidth(),
                isError = isDireccionError,
                supportingText = {
                    if (isDireccionError) Text("La dirección es requerida")
                },
                singleLine = true
            )

            // Campo Teléfono con verificación dicho campo.
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    viewModel.onTelefonoChange(it.filter { c -> c.isDigit() }) // Solo números
                    if (isTelefonoError) isTelefonoError = it.length < 9
                },
                label = { Text("Teléfono de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                isError = isTelefonoError,
                supportingText = {
                    if (isTelefonoError) Text("Debe tener al menos 9 dígitos")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

            // Botón para finalizar.
            Button(
                onClick = {
                    if (validarFormulario()) {
                        // Si la validación es exitosa entonces navega.
                        onFinalizarPedido()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Finalizar Pedido")
            }
        }
    }
}