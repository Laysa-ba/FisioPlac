package com.example.fisioplac.data.repository

import com.example.fisioplac.data.model.GeriatricFicha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.firestore.FieldValue // Não é mais necessário para o .update()
import kotlinx.coroutines.tasks.await

class GeriatricFormRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Pega o ID do médico logado
    private val medicoId: String get() = auth.currentUser?.uid ?: ""

    /**
     * --- ARQUITETURA ATUALIZADA (Sua Sugestão) ---
     * Salva os dados da ficha como um NOVO documento em uma
     * subcoleção 'fichas_geriatricas' dentro do paciente.
     */
    suspend fun saveFichaData(pacienteId: String, data: GeriatricFicha): Result<Unit> {
        return try {
            // 1. Define o caminho para a nova subcoleção
            val subcollectionRef = db.collection("pacientes")
                .document(pacienteId)
                .collection("fichas_geriatricas")

            // 2. Adiciona os metadados (quem salvou)
            // (dataCriacao será adicionado pelo @ServerTimestamp)
            val dataToSave = data.copy(
                medicoId = this.medicoId
            )

            // 3. Adiciona o novo documento na subcoleção
            subcollectionRef.add(dataToSave).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}