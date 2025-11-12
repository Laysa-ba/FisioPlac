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
    val medicamentos: List<Medicamento> = emptyList(),

    // --- NOVOS CAMPOS (Step 3) ---
    val visao: String = "",
    val audicao: String = "",

    val incontinenciaUrinaria: String? = null,
    val incontinenciaUrinariaData: String? = null,

    val incontinenciaFecal: String? = null,
    val incontinenciaFecalData: String? = null,

    val sono: String? = null,
    val sonoDisturbioDetalhes: String? = null,

    val usaOrtese: String? = null,
    val orteseDetalhes: String? = null,

    val usaProtese: String? = null,
    val proteseDetalhes: String? = null,

    val teveQuedaUltimos12Meses: String? = null,
    val contagemQuedas: String? = null,

    val eFumante: String? = null,
    val parouFumarTempo: String? = null,

    val eEtilista: String? = null,
    val parouBeberTempo: String? = null
)