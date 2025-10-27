package com.example.ordereat_giuseppesaavedra.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.regex.Pattern

class AuthViewModel : ViewModel() {

    //Estado de los campos del formulario
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    //Estado de errores para cada campo
    private val _nombreError = MutableStateFlow<String?>(null)
    val nombreError: StateFlow<String?> = _nombreError.asStateFlow()

    private val _correoError = MutableStateFlow<String?>(null)
    val correoError: StateFlow<String?> = _correoError.asStateFlow()

    private val _contrasenaError = MutableStateFlow<String?>(null)
    val contrasenaError: StateFlow<String?> = _contrasenaError.asStateFlow()

    // Estado de autenticación simulado
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    //Actualizador de campos
    fun onNombreChange(valor: String) {
        _nombre.value = valor
        if (_nombreError.value != null) _nombreError.value = null // Limpia error al escribir
    }

    fun onCorreoChange(valor: String) {
        _correo.value = valor
        if (_correoError.value != null) _correoError.value = null // Limpia error al escribir
    }

    fun onContrasenaChange(valor: String) {
        _contrasena.value = valor
        if (_contrasenaError.value != null) _contrasenaError.value = null // Limpia error al escribir
    }

    //Lógica de negocio

    // Función de validación de email
    private fun esCorreoValido(correo: String): Boolean {
        return Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        ).matcher(correo).matches()
    }

    //Definimos la lógica de validación.
    fun login(): Boolean {
        _correoError.value = when {
            _correo.value.isBlank() -> "El correo es requerido."
            !esCorreoValido(_correo.value) -> "El formato del correo no es válido."
            else -> null
        }
        _contrasenaError.value = if (_contrasena.value.isBlank()) "La contraseña es requerida." else null

        // Comprobamos si hay algún error.
        if (_correoError.value != null || _contrasenaError.value != null) {
            return false // Falla si hay errores
        }
        _isAuthenticated.value = true
        return true
    }

    fun register(): Boolean {
        _nombreError.value = if (_nombre.value.isBlank()) "El nombre es requerido." else null
        _correoError.value = when {
            _correo.value.isBlank() -> "El correo es requerido."
            !esCorreoValido(_correo.value) -> "El formato del correo no es válido."
            else -> null
        }
        _contrasenaError.value = when {
            _contrasena.value.isBlank() -> "La contraseña es requerida."
            _contrasena.value.length < 6 -> "Debe tener al menos 6 caracteres."
            else -> null
        }

        if (_nombreError.value != null || _correoError.value != null || _contrasenaError.value != null) {
            return false
        // Falla si hay errores
        }

        _isAuthenticated.value = true
        return true
    }

    fun logout() {
        _isAuthenticated.value = false
        _nombre.value = ""
        _correo.value = ""
        _contrasena.value = ""
        //Limpia los campos en caso de errores.
        _nombreError.value = null
        _correoError.value = null
        _contrasenaError.value = null
    }
}