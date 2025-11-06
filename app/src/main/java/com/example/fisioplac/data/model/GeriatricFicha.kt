package com.example.fisioplac.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representa o modelo de dados completo da ficha geriátrica.
 * Todos os campos de todas as etapas estarão aqui.
 */
data class GeriatricFicha(
    // Metadados
    val id: String = "",
    val pacienteId: String = "",
    val medicoId: String = "",
    @ServerTimestamp
    val dataCriacao: Date? = null,
    @ServerTimestamp
    val dataUltimaAtualizacao: Date? = null,

    // --- ETAPA 1: Identificação ---
    val dataAvaliacao: String = "",
    val estagiario: String = "",
    val nome: String = "",
    val dataNascimento: String = "",
    val idade: String = "",
    val sexo: String = "", // <-- CAMPO ADICIONADO
    val telefone: String = "",
    val estadoCivil: String = "",
    val escolaridade: String = "",
    val localResidencia: String = "",
    val moraCom: String = "",
    val renda: String = "", // A máscara salva como String (ex: "R$ 1.200,00")
    val queixaPrincipal: String = "", // <-- CAMPO ADICIONADO

    // --- ETAPA 1: Social ---
    val praticaAtividadeFisica: Boolean = false,
    val diasPorSemana: String = "",
    val frequenciaSair: String = "", // <-- CAMPO ADICIONADO
    val atividadeSocial: String = "",
    val doencasAssociadas: String = "",

    // --- ETAPA 2: (Campos Futuros) ---
    val diagnosticoFisioterapeutico: String? = null
    // ... adicione aqui os campos das outras 7 etapas
)