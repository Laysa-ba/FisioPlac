package com.example.fisioplac.data.repository

import com.example.fisioplac.data.model.GeriatricFicha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GeriatricFormRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val medicoId = auth.currentUser?.uid ?: "uid_desconhecido"

    /**
     * Salva a ficha completa como um novo documento na subcoleção do paciente.
     * Esta é a ÚNICA função de escrita.
     */
    suspend fun saveFinalFicha(pacienteId: String, data: GeriatricFicha): Result<Unit> {
        return try {
            // Garante que o medicoId está no objeto de dados
            val fichaCompleta = data.copy(medicoId = medicoId)

            // Adiciona o novo documento
            db.collection("pacientes").document(pacienteId)
                .collection("fichas_geriatricas")
                .add(fichaCompleta) // .add() cria um novo ID automático
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}