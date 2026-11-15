package com.example.recordatorios_karinameza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios_karinameza.data.auth.AuthRepository
import com.example.recordatorios_karinameza.data.auth.UsuarioAutenticado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class AuthUiState(
    val cargando: Boolean = true,
    val usuario: UsuarioAutenticado? = null,
    val operacionEnCurso: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observarSesion()
    }

    private fun observarSesion() {
        viewModelScope.launch {
            authRepository.observarSesion().collect { usuario ->
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    usuario = usuario,
                    error = null
                )
            }
        }
    }

    fun registrar(email: String, password: String) {
        ejecutarAutenticacion {
            authRepository.registrar(email, password)
        }
    }

    fun iniciarSesion(email: String, password: String) {
        ejecutarAutenticacion {
            authRepository.iniciarSesion(email, password)
        }
    }

    fun cerrarSesion() {
        try {
            authRepository.cerrarSesion()
            _uiState.value = _uiState.value.copy(error = null)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "No se pudo cerrar la sesión."
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun ejecutarAutenticacion(
        accion: suspend () -> UsuarioAutenticado
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                operacionEnCurso = true,
                error = null
            )

            try {
                accion()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = obtenerMensajeError(e)
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    operacionEnCurso = false
                )
            }
        }
    }

    private fun obtenerMensajeError(error: Exception): String {
        val mensaje = error.message.orEmpty()

        return when {
            "email address is already in use" in mensaje ->
                "Ese correo electrónico ya está registrado."

            "password is invalid" in mensaje ||
                    "wrong-password" in mensaje ||
                    "invalid-credential" in mensaje ->
                "El correo o la contraseña son incorrectos."

            "no user record" in mensaje ||
                    "user-not-found" in mensaje ->
                "No existe una cuenta con ese correo."

            "email address is badly formatted" in mensaje ->
                "El formato del correo electrónico no es válido."

            "password is too weak" in mensaje ->
                "La contraseña es demasiado débil."

            "network" in mensaje.lowercase() ->
                "No se pudo conectar. Revisá tu conexión a internet."

            else ->
                "Ocurrió un error al autenticarte. Revisá los datos e intentá nuevamente."
        }
    }
}
