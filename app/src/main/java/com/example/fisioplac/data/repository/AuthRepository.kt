package com.example.fisioplac.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Tipo de alias para o resultado de login (da tela de Login)
typealias LoginResult = Result<String>

// NOVO: Data class para guardar os dados do perfil da Home
data class DoctorProfile(
    val name: String,
    val specialties: List<String> // Usar List em vez de ArrayList é mais idiomático
)

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
     * (Usado pela LoginActivity)
     */
    suspend fun loginAndFetchDoctorName(cpf: String, pass: String): LoginResult {
        return try {
            val email = "$cpf@fisioplac.com"
            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = getCurrentUid()!!
            fetchDoctorName(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca APENAS o nome do doutor no Firestore usando um UID.
     * (Usado pela LoginActivity)
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
            Result.failure(e)
        }
    }

    // --- FUNÇÕES NOVAS PARA A HOMEACTIVITY ---

    /**
     * Busca o perfil completo (nome e especialidades) do doutor.
     * (Usado pela HomeActivity)
     */
    suspend fun fetchDoctorProfile(uid: String): Result<DoctorProfile> {
        return try {
            val document = db.collection("doutores").document(uid).get().await()
            if (document.exists()) {
                val name = document.getString("nome") ?: "Doutor(a)"

                // CORREÇÃO DO WARNING:
                // Trocamos `as ArrayList<String>` por um "safe cast" `as? List<String>`.
                val specialties = document.get("especialidades") as? List<String> ?: emptyList()

                val profile = DoctorProfile(name = name, specialties = specialties)
                Result.success(profile)
            } else {
                Result.failure(Exception("Dados do doutor não encontrados."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Desloga o usuário do Firebase Auth.
     * (Usado pela HomeActivity)
     */
    fun logout() {
        auth.signOut()
    }
}