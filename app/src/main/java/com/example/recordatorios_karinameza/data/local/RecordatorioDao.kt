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
        WHERE propietarioUid = :uid
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY id DESC
    """)
    fun observarTodosPorUsuario(uid: String): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE id = :id
        AND propietarioUid = :uid
        AND eliminadoPendienteSincronizacion = 0
    """)
    suspend fun obtenerPorIdYUsuario(
        id: Int,
        uid: String
    ): RecordatorioEntity?

    @Query("""
        SELECT * FROM recordatorios
        WHERE estado = :estado
        AND propietarioUid = :uid
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY id DESC
    """)
    fun observarPorEstadoYUsuario(
        estado: String,
        uid: String
    ): Flow<List<RecordatorioEntity>>


    @Query("""
        SELECT * FROM recordatorios
        WHERE pendienteSincronizacion = 1
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY fechaActualizacion ASC
    """)
    fun observarPendientesSincronizacion(): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE propietarioUid = :uid
        AND pendienteSincronizacion = 1
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY fechaActualizacion ASC
    """)
    fun observarPendientesPorUsuario(
        uid: String
    ): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE eliminadoPendienteSincronizacion = 1
        ORDER BY fechaActualizacion ASC
    """)
    fun observarEliminacionesPendientes(): Flow<List<RecordatorioEntity>>

    @Query("""
        SELECT * FROM recordatorios
        WHERE propietarioUid = :uid
        AND eliminadoPendienteSincronizacion = 1
        ORDER BY fechaActualizacion ASC
    """)
    fun observarEliminacionesPendientesPorUsuario(
        uid: String
    ): Flow<List<RecordatorioEntity>>

    @Query("""
    SELECT * FROM recordatorios
    WHERE propietarioUid = :uid
""")
    suspend fun obtenerTodosPorUsuario(
        uid: String
    ): List<RecordatorioEntity>

    @Query("""
    DELETE FROM recordatorios
    WHERE id = :id
    AND propietarioUid = :uid
    AND pendienteSincronizacion = 0
    AND eliminadoPendienteSincronizacion = 0
""")
    suspend fun eliminarObsoletoPorUsuario(
        id: Int,
        uid: String
    ): Int

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
        UPDATE recordatorios
        SET eliminadoPendienteSincronizacion = 1,
            pendienteSincronizacion = 1,
            fechaActualizacion = :fechaActualizacion
        WHERE id = :id
        AND propietarioUid = :uid
    """)
    suspend fun marcarComoEliminacionPendientePorUsuario(
        id: Int,
        uid: String,
        fechaActualizacion: Long
    ): Int

    @Query("""
        DELETE FROM recordatorios
        WHERE id = :id
    """)
    suspend fun eliminarPorId(id: Int): Int

    @Query("""
        DELETE FROM recordatorios
        WHERE id = :id
        AND propietarioUid = :uid
    """)
    suspend fun eliminarPorIdYUsuario(
        id: Int,
        uid: String
    ): Int

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
        UPDATE recordatorios
        SET identificadorRemoto = :identificadorRemoto,
            pendienteSincronizacion = 0
        WHERE id = :id
        AND propietarioUid = :uid
        AND fechaActualizacion = :fechaActualizacion
        AND eliminadoPendienteSincronizacion = 0
    """)
    suspend fun marcarComoSincronizadoPorUsuario(
        id: Int,
        uid: String,
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

    @Query("""
        SELECT * FROM recordatorios
        WHERE identificadorCliente = :identificadorCliente
        AND propietarioUid = :uid
        LIMIT 1
    """)
    suspend fun obtenerPorIdentificadorClienteYUsuario(
        identificadorCliente: String,
        uid: String
    ): RecordatorioEntity?

    @Query("""
        SELECT * FROM recordatorios
        WHERE propietarioUid IS NULL
        AND eliminadoPendienteSincronizacion = 0
        ORDER BY id DESC
    """)
    fun observarRegistrosSinPropietario(): Flow<List<RecordatorioEntity>>

    @Query("""
        UPDATE recordatorios
        SET propietarioUid = :uid,
            pendienteSincronizacion = 1,
            fechaActualizacion = :fechaActualizacion
        WHERE propietarioUid IS NULL
        AND id = :id
    """)
    suspend fun asignarPropietario(
        id: Int,
        uid: String,
        fechaActualizacion: Long
    ): Int
}
