package com.example.recordatorios_karinameza

val recordatorios = listOf(
    Recordatorio(
        id = 1,
        titulo = "Entregar Api 1",
        descripcion = "Completar la entrega de programación mobile",
        categoria = Categoria.Estudio,
        prioridad = Prioridad.Alta,
        estado = Estado.Pendiente
    ),
    Recordatorio(
        id = 2,
        titulo = "Realizar Autoevaluación",
        descripcion = "Hacer la autoevaluación del módulo número 1",
        categoria = Categoria.Personal,
        prioridad = Prioridad.Media,
        estado = Estado.Pendiente
    ),
    Recordatorio(
        id = 3,
        titulo = "Charla PP",
        descripcion = "Asistir a clase de práctica profesional",
        categoria = Categoria.Academica,
        prioridad = Prioridad.Baja,
        estado = Estado.Completado
    )
)

val categoriasDisponibles = setOf(
    Categoria.Estudio,
    Categoria.Academica,
    Categoria.Personal
)

val etiquetasCategorias = mapOf(
    Categoria.Estudio to "Estudio",
    Categoria.Academica to "Académica",
    Categoria.Personal to "Personal"
)

val recordatoriosPendientes = recordatorios.filter {
    it.estado == Estado.Pendiente
}

val recordatoriosPrioridadAlta = recordatorios.filter {
    it.prioridad == Prioridad.Alta
}