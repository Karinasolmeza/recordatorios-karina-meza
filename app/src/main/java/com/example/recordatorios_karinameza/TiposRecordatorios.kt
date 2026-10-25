package com.example.recordatorios_karinameza

sealed class Categoria(val etiqueta: String) {
    data object Estudio : Categoria("Estudio")
    data object Academica : Categoria("Académica")
    data object Personal : Categoria("Personal")
}

sealed class Prioridad(val etiqueta: String) {
    data object Alta : Prioridad("Alta")
    data object Media : Prioridad("Media")
    data object Baja : Prioridad("Baja")
}

sealed class Estado(val etiqueta: String) {
    data object Pendiente : Estado("Pendiente")
    data object Completado : Estado("Completado")
}