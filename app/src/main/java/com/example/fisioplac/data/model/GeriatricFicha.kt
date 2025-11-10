package com.example.fisioplac.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa o modelo de dados completo para a ficha geriátrica.
 */
@Keep
data class GeriatricFicha(
    // Metadados (Preenchidos pelo Repositório)
    var medicoId: String = "",
    @ServerTimestamp
    var dataUltimaAtualizacao: Date? = null,

    // Dados da Etapa 1
    val pacienteId: String = "",
    val nome: String = "",
    val dataAvaliacao: String = "",
    val estagiario: String = "",
    val dataNascimento: String = "",
    val idade: String = "",
    val sexo: String? = null,
    val telefone: String = "",
    val estadoCivil: String = "",
    val escolaridade: String = "",
    val localResidencia: String = "",
    val moraCom: String = "",
    val renda: String = "",
    val queixaPrincipal: String = "",
    val outrasDoencas: String = "",

    // --- Social / Atividades (Etapa 1) ---
    val praticaAtividadeFisica: String? = null, // (Sim, Não)
    val diasPorSemana: String = "",
    val frequenciaSair: String? = null, // (Diariamente, Pouca frequência)
    val atividadeSocial: String = "",
    val doencasAssociadas: String = "",

    // --- CAMPO ADICIONADO PARA A ETAPA 2 ---
    val medicamentos: List<Medicamento> = emptyList()
)