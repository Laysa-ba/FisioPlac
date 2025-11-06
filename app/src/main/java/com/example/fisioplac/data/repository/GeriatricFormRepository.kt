package com.example.fisioplac.data.repository

import com.example.fisioplac.data.model.GeriatricFicha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GeriatricFormRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val medicoId: String get() = auth.currentUser?.uid ?: ""

    /**
     * Cria um *novo* documento de ficha no Firestore.
     * @return O ID do novo documento criado.
     */
    suspend fun createNewFicha(pacienteId: String, pacienteNome: String?): Result<String> {
        return try {
            val ficha = GeriatricFicha(
                pacienteId = pacienteId,
                medicoId = medicoId,
                nome = pacienteNome ?: "" // Pré-preenche o nome
            )

            val documentRef = db.collection("fichas")
                .add(ficha)
                .await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza uma ficha existente no Firestore com os dados da Etapa 1.
     */
    suspend fun saveStep1Data(formId: String, data: GeriatricFicha): Result<Unit> {
        return try {
            // Mapeia todos os campos da Etapa 1 para atualização
            val updates = mapOf(
                "dataAvaliacao" to data.dataAvaliacao,
                "estagiario" to data.estagiario,
                "nome" to data.nome,
                "dataNascimento" to data.dataNascimento,
                "idade" to data.idade,
                "sexo" to data.sexo, // <-- CAMPO ADICIONADO
                "telefone" to data.telefone,
                "estadoCivil" to data.estadoCivil,
                "escolaridade" to data.escolaridade,
                "localResidencia" to data.localResidencia,
                "moraCom" to data.moraCom,
                "renda" to data.renda,
                "queixaPrincipal" to data.queixaPrincipal, // <-- CAMPO ADICIONADO
                "praticaAtividadeFisica" to data.praticaAtividadeFisica,
                "diasPorSemana" to data.diasPorSemana,
                "frequenciaSair" to data.frequenciaSair, // <-- CAMPO ADICIONADO
                "atividadeSocial" to data.atividadeSocial,
                "doencasAssociadas" to data.doencasAssociadas,
                "dataUltimaAtualizacao" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("fichas").document(formId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}