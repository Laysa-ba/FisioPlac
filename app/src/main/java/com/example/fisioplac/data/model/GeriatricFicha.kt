package com.example.fisioplac.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa o modelo de dados completo da ficha geriátrica.
 * (Atualizado para incluir todos os campos da Etapa 1)
 */
data class GeriatricFicha(
    // Metadados (Preenchidos pelo Repositório)
    var medicoId: String = "",
    @ServerTimestamp
    var dataCriacao: Date? = null,

    // Metadados (Preenchidos pela Activity)
    val pacienteId: String = "", // Este é o ID do paciente
    val nome: String = "", // Este é o nome do paciente

    // --- Dados da Etapa 1 ---
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
    val outrasDoencas: String = "", // <-- Campo que faltava

    // --- Social / Atividades ---
    val praticaAtividadeFisica: String? = null, // (Sim, Não)
    val diasPorSemana: String = "",
    val frequenciaSair: String? = null, // (Diariamente, Pouca frequência)
    val atividadeSocial: String = "",
    val doencasAssociadas: String = ""
)