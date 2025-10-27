package com.example.ordereat_giuseppesaavedra.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    //Estado de los campos del formulario.
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    // Autenticación simulada.
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    //Actualizador de campos.
    fun onNombreChange(valor: String) { _nombre.value = valor }
    fun onCorreoChange(valor: String) { _correo.value = valor }
    fun onContrasenaChange(valor: String) { _contrasena.value = valor }

    // Simulación.
    fun login(): Boolean {
        // Simulación: Inicia sesión si correo y contraseña no están vacíos.
        if (_correo.value.isNotBlank() && _contrasena.value.isNotBlank()) {
            _isAuthenticated.value = true
            return true
        }
        return false // Falla si están vacíos.
    }

    fun register(): Boolean {
        // Simulación: Registra si los tres campos no están vacíos.
        if (_nombre.value.isNotBlank() && _correo.value.isNotBlank() && _contrasena.value.isNotBlank()) {
            _isAuthenticated.value = true
            return true
        }
        return false // Falla si están vacíos.
    }

    fun logout() {
        // Limpia los campos y cierra la sesión.
        _isAuthenticated.value = false
        _nombre.value = ""
        _correo.value = ""
        _contrasena.value = ""
    }
}