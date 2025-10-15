package com.example.ordereat_giuseppesaavedra.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ordereat_giuseppesaavedra.model.Plato
import com.example.ordereat_giuseppesaavedra.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel: ViewModel() {
    private val repository = MenuRepository()

    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos : StateFlow<List<Plato>> = _platos.asStateFlow()

    // Carrito: Mapea Plato a Cantidad (Int)
    private val _carrito = MutableStateFlow<Map<Plato, Int>>(emptyMap())
    val carrito: StateFlow<Map<Plato, Int>> = _carrito.asStateFlow()

    init{
        _platos.value = repository.getPlatos()
    }

    fun agregarPlatoAlCarrito(plato : Plato){
        _carrito.update { currentMap ->
            val cantidadActual = currentMap[plato] ?: 0
            currentMap + (plato to cantidadActual + 1)
        }
    }

    fun eliminarPlatoDelCarrito(plato : Plato){
        _carrito.update { currentMap ->
            val cantidadActual = currentMap[plato] ?: 0
            if (cantidadActual > 1){
                currentMap + (plato to cantidadActual - 1)
            } else {
                currentMap - plato // Elimina completamente si la cantidad es 1
            }
        }
    }

    fun vaciarCarrito(){
        _carrito.value = emptyMap()
    }

    // Ajustado para retornar un Int (el total en pesos chilenos)
    fun calcularTotal(): Int {
        return _carrito.value.entries.sumOf { (plato, cantidad) ->
            plato.precio * cantidad
        }
    }
}