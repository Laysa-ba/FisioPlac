package com.example.fisioplac.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat // Precisamos disso para converter a String
import java.util.Calendar
import java.util.Locale

/**
 * Data class para guardar as informações do paciente.
 * Contém os dados puros, prontos para serem usados em qualquer tela.
 */
data class PatientProfile(
    val id: String,
    val name: String,
    val sex: String,
    val birthDate: String, // Formato "dd/MM/yyyy"
    val age: String,       // Apenas o número
    val estadoCivil: String?,
    val telefone: String?,
    val escolaridade: String?,
    val renda: String?,
    val localResidencia: String?,
    val moraCom: String?
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
     * *** FUNÇÃO CORRIGIDA PARA O CRASH ***
     */
    private fun formatPatientProfile(document: DocumentSnapshot): PatientProfile {
        // --- DADOS BÁSICOS (PUROS) ---
        val patientName = document.getString("nome") ?: "Nome não encontrado"
        val sex = document.getString("sexo") ?: "Não informado"

        // --- DADOS COMPLEMENTARES (PUROS) ---
        val estadoCivil = document.getString("estadoCivil")
        val telefone = document.getString("telefone")
        val escolaridade = document.getString("escolaridade")
        val renda = document.getString("renda")
        val localResidencia = document.getString("localResidencia")
        val moraCom = document.getString("moraCom")

        // --- CORREÇÃO APLICADA AQUI ---
        // 1. Lemos o campo como STRING, não como Timestamp
        val birthDateString = document.getString("dataNascimento")

        var formattedBirthDate = "" // Vazio por padrão
        var formattedAge = ""       // Vazio por padrão

        // 2. Verificamos se a string não é nula ou vazia
        if (!birthDateString.isNullOrBlank()) {

            // A data de nascimento para o formulário é a própria string
            formattedBirthDate = birthDateString

            try {
                // 3. Tentamos "converter" a string (ex: "20/05/1990") para um objeto Data
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
                val birthDate = sdf.parse(birthDateString) // Converte String para Date

                if (birthDate != null) {
                    // 4. Se der certo, calculamos a idade
                    val birthCal = Calendar.getInstance()
                    birthCal.time = birthDate
                    val todayCal = Calendar.getInstance()

                    var age = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
                    if (todayCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    formattedAge = age.toString() // Idade como dado puro
                }
            } catch (e: Exception) {
                // Se o formato da data no banco estiver errado (ex: "20-05-1990"), vai dar erro aqui
                Log.e("PatientRepository", "Falha ao converter dataNascimento: $birthDateString", e)
                formattedBirthDate = "Data Inválida"
                formattedAge = "??"
            }
        }
        // --- FIM DA CORREÇÃO ---

        // --- RETORNO CORRIGIDO ---
        return PatientProfile(
            id = document.id,
            name = patientName,
            sex = sex,
            birthDate = formattedBirthDate, // <-- Passando a string
            age = formattedAge,         // <-- Passando a idade calculada

            estadoCivil = estadoCivil,
            telefone = telefone,
            escolaridade = escolaridade,
            renda = renda,
            localResidencia = localResidencia,
            moraCom = moraCom
        )
    }
}