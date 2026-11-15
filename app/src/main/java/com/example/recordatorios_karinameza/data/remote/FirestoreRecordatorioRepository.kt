package com.example.recordatorios_karinameza.data.remote

import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Estado
import com.example.recordatorios_karinameza.Prioridad
import com.example.recordatorios_karinameza.Recordatorio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRecordatorioRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun coleccionRecordatorios(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("reminders")

    suspend fun obtenerRecordatorios(uid: String): List<Recordatorio> {
        require(uid.isNotBlank()) { "El usuario no es válido." }

        val resultado = coleccionRecordatorios(uid)
            .get()
            .await()

        return resultado.documents.mapNotNull { documento ->
            val datos = documento.data ?: return@mapNotNull null

            Recordatorio(
                id = 0,
                titulo = datos["titulo"] as? String ?: return@mapNotNull null,
                descripcion = datos["descripcion"] as? String,
                categoria = categoriaDesde(datos["categoria"] as? String),
                prioridad = prioridadDesde(datos["prioridad"] as? String),
                estado = estadoDesde(datos["estado"] as? String),
                fechaCreacion = (datos["fechaCreacion"] as? Number)?.toLong()
                    ?: System.currentTimeMillis(),
                fechaActualizacion = (datos["fechaActualizacion"] as? Number)?.toLong()
                    ?: System.currentTimeMillis(),
                identificadorCliente = documento.id,
                identificadorRemoto = documento.id,
                pendienteSincronizacion = false,
                eliminadoPendienteSincronizacion = false,
                propietarioUid = uid
            )
        }
    }

    suspend fun guardarRecordatorio(
        uid: String,
        recordatorio: Recordatorio
    ) {
        require(uid.isNotBlank()) { "El usuario no es válido." }
        require(recordatorio.identificadorCliente.isNotBlank()) {
            "El identificador del recordatorio no es válido."
        }

        val datos = hashMapOf(
            "titulo" to recordatorio.titulo,
            "descripcion" to recordatorio.descripcion,
            "categoria" to recordatorio.categoria.etiqueta,
            "prioridad" to recordatorio.prioridad.etiqueta,
            "estado" to recordatorio.estado.etiqueta,
            "fechaCreacion" to recordatorio.fechaCreacion,
            "fechaActualizacion" to recordatorio.fechaActualizacion,
            "identificadorCliente" to recordatorio.identificadorCliente
        )

        coleccionRecordatorios(uid)
            .document(recordatorio.identificadorCliente)
            .set(datos)
            .await()
    }

    suspend fun eliminarRecordatorio(
        uid: String,
        identificadorCliente: String
    ) {
        require(uid.isNotBlank()) { "El usuario no es válido." }
        require(identificadorCliente.isNotBlank()) {
            "El identificador del recordatorio no es válido."
        }

        coleccionRecordatorios(uid)
            .document(identificadorCliente)
            .delete()
            .await()
    }

    private fun categoriaDesde(valor: String?): Categoria =
        when (valor) {
            "Estudio" -> Categoria.Estudio
            "Académica" -> Categoria.Academica
            "Personal" -> Categoria.Personal
            else -> Categoria.Personal
        }

    private fun prioridadDesde(valor: String?): Prioridad =
        when (valor) {
            "Alta" -> Prioridad.Alta
            "Media" -> Prioridad.Media
            "Baja" -> Prioridad.Baja
            else -> Prioridad.Media
        }

    private fun estadoDesde(valor: String?): Estado =
        when (valor) {
            "Pendiente" -> Estado.Pendiente
            "Completado" -> Estado.Completado
            else -> Estado.Pendiente
        }
}
