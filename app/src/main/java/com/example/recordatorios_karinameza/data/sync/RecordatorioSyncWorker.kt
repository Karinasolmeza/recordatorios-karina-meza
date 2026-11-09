package com.example.recordatorios_karinameza.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recordatorios_karinameza.data.OfflineFirstRecordatorioRepository
import com.example.recordatorios_karinameza.data.RoomRecordatorioRepository
import com.example.recordatorios_karinameza.data.local.RecordatoriosDatabase
import com.example.recordatorios_karinameza.data.remote.RemoteRecordatorioRepository
import com.example.recordatorios_karinameza.data.remote.RetrofitClient
import kotlinx.coroutines.CancellationException

class RecordatorioSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = RecordatoriosDatabase.obtenerInstancia(
                applicationContext
            )

            val dao = database.recordatorioDao()

            val repositoryLocal = RoomRecordatorioRepository(dao)

            val repositoryRemoto = RemoteRecordatorioRepository(
                RetrofitClient.recordatorioApi
            )

            val repository = OfflineFirstRecordatorioRepository(
                local = repositoryLocal,
                dao = dao,
                remoto = repositoryRemoto
            )

            val resultado = repository.sincronizarPendientes()

            if (resultado.requiereReintento) {
                Result.retry()
            } else {
                Result.success()
            }

        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.retry()
        }
    }
}
