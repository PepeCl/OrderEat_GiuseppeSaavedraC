package com.example.ordereat_giuseppesaavedra.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ordereat_giuseppesaavedra.model.Plato
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


// Acá definimos la instancia de "Preferences DataStore" a nivel de Context.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "carrito_prefs")

// Gestiona la acción de persistencia del carrito cuando se cierra la app.
class CarritoDataStore(val context: Context) {

    companion object {
        // Definimos una "clave" para almacenar el carrito.
        val CARRITO_KEY = stringSetPreferencesKey("carrito_items")
    }

    // Acá leemos el estado del carrito. Retorna un "map".
    suspend fun leerCarrito(): Map<String, Int> {
        return context.dataStore.data
            .map { preferences ->
                // Lee el Set<String> o un set vacío.
                val setDeStrings = preferences[CARRITO_KEY] ?: emptySet()

                // Deserializa el set.
                setDeStrings
                    .mapNotNull { itemString ->
                        val partes = itemString.split(":")
                        if (partes.size == 2) {
                            partes[0] to (partes[1].toIntOrNull() ?: 0)
                        } else {
                            null // Ignora strings mal formados.
                        }
                    }
                    .toMap()
            }
            .first() // Emite solamente el valor actual.
    }

    //Convertimos a serie el estado actual de carrito y lo conserva.
    //Además, revibe el map de Plato desde el ViewModel.
    suspend fun guardarCarrito(carrito: Map<Plato, Int>) {
        // Serializa el Map<Plato, Int> a un Set<String>
        val setDeStrings = carrito.map { (plato, cantidad) ->
            "${plato.id}:$cantidad"
        }.toSet()

        // Guarda el set en DataStore
        context.dataStore.edit { preferences ->
            preferences[CARRITO_KEY] = setDeStrings
        }
    }

    //Limpia el carrito.
    suspend fun limpiarCarrito() {
        context.dataStore.edit { preferences ->
            preferences.remove(CARRITO_KEY)
        }
    }
}