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
    val parouBeberTempo: String? = null,

    // --- (Step 4 - Estado Mental) ---
    val orientacaoTempoMes: Boolean = false,
    val orientacaoTempoDia: Boolean = false,
    val orientacaoTempoAno: Boolean = false,
    val orientacaoTempoDiaSemana: Boolean = false,
    val orientacaoTempoAproximado: Boolean = false,
    val pontuacaoOrientacaoTempo: Int = 0,

    val orientacaoLocalEstado: Boolean = false,
    val orientacaoLocalCidade: Boolean = false,
    val orientacaoLocalBairro: Boolean = false,
    val orientacaoLocalLocal: Boolean = false,
    val orientacaoLocalPais: Boolean = false,
    val pontuacaoOrientacaoLocal: Int = 0,

    val registroPalavraCarro: Boolean = false,
    val registroPalavraVaso: Boolean = false,
    val registroPalavraTijolo: Boolean = false,
    val pontuacaoRegistroPalavras: Int = 0,

    val calculoRealiza: String? = null, // "Sim" ou "Não"
    val pontuacaoCalculo: Int = 0,

    // --- (Step 5 - Memorização e Linguagem) ---
    val pontuacaoMemorizacao: Int = 0,
    val linguagemNomearObjetos: Int = 0,
    val linguagemRepetirFrase: Int = 0,
    val linguagemComandoEstagios: Int = 0,
    val linguagemLerOrdem: Int = 0,
    val linguagemEscreverFrase: Int = 0,
    val linguagemCopiarDesenho: Int = 0,
    val pontuacaoTotalEstadoMental: Int = 0,
    val diagnosticoEstadoMental: String = "",

    // --- (Step 6 - Força Muscular) ---
    val forcaOmbroD: String = "",
    val forcaOmbroE: String = "",
    val forcaCotoveloD: String = "",
    val forcaCotoveloE: String = "",
    val forcaPunhoD: String = "",
    val forcaPunhoE: String = "",
    val forcaQuadrilD: String = "",
    val forcaQuadrilE: String = "",
    val forcaJoelhoD: String = "",
    val forcaJoelhoE: String = "",
    val forcaTornozeloD: String = "",
    val forcaTornozeloE: String = "",

    // --- (Step 7 - Índice de Barthel) ---
    val barthelAlimentacao: String? = null,
    val barthelBanho: String? = null,
    val barthelAtividadesDiarias: String? = null,
    val barthelVestir: String? = null,
    val barthelIntestino: String? = null,
    val barthelUrinario: String? = null,
    val barthelUsoBanheiro: String? = null,
    val barthelTransferencia: String? = null,
    val barthelMobilidade: String? = null,
    val barthelEscadas: String? = null,
    val barthelPontuacaoTotal: Int = 0,
    val barthelNivelDependencia: String = "",

    // --- (Step 8 - TUGT) ---
    val tugtTempo: String = "",
    val tugtDiagnostico: String = "",

    // --- (Step 10 - Teste de Caminhada 6 Minutos) ---
    val tc6mPa0: String = "",
    val tc6mPa3: String = "",
    val tc6mPa6: String = "",
    val tc6mFc0: String = "",
    val tc6mFc3: String = "",
    val tc6mFc6: String = "",
    val tc6mSat0: String = "",
    val tc6mSat3: String = "",
    val tc6mSat6: String = "",
    val tc6mFr0: String = "",
    val tc6mFr3: String = "",
    val tc6mFr6: String = "",
    val tc6mBorg0: String = "",
    val tc6mBorg3: String = "",
    val tc6mBorg6: String = "",
    val tc6mDistanciaPercorrida: String = "",
    val tc6mDistanciaPredita: String = "",

    // --- (Step 11 - Equilíbrio) ---
    val equilibrioSentarApoios: String? = null, // "Sem apoios", "Com 1 apoio", etc.
    val equilibrioSentarDesequilibrio: Boolean = false,
    val equilibrioSentarNota: Double = 0.0,
    val equilibrioLevantarApoios: String? = null, // "Sem apoios", "Com 1 apoio", etc.
    val equilibrioLevantarDesequilibrio: Boolean = false,
    val equilibrioLevantarNota: Double = 0.0,

    val diagnosticoFisioterapeutico: String = "",
    val objetivosTratamento: String = ""
)