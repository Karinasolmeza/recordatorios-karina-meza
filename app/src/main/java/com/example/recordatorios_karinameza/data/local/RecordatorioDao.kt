package com.example.recordatorios_karinameza.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {

    @Query("""
        SELECT * FROM recordatorios
        WHERE eliminadoPendienteSincronizacion = 0
        ORDER BY id DESC
    """)
    fun observarTodos(): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE id = :id
        AND eliminadoPendienteSincronizacion = 0
    """)
    suspend fun obtenerPorId(id: Int): RecordatorioEntity?

    @Query("""
        SELECT * FROM recordatorios
        WHERE estado = :estado
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY id DESC
    """)
    fun observarPorEstado(estado: String): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE pendienteSincronizacion = 1
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY fechaActualizacion ASC
    """)
    fun observarPendientesSincronizacion(): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE eliminadoPendienteSincronizacion = 1
        ORDER BY fechaActualizacion ASC
    """)
    fun observarEliminacionesPendientes(): Flow<List<RecordatorioEntity>>

    @Insert
    suspend fun insertar(recordatorio: RecordatorioEntity): Long

    @Update
    suspend fun actualizar(recordatorio: RecordatorioEntity): Int

    @Query("""
        UPDATE recordatorios
        SET eliminadoPendienteSincronizacion = 1,
            pendienteSincronizacion = 1,
            fechaActualizacion = :fechaActualizacion
        WHERE id = :id
    """)
    suspend fun marcarComoEliminacionPendiente(
        id: Int,
        fechaActualizacion: Long
    ): Int

    @Query("""
        DELETE FROM recordatorios
        WHERE id = :id
    """)
    suspend fun eliminarPorId(id: Int): Int

    @Query("""
        UPDATE recordatorios
        SET identificadorRemoto = :identificadorRemoto,
            pendienteSincronizacion = 0
        WHERE id = :id
        AND fechaActualizacion = :fechaActualizacion
        AND eliminadoPendienteSincronizacion = 0
    """)
    suspend fun marcarComoSincronizado(
        id: Int,
        identificadorRemoto: String,
        fechaActualizacion: Long
    ): Int

    @Query("""
        SELECT * FROM recordatorios
        WHERE identificadorCliente = :identificadorCliente
        LIMIT 1
    """)
    suspend fun obtenerPorIdentificadorCliente(
        identificadorCliente: String
    ): RecordatorioEntity?

}
