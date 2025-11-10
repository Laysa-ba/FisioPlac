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

// Estado da UI (Idle, Loading, Error, Validation Error)
data class FormUiState(
    val isLoading: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null
)

class GeriatricFormViewModel : ViewModel() {

    private val repository = GeriatricFormRepository()

    // --- ESTADO COMPARTILHADO ---
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
     * Chamado pela Activity Mãe. Apenas inicia o ViewModel localmente.
     */
    fun startForm(pacienteId: String, pacienteNome: String?) {
        if (_formData.value?.pacienteId.isNullOrBlank()) {
            _formData.value = GeriatricFicha(
                pacienteId = pacienteId,
                nome = pacienteNome ?: "",
                dataAvaliacao = getCurrentDate()
            )
        }
    }

    /**
     * Chamado pelo Step1Fragment.
     * Valida, atualiza o LiveData local e avança a etapa.
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
     * Atualiza o LiveData local e avança a etapa.
     */
    fun onStep2NextClicked(medicamentos: List<Medicamento>) {
        _formData.value = _formData.value?.copy(medicamentos = medicamentos)
        _currentStep.value = 3 // Avança para a Etapa 3
    }

    /**
     * Chamado pelo Step2Fragment quando um medicamento é adicionado.
     * Adiciona o item a uma nova lista e atualiza o LiveData.
     */
    fun addMedicamento(nome: String, tempoUso: String, comoUsar: String) {
        val novoMedicamento = Medicamento(nome, tempoUso, comoUsar)
        val listaAtual = _formData.value?.medicamentos?.toMutableList() ?: mutableListOf()
        listaAtual.add(novoMedicamento)
        _formData.value = _formData.value?.copy(medicamentos = listaAtual)
    }

    /**
     * *** FUNÇÃO CORRIGIDA ***
     * Chamado pelo Adapter quando um item é expandido/colapsado.
     * Cria uma *nova lista* (imutabilidade) para forçar o LiveData a atualizar.
     */
    fun toggleMedicamentoExpanded(position: Int) {
        val listaAntiga = _formData.value?.medicamentos ?: return

        // Cria uma *nova* lista, mapeando cada item
        val novaLista = listaAntiga.mapIndexed { index, medicamento ->
            if (index == position) {
                // Se for o item clicado, retorna uma *cópia* dele com o 'isExpanded' trocado
                medicamento.copy(isExpanded = !medicamento.isExpanded)
            } else {
                // Se não for o item clicado, retorna o item original
                medicamento
            }
        }

        // Define a *nova lista* no LiveData
        _formData.value = _formData.value?.copy(medicamentos = novaLista)
    }

    /**
     * Chamado pela Etapa Final (ex: Etapa 2, por enquanto) ao clicar em "Concluir".
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

    // Lógica de validação (sem alterações)
    private fun validateStep1(data: GeriatricFicha): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val requiredError = "Campo obrigatório"
        val unmaskedTelefone = data.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = data.renda.replace(Regex("[^\\d]"), "")

        if (data.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (data.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (data.nome.isBlank()) errors["nome"] = requiredError
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