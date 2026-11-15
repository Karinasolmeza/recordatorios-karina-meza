package com.example.recordatorios_karinameza.data.sync

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import com.example.recordatorios_karinameza.data.OfflineFirstRecordatorioRepository
import com.example.recordatorios_karinameza.data.RoomRecordatorioRepository
import com.example.recordatorios_karinameza.data.local.RecordatoriosDatabase
import com.example.recordatorios_karinameza.data.remote.FirestoreRecordatorioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException

class RecordatorioSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = inputData.getString(CLAVE_UID)
            ?: return Result.failure()

        val usuarioActual = FirebaseAuth.getInstance().currentUser
            ?: return Result.success()

        if (usuarioActual.uid != uid) {
            return Result.success()
        }

        return try {
            val database = RecordatoriosDatabase.obtenerInstancia(applicationContext)
            val dao = database.recordatorioDao()

            val repositoryLocal = RoomRecordatorioRepository(
                dao = dao,
                uid = uid
            )

            val repositoryRemoto = FirestoreRecordatorioRepository()

            val repository = OfflineFirstRecordatorioRepository(
                local = repositoryLocal,
                dao = dao,
                remoto = repositoryRemoto,
                uid = uid
            )

            // Primero envía los cambios locales y luego descarga los remotos.
            val resultado = repository.sincronizarPendientes()
            repository.descargarDesdeFirestore()

            if (resultado.requiereReintento) {
                Result.retry()
            } else {
                Result.success()
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val CLAVE_UID = "usuario_uid"
    }
}
