package com.example.recordatorios_karinameza.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class UsuarioAutenticado(
    val uid: String,
    val email: String?
)

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val usuarioActual: UsuarioAutenticado?
        get() = firebaseAuth.currentUser?.toUsuarioAutenticado()

    fun observarSesion(): Flow<UsuarioAutenticado?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUsuarioAutenticado())
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    suspend fun registrar(
        email: String,
        password: String
    ): UsuarioAutenticado {
        val resultado = firebaseAuth
            .createUserWithEmailAndPassword(email.trim(), password)
            .await()

        return resultado.user?.toUsuarioAutenticado()
            ?: throw IllegalStateException("No se pudo crear el usuario.")
    }

    suspend fun iniciarSesion(
        email: String,
        password: String
    ): UsuarioAutenticado {
        val resultado = firebaseAuth
            .signInWithEmailAndPassword(email.trim(), password)
            .await()

        return resultado.user?.toUsuarioAutenticado()
            ?: throw IllegalStateException("No se pudo iniciar sesión.")
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUsuarioAutenticado() =
        UsuarioAutenticado(
            uid = uid,
            email = email
        )
}
