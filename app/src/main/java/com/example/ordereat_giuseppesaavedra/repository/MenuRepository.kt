package com.example.ordereat_giuseppesaavedra.repository

import com.example.ordereat_giuseppesaavedra.model.Plato

class MenuRepository {
    fun getPlatos(): List<Plato>{
        return listOf(
            Plato ("PP01","Ensalada César","Fresca ensalada con pollo a la parrilla y aderezo",11990),
            Plato ("PP02","Hamburguesa Clásica","Hamburguesa de carne con lechuga, tomate y queso",8990),
            Plato ("PP03","Pizza Pepperoni","Pizza con salsa de tomate, queso mozzarella y abuntante pepperoni",15990),
            Plato ("PP04","Pasta de la casa","Pasta tradicional con salsa bolognesa casera",12990)
        )
    }
}