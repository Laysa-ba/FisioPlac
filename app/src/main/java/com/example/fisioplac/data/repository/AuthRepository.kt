package com.example.fisioplac.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Classe para encapsular o resultado das operações
// Usar a classe Result nativa do Kotlin é uma boa prática
typealias LoginResult = Result<String> // Onde String é o nome do Doutor

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Verifica se o usuário atual está logado no Firebase Auth.
     */
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Retorna o UID do usuário logado.
     */
    fun getCurrentUid(): String? = auth.currentUser?.uid

    /**
     * Tenta autenticar o usuário e, se bem-sucedido, busca o nome no Firestore.
     * Esta função é "suspensa", ou seja, deve ser chamada de uma Coroutine.
     */
    suspend fun loginAndFetchDoctorName(cpf: String, pass: String): LoginResult {
        return try {
            // 1. Autenticação
            val email = "$cpf@fisioplac.com"
            auth.signInWithEmailAndPassword(email, pass).await() // .await() espera o resultado

            // 2. Buscar UID (agora temos certeza que não é nulo)
            val uid = getCurrentUid()!! // !! é seguro aqui, pois o login acabou de acontecer

            // 3. Buscar dados no Firestore
            fetchDoctorName(uid)
        } catch (e: Exception) {
            // Captura qualquer erro (login falhou, rede, etc.)
            Result.failure(e)
        }
    }

    /**
     * Busca o nome do doutor no Firestore usando um UID.
     */
    suspend fun fetchDoctorName(uid: String): LoginResult {
        return try {
            val document = db.collection("doutores").document(uid).get().await()
            if (document.exists()) {
                val doctorName = document.getString("nome") ?: "Doutor(a)"
                Result.success(doctorName)
            } else {
                Result.failure(Exception("Dados do doutor não encontrados no banco."))
            }
        } catch (e: Exception) {
            // Captura erros do Firestore (permissão, rede, etc.)
            Result.failure(e)
        }
    }
}