package com.example.fisioplac.data.repository

// import com.example.fisioplac.data.model.DoctorProfile // <-- ESTA LINHA FOI REMOVIDA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Um data class para guardar o perfil do Doutor
// A classe está definida aqui, por isso não precisa de import.
data class DoctorProfile(
    val name: String,
    val specialties: List<String>
)

// Tipo de resultado para o login, que retorna o nome do doutor
typealias LoginResult = Result<String>

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
     * Faz o logout do usuário.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Tenta autenticar o usuário e, se bem-sucedido, busca o nome no Firestore.
     */
    suspend fun loginAndFetchDoctorName(cpf: String, pass: String): LoginResult {
        return try {
            val email = "$cpf@fisioplac.com"
            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = getCurrentUid()!! // Seguro, pois o login acabou de acontecer

            // Reutiliza a função de buscar perfil
            val profileResult = fetchDoctorProfile(uid)
            profileResult.map { it.name } // Retorna apenas o nome em caso de sucesso
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca o perfil completo (nome e especialidades) do doutor no Firestore.
     * Esta é a função que o SplashViewModel e o HomeViewModel usarão.
     */
    suspend fun fetchDoctorProfile(uid: String): Result<DoctorProfile> {
        return try {
            val document = db.collection("doutores").document(uid).get().await()
            if (document.exists()) {
                val name = document.getString("nome") ?: "Doutor(a)"

                // Cast seguro que corrige o warning (problema antigo)
                val specialties = document.get("especialidades") as? List<String> ?: emptyList()

                val profile = DoctorProfile(name = name, specialties = specialties)
                Result.success(profile)
            } else {
                Result.failure(Exception("Dados do doutor não encontrados no banco."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}