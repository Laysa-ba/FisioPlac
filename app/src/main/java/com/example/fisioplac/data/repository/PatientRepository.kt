package com.example.fisioplac.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Data class para guardar as informações do paciente.
 * Usar data classes é melhor do que passar o DocumentSnapshot.
 */
data class PatientProfile(
    val id: String,
    val name: String,
    val sex: String,
    val birthDate: String,
    val age: String
)

class PatientRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Busca um paciente pelo CPF.
     * Retorna um Result que pode conter o DocumentSnapshot ou um Erro.
     */
    private suspend fun searchPatientByCpf(cpf: String): Result<DocumentSnapshot> {
        return try {
            val documents = db.collection("pacientes")
                .whereEqualTo("cpf", cpf)
                .get()
                .await()

            if (documents.isEmpty) {
                Result.failure(Exception("Nenhum paciente encontrado com este CPF."))
            } else {
                Result.success(documents.first())
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE_ERROR", "Falha ao buscar paciente por CPF: ", e)
            Result.failure(e)
        }
    }

    /**
     * Verifica se existe um vínculo ativo entre o médico e o paciente.
     */
    private suspend fun checkPatientBond(
        doctorId: String,
        patientId: String,
        specialty: String
    ): Result<Boolean> {
        return try {
            val documents = db.collection("vinculos")
                .whereEqualTo("medicoId", doctorId)
                .whereEqualTo("pacienteId", patientId)
                .whereEqualTo("especialidade", specialty)
                .whereEqualTo("ativo", true)
                .get()
                .await()

            Result.success(!documents.isEmpty) // true se a lista NÃO estiver vazia
        } catch (e: Exception) {
            Log.e("FIRESTORE_ERROR", "Falha ao verificar vínculo: ", e)
            Result.failure(e)
        }
    }

    /**
     * Função principal que combina a busca e a checagem de vínculo.
     * Esta é a única função que o ViewModel vai chamar.
     */
    suspend fun findAndValidatePatient(cpf: String, specialty: String): Result<PatientProfile> {
        val doctorId = auth.currentUser?.uid
            ?: return Result.failure(Exception("Usuário não está logado."))

        // 1. Tenta buscar o paciente
        val patientResult = searchPatientByCpf(cpf)
        if (patientResult.isFailure) {
            return Result.failure(patientResult.exceptionOrNull()!!)
        }
        val patientDocument = patientResult.getOrThrow()
        val patientId = patientDocument.id

        // 2. Tenta checar o vínculo
        val bondResult = checkPatientBond(doctorId, patientId, specialty)
        if (bondResult.isFailure) {
            return Result.failure(bondResult.exceptionOrNull()!!)
        }

        // 3. Se o vínculo não for encontrado (false), retorna erro
        val hasBond = bondResult.getOrThrow()
        if (!hasBond) {
            return Result.failure(Exception("Paciente não vinculado a você para esta especialidade."))
        }

        // 4. Sucesso! Formata os dados do paciente e retorna
        return Result.success(formatPatientProfile(patientDocument))
    }

    /**
     * Converte o DocumentSnapshot em uma Data Class limpa.
     * Esta lógica foi movida da Activity para o Repository.
     */
    private fun formatPatientProfile(document: DocumentSnapshot): PatientProfile {
        val patientName = document.getString("nome") ?: "Nome não encontrado"
        val sex = document.getString("sexo") ?: "Não informado"
        val birthTimestamp = document.getTimestamp("dataNascimento")

        var formattedBirthDate = "Nascimento: Não informado"
        var formattedAge = "Idade: Não informada"

        if (birthTimestamp != null) {
            val birthDate = birthTimestamp.toDate()
            val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"))
            formattedBirthDate = "Nascimento: ${sdf.format(birthDate)}"

            val birthCal = Calendar.getInstance()
            birthCal.time = birthDate
            val todayCal = Calendar.getInstance()

            var age = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (todayCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            formattedAge = "Idade: $age anos"
        }

        return PatientProfile(
            id = document.id,
            name = patientName,
            sex = "Sexo: $sex",
            birthDate = formattedBirthDate,
            age = formattedAge
        )
    }
}