package com.example.recordatorios_karinameza.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(
    name = "preferencias_aplicacion"
)

enum class OrdenamientoRecordatorios {
    FECHA,
    PRIORIDAD
}

data class PreferenciasAplicacion(
    val temaOscuro: Boolean = true,
    val mostrarCompletados: Boolean = true,
    val ordenamiento: OrdenamientoRecordatorios =
        OrdenamientoRecordatorios.FECHA
)

class PreferenciasRepository(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
        private val MOSTRAR_COMPLETADOS =
            booleanPreferencesKey("mostrar_completados")
        private val ORDENAMIENTO = stringPreferencesKey("ordenamiento")
    }

    val preferencias: Flow<PreferenciasAplicacion> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferencias ->
                val ordenamientoGuardado =
                    preferencias[ORDENAMIENTO]

                PreferenciasAplicacion(
                    temaOscuro = preferencias[TEMA_OSCURO] ?: true,
                    mostrarCompletados =
                        preferencias[MOSTRAR_COMPLETADOS] ?: true,
                    ordenamiento = runCatching {
                        OrdenamientoRecordatorios.valueOf(
                            ordenamientoGuardado ?: "FECHA"
                        )
                    }.getOrDefault(OrdenamientoRecordatorios.FECHA)
                )
            }

    suspend fun actualizarTema(temaOscuro: Boolean) {
        dataStore.edit { preferencias ->
            preferencias[TEMA_OSCURO] = temaOscuro
        }
    }

    suspend fun actualizarMostrarCompletados(mostrar: Boolean) {
        dataStore.edit { preferencias ->
            preferencias[MOSTRAR_COMPLETADOS] = mostrar
        }
    }

    suspend fun actualizarOrdenamiento(
        ordenamiento: OrdenamientoRecordatorios
    ) {
        dataStore.edit { preferencias ->
            preferencias[ORDENAMIENTO] = ordenamiento.name
        }
    }
}

fun Context.obtenerPreferenciasRepository(): PreferenciasRepository {
    return PreferenciasRepository(dataStore)
}
