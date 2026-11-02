package com.example.recordatorios_karinameza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.Estado
import com.example.recordatorios_karinameza.data.RecordatorioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repository: RecordatorioRepository
) : ViewModel() {

    private val _listadoUiState =
        MutableStateFlow<ListadoUiState>(ListadoUiState.Cargando)

    val listadoUiState: StateFlow<ListadoUiState> =
        _listadoUiState.asStateFlow()

    private val _formularioUiState =
        MutableStateFlow(FormularioUiState())

    val formularioUiState: StateFlow<FormularioUiState> =
        _formularioUiState.asStateFlow()

    private val _detalleUiState =
        MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)

    val detalleUiState: StateFlow<DetalleUiState> =
        _detalleUiState.asStateFlow()

    init {
        cargarRecordatorios()
    }

    fun cargarRecordatorios() {
        viewModelScope.launch {
            _listadoUiState.value = ListadoUiState.Cargando

            try {
                val datos = repository.obtenerRecordatorios()

                _listadoUiState.value =
                    if (datos.isEmpty()) {
                        ListadoUiState.Vacio
                    } else {
                        ListadoUiState.ConDatos(datos)
                    }

            } catch (e: Exception) {
                _listadoUiState.value =
                    ListadoUiState.Error(
                        "No se pudieron cargar los recordatorios"
                    )
            }
        }
    }

    fun cargarDetalle(id: Int) {
        viewModelScope.launch {
            _detalleUiState.value = DetalleUiState.Cargando

            try {
                val recordatorio =
                    repository.obtenerRecordatorioPorId(id)

                _detalleUiState.value =
                    if (recordatorio != null) {
                        DetalleUiState.ConDatos(recordatorio)
                    } else {
                        DetalleUiState.NoEncontrado
                    }

            } catch (e: Exception) {
                _detalleUiState.value =
                    DetalleUiState.Error(
                        "No se pudo cargar el recordatorio"
                    )
            }
        }
    }

    fun actualizarTitulo(valor: String) {
        _formularioUiState.update {
            it.copy(
                titulo = valor,
                errorTitulo = null,
                errorGeneral = null
            )
        }
    }

    fun actualizarDescripcion(valor: String) {
        _formularioUiState.update {
            it.copy(
                descripcion = valor,
                errorDescripcion = null,
                errorGeneral = null
            )
        }
    }

    fun actualizarCategoria(
        valor: com.example.recordatorios_karinameza.Categoria
    ) {
        _formularioUiState.update {
            it.copy(
                categoria = valor,
                errorGeneral = null
            )
        }
    }

    fun actualizarPrioridad(
        valor: com.example.recordatorios_karinameza.Prioridad
    ) {
        _formularioUiState.update {
            it.copy(
                prioridad = valor,
                errorGeneral = null
            )
        }
    }

    fun guardarRecordatorio() {
        val formulario = _formularioUiState.value
        val tituloLimpio = formulario.titulo.trim()

        val errorTitulo = when {
            tituloLimpio.length < 4 ->
                "El título debe tener al menos 4 caracteres"
            else -> null
        }

        val errorDescripcion = when {
            formulario.descripcion.length > 200 ->
                "La descripción no puede superar los 200 caracteres"
            else -> null
        }

        _formularioUiState.update {
            it.copy(
                errorTitulo = errorTitulo,
                errorDescripcion = errorDescripcion
            )
        }

        if (
            errorTitulo != null ||
            errorDescripcion != null ||
            formulario.categoria == null ||
            formulario.prioridad == null
        ) {
            return
        }

        viewModelScope.launch {
            _formularioUiState.update {
                it.copy(
                    enviando = true,
                    errorGeneral = null
                )
            }

            try {
                val nuevo = Recordatorio(
                    id = 0,
                    titulo = tituloLimpio,
                    descripcion = formulario.descripcion.trim()
                        .ifBlank { null },
                    categoria = formulario.categoria,
                    prioridad = formulario.prioridad,
                    estado = Estado.Pendiente
                )

                repository.guardarRecordatorio(nuevo)

                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        guardadoExitoso = true
                    )
                }

                cargarRecordatorios()

            } catch (e: Exception) {
                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        errorGeneral =
                            "No se pudo guardar el recordatorio"
                    )
                }
            }
        }
    }

    fun limpiarFormulario() {
        _formularioUiState.value = FormularioUiState()
    }
}