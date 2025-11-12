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
     * *** CORREÇÃO DO AUTOFILL APLICADA AQUI ***
     */
    fun startForm(pacienteId: String, pacienteNome: String?) {
        // Remove a verificação "if" para forçar uma nova ficha limpa
        _formData.value = GeriatricFicha(
            pacienteId = pacienteId,
            nome = pacienteNome ?: "",
            dataAvaliacao = getCurrentDate()
            // O resto dos campos (em português) virão do default do GeriatricFicha
        )
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
     * Chamado pelo Step3Fragment (ao clicar em "Concluir").
     */
    fun onStep3NextClicked(dataFromView: GeriatricFicha) {
        _uiState.value = FormUiState(validationErrors = emptyMap())
        _formData.value = dataFromView // Atualiza o formData com os dados do Step 3
        onConcluirClicked() // Chama a função de concluir
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
     * Chamado pela Etapa Final (agora, o Step 3) ao clicar em "Concluir".
     */
    fun onConcluirClicked() {
        _uiState.value = FormUiState(isLoading = true)

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
        val unmaskedTelefone = data.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = data.renda.replace(Regex("[^\\d]"), "")

        if (data.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (data.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (data.nome.isBlank()) errors["nome"] = requiredError
        // ... (etc. campos em português do modelo)
        return errors
    }

    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}