
package com.example.recordatorios_karinameza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Estado
import com.example.recordatorios_karinameza.Prioridad
import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.data.OrdenamientoRecordatorios
import com.example.recordatorios_karinameza.data.PreferenciasAplicacion
import com.example.recordatorios_karinameza.data.PreferenciasRepository
import com.example.recordatorios_karinameza.data.RecordatorioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repository: RecordatorioRepository,
    private val preferenciasRepository: PreferenciasRepository,
    private val programarSincronizacion: () -> Unit
) : ViewModel() {

    private var recordatoriosOriginales: List<Recordatorio> = emptyList()
    private var observarRecordatoriosJob: Job? = null
    private var recordatorioEnEdicion: Recordatorio? = null

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

    private val _preferenciasUiState =
        MutableStateFlow(PreferenciasAplicacion())

    val preferenciasUiState: StateFlow<PreferenciasAplicacion> =
        _preferenciasUiState.asStateFlow()

    private val _crudUiState =
        MutableStateFlow<CrudUiState>(CrudUiState.Inicial)

    val crudUiState: StateFlow<CrudUiState> =
        _crudUiState.asStateFlow()

    init {
        cargarRecordatorios()

        viewModelScope.launch {
            preferenciasRepository.preferencias
                .catch {
                    emit(PreferenciasAplicacion())
                }
                .collect { preferencias ->
                    _preferenciasUiState.value = preferencias
                    aplicarPreferencias()
                }
        }
    }

    private fun aplicarPreferencias() {
        val preferencias = _preferenciasUiState.value

        val filtrados = if (preferencias.mostrarCompletados) {
            recordatoriosOriginales
        } else {
            recordatoriosOriginales.filter {
                it.estado != Estado.Completado
            }
        }

        val ordenados = when (preferencias.ordenamiento) {
            OrdenamientoRecordatorios.PRIORIDAD -> {
                filtrados.sortedBy {
                    when (it.prioridad) {
                        Prioridad.Alta -> 1
                        Prioridad.Media -> 2
                        Prioridad.Baja -> 3
                    }
                }
            }

            OrdenamientoRecordatorios.FECHA -> filtrados
        }

        _listadoUiState.value = if (ordenados.isEmpty()) {
            ListadoUiState.Vacio
        } else {
            ListadoUiState.ConDatos(ordenados)
        }
    }

    private fun solicitarSincronizacion() {
        runCatching {
            programarSincronizacion()
        }
    }

    fun actualizarTema(temaOscuro: Boolean) {
        viewModelScope.launch {
            try {
                preferenciasRepository.actualizarTema(temaOscuro)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _preferenciasUiState.value =
                    _preferenciasUiState.value.copy(
                        temaOscuro = temaOscuro
                    )
            }
        }
    }

    fun actualizarMostrarCompletados(mostrar: Boolean) {
        viewModelScope.launch {
            try {
                preferenciasRepository.actualizarMostrarCompletados(mostrar)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _preferenciasUiState.value =
                    _preferenciasUiState.value.copy(
                        mostrarCompletados = mostrar
                    )
                aplicarPreferencias()
            }
        }
    }

    fun actualizarOrdenamiento(
        ordenamiento: OrdenamientoRecordatorios
    ) {
        viewModelScope.launch {
            try {
                preferenciasRepository.actualizarOrdenamiento(ordenamiento)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _preferenciasUiState.value =
                    _preferenciasUiState.value.copy(
                        ordenamiento = ordenamiento
                    )
                aplicarPreferencias()
            }
        }
    }

    fun cargarRecordatorios() {
        observarRecordatoriosJob?.cancel()

        observarRecordatoriosJob = viewModelScope.launch {
            _listadoUiState.value = ListadoUiState.Cargando

            try {
                repository.observarRecordatorios().collect { lista ->
                    recordatoriosOriginales = lista
                    aplicarPreferencias()
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _listadoUiState.value = ListadoUiState.Error(
                    "No se pudieron cargar los recordatorios"
                )
            }
        }
    }

    fun cargarDetalle(id: Int) {
        viewModelScope.launch {
            _detalleUiState.value = DetalleUiState.Cargando

            try {
                val recordatorio = repository.obtenerRecordatorioPorId(id)

                _detalleUiState.value =
                    if (recordatorio != null) {
                        DetalleUiState.ConDatos(recordatorio)
                    } else {
                        DetalleUiState.NoEncontrado
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _detalleUiState.value = DetalleUiState.Error(
                    "No se pudo cargar el recordatorio"
                )
            }
        }
    }

    fun prepararEdicion(recordatorio: Recordatorio) {
        recordatorioEnEdicion = recordatorio

        _formularioUiState.value = FormularioUiState(
            titulo = recordatorio.titulo,
            descripcion = recordatorio.descripcion ?: "",
            categoria = recordatorio.categoria,
            prioridad = recordatorio.prioridad
        )
    }

    fun estaEditando(): Boolean {
        return recordatorioEnEdicion != null
    }

    fun actualizarRecordatorio(recordatorio: Recordatorio) {
        viewModelScope.launch {
            _crudUiState.value = CrudUiState.Procesando

            try {
                val actualizado =
                    repository.actualizarRecordatorio(recordatorio)

                solicitarSincronizacion()

                _detalleUiState.value =
                    DetalleUiState.ConDatos(actualizado)

                _crudUiState.value = CrudUiState.Exito

            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _crudUiState.value = CrudUiState.Error(
                    "No se pudo actualizar el recordatorio"
                )
            }
        }
    }

    fun eliminarRecordatorio(id: Int) {
        viewModelScope.launch {
            _crudUiState.value = CrudUiState.Procesando

            try {
                val eliminado = repository.eliminarRecordatorio(id)

                if (eliminado) {
                    solicitarSincronizacion()

                    _crudUiState.value = CrudUiState.Exito
                    _detalleUiState.value = DetalleUiState.NoEncontrado
                } else {
                    _crudUiState.value = CrudUiState.Error(
                        "No se encontró el recordatorio para eliminar"
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _crudUiState.value = CrudUiState.Error(
                    "No se pudo eliminar el recordatorio"
                )
            }
        }
    }

    fun limpiarCrudUiState() {
        _crudUiState.value = CrudUiState.Inicial
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

    fun actualizarCategoria(valor: Categoria) {
        _formularioUiState.update {
            it.copy(
                categoria = valor,
                errorGeneral = null
            )
        }
    }

    fun actualizarPrioridad(valor: Prioridad) {
        _formularioUiState.update {
            it.copy(
                prioridad = valor,
                errorGeneral = null
            )
        }
    }

    fun guardarRecordatorio() {
        val formulario = _formularioUiState.value

        if (formulario.enviando) return

        val tituloLimpio = formulario.titulo.trim()
        val descripcionLimpia = formulario.descripcion.trim()

        val categoria = formulario.categoria
        val prioridad = formulario.prioridad

        val errorTitulo = when {
            tituloLimpio.length < 4 ->
                "El título debe tener al menos 4 caracteres"
            else -> null
        }

        val errorDescripcion = when {
            descripcionLimpia.length > 200 ->
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
            categoria == null ||
            prioridad == null
        ) {
            return
        }

        viewModelScope.launch {
            _formularioUiState.update {
                it.copy(
                    enviando = true,
                    errorGeneral = null,
                    guardadoExitoso = false
                )
            }

            try {
                val existente = recordatorioEnEdicion

                val recordatorio = if (existente != null) {
                    existente.copy(
                        titulo = tituloLimpio,
                        descripcion = descripcionLimpia.ifBlank { null },
                        categoria = categoria,
                        prioridad = prioridad
                    )
                } else {
                    Recordatorio(
                        id = 0,
                        titulo = tituloLimpio,
                        descripcion = descripcionLimpia.ifBlank { null },
                        categoria = categoria,
                        prioridad = prioridad,
                        estado = Estado.Pendiente
                    )
                }

                val guardado = if (existente != null) {
                    repository.actualizarRecordatorio(recordatorio)
                } else {
                    repository.guardarRecordatorio(recordatorio)
                }

                if (existente != null) {
                    _detalleUiState.value =
                        DetalleUiState.ConDatos(guardado)
                }

                recordatorioEnEdicion = null

                solicitarSincronizacion()

                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        guardadoExitoso = true,
                        errorGeneral = null
                    )
                }

            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        errorGeneral = "No se pudo guardar el recordatorio"
                    )
                }
            }
        }
    }

    fun limpiarFormulario() {
        recordatorioEnEdicion = null
        _formularioUiState.value = FormularioUiState()
    }
}
