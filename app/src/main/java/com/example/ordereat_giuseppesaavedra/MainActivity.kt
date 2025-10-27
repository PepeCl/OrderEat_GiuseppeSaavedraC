package com.example.ordereat_giuseppesaavedra

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Imports de las vistas y viewmodel
import com.example.ordereat_giuseppesaavedra.ui.screens.LoginScreen
import com.example.ordereat_giuseppesaavedra.ui.screens.RegisterScreen
import com.example.ordereat_giuseppesaavedra.viewmodel.AuthViewModel
import com.example.ordereat_giuseppesaavedra.viewmodel.MenuViewModel
import com.example.ordereat_giuseppesaavedra.ui.screens.CarritoScreen
import com.example.ordereat_giuseppesaavedra.ui.screens.CheckoutScreen
import com.example.ordereat_giuseppesaavedra.ui.screens.MenuScreen
import com.example.ordereat_giuseppesaavedra.ui.theme.OrderEat_GiuseppeSaavedraTheme

//Definimos las rutas de navegación.
object Destinos {
    const val LOGIN = "login_destino"
    const val REGISTER = "register_destino"
    const val MENU = "menu_destino"
    const val CARRITO = "carrito_destino"
    const val CHECKOUT = "checkout_destino"
    const val CONFIRMACION = "confirmacion_destino"
}
// Entrada principal a nuestra aplicación.
// Configura el tema "raíz" de Material 3 y lanza la UI de Jetpack Compose.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderEat_GiuseppeSaavedraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigation()
                }
            }
        }
    }
}

//Gestión principal de la navegación de toda la aplicación.
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavigation(
    // Instanciamos los dos ViewModels.
    menuViewModel: MenuViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    // Definimos las animaciones de transición.
    val slideIn = slideInHorizontally { fullWidth -> fullWidth }
    val slideOut = slideOutHorizontally { fullWidth -> -fullWidth }
    val popIn = slideInHorizontally { fullWidth -> -fullWidth }
    val popOut = slideOutHorizontally { fullWidth -> fullWidth }

    // Contenedor principal de la navegación.
    NavHost(
        navController = navController,
        startDestination = Destinos.LOGIN, // <--- NUEVO PUNTO DE INICIO
        enterTransition = { slideIn },
        exitTransition = { slideOut },
        popEnterTransition = { popIn },
        popExitTransition = { popOut }
    ) {

        // Rutas de autenticación.
        composable(Destinos.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    // Navega al menú y limpia el stack de login.
                    navController.navigate(Destinos.MENU) {
                        popUpTo(Destinos.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Destinos.REGISTER)
                }
            )
        }

        composable(Destinos.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    // Navega al menú y limpia el stack de auth.
                    navController.navigate(Destinos.MENU) {
                        popUpTo(Destinos.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Vuelve a Login.
                }
            )
        }

        // Rutas principales de la app.
        composable(Destinos.MENU) {
            MenuScreen(
                viewModel = menuViewModel,
                onVerCarrito = { navController.navigate(Destinos.CARRITO) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Destinos.LOGIN) {
                        popUpTo(Destinos.MENU) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinos.CARRITO) {
            CarritoScreen(
                viewModel = menuViewModel,
                onVolverAlMenu = { navController.popBackStack() },
                onConfirmarPedido = {
                    navController.navigate(Destinos.CHECKOUT)
                }
            )
        }

        composable(Destinos.CHECKOUT) {
            CheckoutScreen(
                viewModel = menuViewModel,
                onVolverAlCarrito = { navController.popBackStack() },
                onFinalizarPedido = {
                    navController.navigate(Destinos.CONFIRMACION) {
                        popUpTo(Destinos.MENU)
                    }
                }
            )
        }

        composable(Destinos.CONFIRMACION) {
            ConfirmationScreenContent(viewModel = menuViewModel, onNewOrder = {
                menuViewModel.vaciarCarrito()
                navController.popBackStack(Destinos.MENU, inclusive = false)
            })
        }
    }
}


@Composable
fun ConfirmationScreenContent(
    viewModel: MenuViewModel,
    onNewOrder: () -> Unit
) {
    //Definimos el texto que aparecerá al compartir.
    val total = viewModel.total.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val context = LocalContext.current
    val textoParaCompartir = """
        ¡Hice un pedido en OrderEat!
        Total: $$total
        Cliente: $nombre
        Entrega: $direccion
    """.trimIndent()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Pedido Enviado!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
        Text("Dirección: $direccion", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        Text("Total: $$total", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onNewOrder) {
            Text("Nuevo Pedido")
        }
        Spacer(Modifier.height(8.dp))

        //Implementamos el recurso nativo, en este caso, el de Compartir Contenido.
        OutlinedButton(onClick = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textoParaCompartir)
                type = "text/plain"
            }
            //Creamos un "Chooser" para elegir entre distintas apps para compartir.
            val shareIntent = Intent.createChooser(sendIntent, "Compartir pedido")
            context.startActivity(shareIntent)
        }) {
            Icon(
                Icons.Filled.Share,
                contentDescription = "Compartir",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Compartir Pedido")
        }
    }
}