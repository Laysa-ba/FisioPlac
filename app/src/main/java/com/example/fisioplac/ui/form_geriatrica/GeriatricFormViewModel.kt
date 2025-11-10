package com.example.fisioplac.ui.form_geriatrica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.TOTAL_FICHA_STEPS
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.data.repository.GeriatricFormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// (Opcional, mas recomendado) Crie esta classe para o perfil do paciente
data class PatientProfile(
    val name: String,
    val birthDate: String,
    val age: String,
    val sex: String
)

/**
 * 1. O 'UiState' agora contém os dados da TELA 0 (CPF) E da TELA 1 (Fragment).
 */
data class GeriatricFormUiState(
    // --- Dados da GeriatricaActivity (Tela 0 - CPF) ---
    val isLoading: Boolean = false,
    val isPatientFound: Boolean = false,
    val patientProfile: PatientProfile? = null, // A Activity usa isso!
    val isPatientSelected: Boolean = false,
    val canInitiateFicha: Boolean = false,
    val errorMessage: String? = null,
    val foundPatientId: String? = null, // A Activity usa isso!
    val specialty: String? = null, // A Activity usa isso!
    val pacienteId: String? = null, // Id final do paciente
    val validationErrors: Map<String, String> = emptyMap(),

    val navigationEvent: NavigationEvent? = null, // Para controlar a navegação

    // --- Dados da Tela 1 ---
    val passoAtual: Int = 1,
    val totalPassos: Int = TOTAL_FICHA_STEPS,
    val dataAvaliacao: String = "",
    val estagiario: String = "",
    val nome: String = "",
    val dataNascimento: String = "",
    val idade: String = "",
    val sexo: String = "",
    val telefone: String = "",
    val estadoCivil: String = "",
    val escolaridade: String = "",
    val localResidencia: String = "",
    val moraCom: String = "",
    val renda: String = "",
    val queixaPrincipal: String = "",
    val outrasDoencas: String = "",
    val praticaAtividadeFisica: String = "", // "Sim", "Não", ""
    val diasPorSemana: String = "",
    val frequenciaSair: String = "",
    val atividadeSocial: String = "",
    val doencasAssociadas: String = ""

    // ... (Aqui entrarão os dados das Telas 2, 3, etc.) ...
)

// Eventos de navegação que a Activity "Mãe" vai observar
sealed class NavigationEvent {
    object NavigateToTela2 : NavigationEvent()
    object NavigateToTela3 : NavigationEvent()
    object SaveSuccess : NavigationEvent()
}

/**
 * 2. MUDANÇA CRÍTICA:
 * Removemos o repositório do construtor POR ENQUANTO.
 * Passar o repositório no construtor (injeção de dependência) é o CORRETO,
 * mas faria seu app quebrar agora, pois você precisaria de um ViewModelFactory ou Hilt.
 * Vamos instanciar o repositório aqui dentro para fazer o app funcionar AGORA.
 */
class GeriatricFormViewModel : ViewModel() { // <-- CONSTRUTOR MUDADO

    // Instanciando o repositório aqui (solução temporária)
    private val repository = GeriatricFormRepository()

    private val _uiState = MutableStateFlow(GeriatricFormUiState())
    val uiState = _uiState.asStateFlow()

    // --- 3. FUNÇÕES QUE FALTAVAM (para GeriatricaActivity) ---

    fun setSpecialty(specialty: String) {
        _uiState.update { it.copy(specialty = specialty) }
    }

    fun onCpfChanged(cpf: String) {
        // TODO: Adicione sua lógica de busca de paciente aqui.
        // Se encontrar, atualize o estado:
        // _uiState.update { it.copy(
        //     isPatientFound = true,
        //     patientProfile = PatientProfile(name = "Nome Paciente", age = "50", ...),
        //     foundPatientId = "ID_DO_PACIENTE_ENCONTRADO"
        // )}
    }

    fun onPatientCardClicked() {
        // Lógica de seleção do card
        val currentState = _uiState.value
        val isSelected = !currentState.isPatientSelected // Inverte a seleção
        _uiState.update {
            it.copy(
                isPatientSelected = isSelected,
                canInitiateFicha = isSelected // Habilita o botão se selecionado
            )
        }
    }

    // Função chamada quando a Activity "iniciar ficha"
    // Ela copia os dados do paciente para os campos da Tela 1
    fun onInitiateFicha(pacienteId: String, profile: PatientProfile) {
        _uiState.update { it.copy(
            pacienteId = pacienteId,
            nome = profile.name,
            dataNascimento = profile.birthDate,
            idade = profile.age,
            sexo = profile.sex
        )}
    }

    // --- 4. FUNÇÕES DE EVENTO DA TELA 1 (Preenchidas) ---

    fun onNomeChanged(nome: String) {
        _uiState.update { it.copy(nome = nome, validationErrors = it.validationErrors - "nome") }
    }
    fun onDataAvaliacaoChanged(data: String) {
        _uiState.update { it.copy(dataAvaliacao = data, validationErrors = it.validationErrors - "dataAvaliacao") }
    }
    fun onEstagiarioChanged(nome: String) {
        _uiState.update { it.copy(estagiario = nome, validationErrors = it.validationErrors - "estagiario") }
    }
    fun onDataNascimentoChanged(data: String, idade: String) {
        _uiState.update { it.copy(dataNascimento = data, idade = idade, validationErrors = it.validationErrors - "dataNascimento" - "idade") }
    }
    fun onSexoChanged(sexo: String) {
        _uiState.update { it.copy(sexo = sexo, validationErrors = it.validationErrors - "sexo") }
    }
    fun onTelefoneChanged(telefone: String) {
        _uiState.update { it.copy(telefone = telefone, validationErrors = it.validationErrors - "telefone") }
    }
    fun onEstadoCivilChanged(estado: String) {
        _uiState.update { it.copy(estadoCivil = estado, validationErrors = it.validationErrors - "estadoCivil") }
    }
    fun onEscolaridadeChanged(escolaridade: String) {
        _uiState.update { it.copy(escolaridade = escolaridade, validationErrors = it.validationErrors - "escolaridade") }
    }
    fun onLocalResidenciaChanged(local: String) {
        _uiState.update { it.copy(localResidencia = local, validationErrors = it.validationErrors - "localResidencia") }
    }
    fun onMoraComChanged(moraCom: String) {
        _uiState.update { it.copy(moraCom = moraCom, validationErrors = it.validationErrors - "moraCom") }
    }
    fun onRendaChanged(renda: String) {
        _uiState.update { it.copy(renda = renda, validationErrors = it.validationErrors - "renda") }
    }
    fun onQueixaPrincipalChanged(queixa: String) {
        _uiState.update { it.copy(queixaPrincipal = queixa, validationErrors = it.validationErrors - "queixaPrincipal") }
    }
    fun onOutrasDoencasChanged(doencas: String) {
        _uiState.update { it.copy(outrasDoencas = doencas) } // Campo opcional
    }
    fun onPraticaAtividadeFisicaChanged(pratica: String) {
        val diasSemana = if (pratica == "Sim") _uiState.value.diasPorSemana else ""
        _uiState.update { it.copy(praticaAtividadeFisica = pratica, diasPorSemana = diasSemana, validationErrors = it.validationErrors - "praticaAtividadeFisica") }
    }
    fun onDiasPorSemanaChanged(dias: String) {
        _uiState.update { it.copy(diasPorSemana = dias, validationErrors = it.validationErrors - "diasPorSemana") }
    }
    fun onFrequenciaSairChanged(frequencia: String) {
        _uiState.update { it.copy(frequenciaSair = frequencia, validationErrors = it.validationErrors - "frequenciaSair") }
    }
    fun onAtividadeSocialChanged(atividade: String) {
        _uiState.update { it.copy(atividadeSocial = atividade, validationErrors = it.validationErrors - "atividadeSocial") }
    }
    fun onDoencasAssociadasChanged(doencas: String) {
        _uiState.update { it.copy(doencasAssociadas = doencas, validationErrors = it.validationErrors - "doencasAssociadas") }
    }

    // --- 5. LÓGICA DE VALIDAÇÃO (Preenchida) ---

    fun onAvancarTela1Clicked() {
        val errors = validateStep1()
        if (errors.isNotEmpty()) {
            _uiState.update {
                it.copy(validationErrors = errors, errorMessage = "Aviso: Todos os dados são obrigatórios!")
            }
            return
        }
        _uiState.update {
            it.copy(isLoading = false, navigationEvent = NavigationEvent.NavigateToTela2)
        }
    }

    private fun validateStep1(): Map<String, String> {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()
        val requiredError = "Campo obrigatório"

        // Remove máscaras para validar
        val unmaskedTelefone = state.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = state.renda.replace(Regex("[^\\d]"), "")

        // Campos de Texto
        if (state.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (state.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (state.nome.isBlank()) errors["nome"] = requiredError
        if (state.dataNascimento.isBlank()) errors["dataNascimento"] = requiredError
        if (state.idade.isBlank()) errors["idade"] = requiredError
        if (unmaskedTelefone.isBlank()) errors["telefone"] = requiredError
        if (unmaskedRenda.isBlank()) errors["renda"] = requiredError
        if (state.queixaPrincipal.isBlank()) errors["queixaPrincipal"] = requiredError

        // Dropdowns
        if (state.estadoCivil.isBlank()) errors["estadoCivil"] = requiredError
        if (state.escolaridade.isBlank()) errors["escolaridade"] = requiredError
        if (state.localResidencia.isBlank()) errors["localResidencia"] = requiredError
        if (state.moraCom.isBlank()) errors["moraCom"] = requiredError
        if (state.atividadeSocial.isBlank()) errors["atividadeSocial"] = requiredError
        if (state.doencasAssociadas.isBlank()) errors["doencasAssociadas"] = requiredError

        // Radio Groups
        if (state.sexo.isBlank()) errors["sexo"] = requiredError
        if (state.praticaAtividadeFisica.isBlank()) errors["praticaAtividadeFisica"] = requiredError
        if (state.frequenciaSair.isBlank()) errors["frequenciaSair"] = requiredError

        // Validação condicional
        if (state.praticaAtividadeFisica == "Sim" && state.diasPorSemana.isBlank()) {
            errors["diasPorSemana"] = requiredError
        }

        return errors
    }

    // --- 6. LÓGICA DE SALVAR (Chamada pela Tela 12) ---

    fun onSalvarFichaCompletaClicked() {
        _uiState.update { it.copy(isLoading = true) }
        val state = _uiState.value

        // TODO: Validar os campos das telas 2-12

        val fichaCompleta = GeriatricFicha(
            dataAvaliacao = state.dataAvaliacao,
            estagiario = state.estagiario,
            nome = state.nome,
            dataNascimento = state.dataNascimento,
            idade = state.idade,
            sexo = state.sexo,
            telefone = state.telefone,
            estadoCivil = state.estadoCivil,
            escolaridade = state.escolaridade,
            localResidencia = state.localResidencia,
            moraCom = state.moraCom,
            renda = state.renda,
            queixaPrincipal = state.queixaPrincipal,
            outrasDoencas = state.outrasDoencas,
            praticaAtividadeFisica = state.praticaAtividadeFisica,
            diasPorSemana = state.diasPorSemana,
            frequenciaSair = state.frequenciaSair,
            atividadeSocial = state.atividadeSocial,
            doencasAssociadas = state.doencasAssociadas
            // ... adicione os campos das telas 2-12 aqui ...
        )

        viewModelScope.launch {
            repository.saveFichaData(state.pacienteId!!, fichaCompleta).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, navigationEvent = NavigationEvent.SaveSuccess) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Falha ao salvar: ${e.message}") }
                }
            )
        }
    }

    // --- 7. FUNÇÕES DE EVENTOS ÚNICOS ---

    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigationDone() {
        _uiState.update { it.copy(navigationEvent = null) }
    }
}