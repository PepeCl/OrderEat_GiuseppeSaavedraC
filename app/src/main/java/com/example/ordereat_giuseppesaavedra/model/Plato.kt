package com.example.ordereat_giuseppesaavedra.model

//Definimos una clase para nuestro Plato.
data class Plato(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int
)