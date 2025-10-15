package com.example.ordereat_giuseppesaavedra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Imports de las capas corregidas (asumiendo paquetes en minúscula)
import com.example.ordereat_giuseppesaavedra.viewmodel.MenuViewModel
import com.example.ordereat_giuseppesaavedra.ui.screens.CarritoScreen
import com.example.ordereat_giuseppesaavedra.ui.screens.MenuScreen
// Este import ahora funcionará gracias a Color.kt y Type.kt
import com.example.ordereat_giuseppesaavedra.ui.theme.OrderEat_GiuseppeSaavedraTheme

// Rutas de Navegación
object Destinos {
    const val MENU = "menu_destino"
    const val CARRITO = "carrito_destino"
    const val CONFIRMACION = "confirmacion_destino"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderEat_GiuseppeSaavedraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MenuViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinos.MENU) {

        // DESTINO 1: Menú
        composable(Destinos.MENU) {
            MenuScreen(
                viewModel = viewModel,
                onVerCarrito = { navController.navigate(Destinos.CARRITO) }
            )
        }

        // DESTINO 2: Carrito
        composable(Destinos.CARRITO) {
            CarritoScreen(
                viewModel = viewModel,
                onVolverAlMenu = { navController.popBackStack() },
                onConfirmarPedido = {
                    navController.navigate(Destinos.CONFIRMACION)
                }
            )
        }

        // DESTINO 3: Confirmación
        composable(Destinos.CONFIRMACION) {
            val total = viewModel.calcularTotal() // Captura el total antes de vaciar

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("¡Pedido Enviado!", style = MaterialTheme.typography.headlineMedium)
                Text("Total: $$total", style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(24.dp)) // <-- ¡Corrección aquí!

                Button(onClick = {
                    viewModel.vaciarCarrito() // Vacía el carrito
                    navController.popBackStack(Destinos.MENU, inclusive = false)
                }) {
                    Text("Nuevo Pedido")
                }
            }
        }
    }
}
