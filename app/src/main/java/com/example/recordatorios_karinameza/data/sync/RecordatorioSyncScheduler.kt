package com.example.recordatorios_karinameza.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object RecordatorioSyncScheduler {

    fun programar(context: Context, uid: String) {
        if (uid.isBlank()) return

        val datos = Data.Builder()
            .putString(RecordatorioSyncWorker.CLAVE_UID, uid)
            .build()

        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val solicitud = OneTimeWorkRequestBuilder<RecordatorioSyncWorker>()
            .setInputData(datos)
            .setConstraints(restricciones)
            .setBackoffCriteria(
                androidx.work.BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreTrabajo(uid),
            ExistingWorkPolicy.KEEP,
            solicitud
        )
    }

    fun cancelar(context: Context, uid: String) {
        if (uid.isBlank()) return

        WorkManager.getInstance(context)
            .cancelUniqueWork(nombreTrabajo(uid))
    }

    private fun nombreTrabajo(uid: String): String {
        return "sincronizacion_recordatorios_$uid"
    }
}
