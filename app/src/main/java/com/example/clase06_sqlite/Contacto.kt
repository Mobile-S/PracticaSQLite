package com.example.clase06_sqlite

data class Contacto(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val celular: Long
)