package com.example.ordereat_giuseppesaavedra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ordereat_giuseppesaavedra.model.Plato
import com.example.ordereat_giuseppesaavedra.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.ordereat_giuseppesaavedra.data.CarritoDataStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val menuRepository = MenuRepository()
    private val carritoDataStore = CarritoDataStore(this.getApplication())

    //Estado del menú del restaurant.
    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos: StateFlow<List<Plato>> = _platos.asStateFlow()

    //Estado del carrito.
    private val _carrito = MutableStateFlow<Map<Plato, Int>>(emptyMap())
    val carrito: StateFlow<Map<Plato, Int>> = _carrito.asStateFlow()

    //Estado del Check Out del pedido.
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()
    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()
    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    fun onNombreChange(nuevoNombre: String) { _nombre.value = nuevoNombre }
    fun onDireccionChange(nuevaDireccion: String) { _direccion.value = nuevaDireccion }
    fun onTelefonoChange(nuevoTelefono: String) { _telefono.value = nuevoTelefono }

   //Definimos un "StateFlow" que actualiza el valor del carrito.
    val total: StateFlow<Int> = _carrito.map { carritoMap ->
        // Esta es la misma lógica de tu función calcularTotal()
        carritoMap.entries.sumOf { (plato, cantidad) ->
            plato.precio * cantidad
        }
    }.stateIn(
        scope = viewModelScope, // Se ata al ciclo de vida del ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Inicia cuando la UI observa
        initialValue = 0 // Valor inicial
    )

    //Acá cargamos el carrito aunque se cierre la app.
    init {
        _platos.value = menuRepository.getPlatos()
        cargarCarritoGuardado()
    }

    //Restaura del carrito desde el DataStore.
    private fun cargarCarritoGuardado() {
        viewModelScope.launch {
            // Obtenemos todos los platos disponibles y los ponemos en un mapa por ID
            val mapaPlatos = menuRepository.getPlatos().associateBy { it.id }

            // Leemos el carrito guardado (Map<String, Int>)
            val carritoGuardado = carritoDataStore.leerCarrito()

            // Convertimos el Map<String, Int> a Map<Plato, Int>
            _carrito.value = carritoGuardado
                .mapNotNull { (id, cantidad) ->
                    mapaPlatos[id]?.to(cantidad) // Buscamos el Plato por su ID
                }
                .toMap()
        }
    }

    //Función para agregar plato al carrito de compras.
    fun agregarPlatoAlCarrito(plato: Plato) {
        _carrito.update { currentMap ->
            val cantidadActual = currentMap[plato] ?: 0
            currentMap + (plato to cantidadActual + 1)
        }
        //Guardamos el estado del carrito.
        persistirCarrito()
    }

    //Función para eliminar plato del carrito de compras.
    fun eliminarPlatoDelCarrito(plato: Plato) {
        _carrito.update { currentMap ->
            val cantidadActual = currentMap[plato] ?: 0
            if (cantidadActual > 1) {
                currentMap + (plato to cantidadActual - 1)
            } else {
                currentMap - plato
            }
        }
        //Guardamos el estado del carrito.
        persistirCarrito()
    }

    //Función para vaciar el carrito.
    fun vaciarCarrito() {
        _carrito.value = emptyMap()
        _nombre.value = ""
        _direccion.value = ""
        _telefono.value = ""

        //Limpiamos el carrito que persiste.
        viewModelScope.launch {
            carritoDataStore.limpiarCarrito()
        }
    }

    // Corrutina que guarda el estado actual del carrito en el DataStore.
    private fun persistirCarrito() {
        viewModelScope.launch {
            carritoDataStore.guardarCarrito(_carrito.value)
        }
    }
}