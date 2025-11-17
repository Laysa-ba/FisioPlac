package com.example.fisioplac.ui.form_geriatrica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.data.model.Medicamento
import com.example.fisioplac.data.repository.GeriatricFormRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Lógica de UI em INGLÊS
data class FormUiState(
    val isLoading: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null
)

/**
 * NOVO data class para agrupar os dados de pré-preenchimento.
 * A Activity que inicia este ViewModel deve buscar esses dados do paciente
 * e passá-los para a função 'startForm'.
 */
data class PacienteInfo(
    val nome: String?,
    val dataNascimento: String?,
    val sexo: String?,
    val estadoCivil: String?,
    val telefone: String?,
    val escolaridade: String?,
    val renda: String?,
    val localResidencia: String?,
    val moraCom: String?
)

class GeriatricFormViewModel : ViewModel() {

    private val repository = GeriatricFormRepository()

    // --- ESTADO COMPARTILHADO (em INGLÊS) ---
    private val _formData = MutableLiveData<GeriatricFicha>(GeriatricFicha())
    val formData: LiveData<GeriatricFicha> = _formData

    private val _currentStep = MutableLiveData<Int>(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _closeForm = MutableLiveData<Boolean>(false)
    val closeForm: LiveData<Boolean> = _closeForm

    private val _uiState = MutableLiveData<FormUiState>(FormUiState())
    val uiState: LiveData<FormUiState> = _uiState

    private val _formSaveSuccess = MutableLiveData<Boolean>(false)
    val formSaveSuccess: LiveData<Boolean> = _formSaveSuccess
    // ----------------------------

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Chamado pela Activity Mãe.
     * *** ALTERADO PARA PRÉ-PREENCHER DADOS ***
     *
     * @param pacienteId O ID do paciente (para salvar a ficha)
     * @param pacienteInfo Dados do paciente vindos do banco (para preencher o formulário)
     * @param nomeDoutor Nome do doutor/estagiário logado (para preencher o campo 'estagiario')
     */
    fun startForm(
        pacienteId: String,
        pacienteInfo: PacienteInfo,
        nomeDoutor: String
    ) {
        // Cria uma nova ficha limpa, mas pré-preenche com os dados fornecidos
        _formData.value = GeriatricFicha(
            // === DADOS FIXOS E DE IDENTIFICAÇÃO ===
            pacienteId = pacienteId,
            dataAvaliacao = getCurrentDate(), // Data de hoje, fixa
            estagiario = nomeDoutor,          // Nome do doutor/estagiário

            // === DADOS PRÉ-PREENCHIDOS DO PACIENTE ===
            nome = pacienteInfo.nome ?: "",
            dataNascimento = pacienteInfo.dataNascimento ?: "",
            sexo = pacienteInfo.sexo, // Permitir nulo se o modelo base permitir
            estadoCivil = pacienteInfo.estadoCivil ?: "",
            telefone = pacienteInfo.telefone ?: "",
            escolaridade = pacienteInfo.escolaridade ?: "",
            renda = pacienteInfo.renda ?: "", // A máscara no Fragment vai formatar
            localResidencia = pacienteInfo.localResidencia ?: "",
            moraCom = pacienteInfo.moraCom ?: ""
            // O resto dos campos (ex: queixaPrincipal) usarão o valor default ""
        )

        // Reinicia a etapa para 1
        _currentStep.value = 1
    }

    /**
     * Chamado pelo Step1Fragment.
     */
    fun onStep1NextClicked(dataFromView: GeriatricFicha) {
        val errors = validateStep1(dataFromView)

        if (errors.isNotEmpty()) {
            _uiState.value = FormUiState(
                validationErrors = errors,
                errorMessage = "Aviso: Todos os dados são obrigatórios!"
            )
            return
        }

        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView
        _currentStep.value = 2
    }

    /**
     * Chamado pelo Step2Fragment (Medicamentos).
     */
    fun onStep2NextClicked(medicamentos: List<Medicamento>) {
        _formData.value = _formData.value?.copy(medicamentos = medicamentos)
        _currentStep.value = 3 // Avança para a Etapa 3
    }

    /**
     * Chamado pelo Step3Fragment.
     */
    fun onStep3NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 3

        // ATUALIZADO: Avança para o Step 4
        _currentStep.value = 4
    }

    /**
     * Chamado pelo Step4Fragment.
     */
    fun onStep4NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 4

        // ATUALIZADO: Avança para o Step 5
        _currentStep.value = 5
    }

    /**
     * Chamado pelo Step5Fragment.
     */
    fun onStep5NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 5

        // ATUALIZADO: Avança para o Step 6
        _currentStep.value = 6
    }

    /**
     * Chamado pelo Step6Fragment.
     */
    fun onStep6NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 6

        // ATUALIZADO: Avança para o Step 7
        _currentStep.value = 7
    }

    /**
     * Chamado pelo Step7Fragment.
     */
    fun onStep7NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 7

        // ATUALIZADO: Avança para o Step 8
        _currentStep.value = 8
    }

    /**
     * Chamado pelo Step8Fragment.
     */
    fun onStep8NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 8

        // ATUALIZADO: Avança para o Step 9
        _currentStep.value = 9
    }

    /**
     * Chamado pelo Step9Fragment (não salva dados).
     */
    fun onStep9NextClicked() {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        // Não atualiza o _formData.value, conforme solicitado

        // ATUALIZADO: Avança para o Step 10
        _currentStep.value = 10
    }

    /**
     * Chamado pelo Step10Fragment.
     */
    fun onStep10NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 10

        // ATUALIZADO: Avança para o Step 11
        _currentStep.value = 11
    }

    /**
     * Chamado pelo Step11Fragment.
     */
    fun onStep11NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 11

        // ATUALIZADO: Avança para o Step 12
        _currentStep.value = 12
    }

    /**
     * Chamado pelo Step12Fragment (Final).
     */
    fun onStep12NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 12

        // Chama a função de concluir
        onConcluirClicked()
    }

    /**
     * Chamado pelo Step2Fragment quando um medicamento é adicionado.
     */
    fun addMedicamento(nome: String, tempoUso: String, comoUsar: String) {
        val novoMedicamento = Medicamento(nome, tempoUso, comoUsar)
        val listaAtual = _formData.value?.medicamentos?.toMutableList() ?: mutableListOf()
        listaAtual.add(novoMedicamento)
        _formData.value = _formData.value?.copy(medicamentos = listaAtual)
    }

    /**
     * Chamado pelo Adapter quando um item é expandido/colapsado.
     */
    fun toggleMedicamentoExpanded(position: Int) {
        val listaAntiga = _formData.value?.medicamentos ?: return
        val novaLista = listaAntiga.mapIndexed { index, medicamento ->
            if (index == position) {
                medicamento.copy(isExpanded = !medicamento.isExpanded)
            } else {
                medicamento
            }
        }
        _formData.value = _formData.value?.copy(medicamentos = novaLista)
    }

    /**
     * *** NOVA FUNÇÃO ***
     * Chamada pelos fragments ao clicar em 'Voltar' para salvar o estado atual.
     */
    fun updateFormData(dataFromView: GeriatricFicha) {
        _formData.value = dataFromView
    }

    /**
     * Chamado pela Etapa Final (agora, o Step 12) ao clicar em "Concluir".
     */
    fun onConcluirClicked() {
        _uiState.value = FormUiState(isLoading = true)

        // Garante que a ficha completa seja a versão mais atual
        val fichaCompleta = _formData.value!!

        viewModelScope.launch {
            repository.saveFinalFicha(fichaCompleta.pacienteId, fichaCompleta).fold(
                onSuccess = {
                    _uiState.value = FormUiState(isLoading = false)
                    _formSaveSuccess.value = true // Sinaliza sucesso para a Activity
                },
                onFailure = { e ->
                    _uiState.value = FormUiState(errorMessage = "Falha ao salvar dados: ${e.message}")
                }
            )
        }
    }

    fun onBackClicked() {
        val current = _currentStep.value ?: 1
        if (current == 1) {
            _closeForm.value = true
        } else {
            _uiState.value = FormUiState(validationErrors = emptyMap())
            _currentStep.value = current - 1
        }
    }

    // Lógica de validação
    private fun validateStep1(data: GeriatricFicha): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val requiredError = "Campo obrigatório"

        // Exemplo de validação buscando campos em português
        if (data.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (data.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (data.nome.isBlank()) errors["nome"] = requiredError

        // Validação de campos do Step 1 (em português)
        val unmaskedTelefone = data.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = data.renda.replace(Regex("[^\\d]"), "")
        if (data.dataNascimento.isBlank()) errors["dataNascimento"] = requiredError
        if (data.idade.isBlank()) errors["idade"] = requiredError
        if (unmaskedTelefone.isBlank()) errors["telefone"] = requiredError
        if (unmaskedRenda.isBlank()) errors["renda"] = requiredError
        if (data.queixaPrincipal.isBlank()) errors["queixaPrincipal"] = requiredError
        if (data.estadoCivil.isBlank()) errors["estadoCivil"] = requiredError
        if (data.escolaridade.isBlank()) errors["escolaridade"] = requiredError
        if (data.localResidencia.isBlank()) errors["localResidencia"] = requiredError
        if (data.moraCom.isBlank()) errors["moraCom"] = requiredError
        if (data.atividadeSocial.isBlank()) errors["atividadeSocial"] = requiredError
        if (data.doencasAssociadas.isBlank()) errors["doencasAssociadas"] = requiredError
        if (data.sexo.isNullOrBlank() || data.sexo == "null") errors["sexo"] = requiredError
        if (data.praticaAtividadeFisica.isNullOrBlank() || data.praticaAtividadeFisica == "null") errors["praticaAtividadeFisica"] = requiredError
        if (data.frequenciaSair.isNullOrBlank() || data.frequenciaSair == "null") errors["frequenciaSair"] = requiredError
        if (data.praticaAtividadeFisica == "Sim" && data.diasPorSemana.isBlank()) {
            errors["diasPorSemana"] = requiredError
        }

        return errors
    }

    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}