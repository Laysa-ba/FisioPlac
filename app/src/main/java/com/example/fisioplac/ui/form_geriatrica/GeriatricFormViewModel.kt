package com.example.fisioplac.ui.form_geriatrica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.data.repository.GeriatricFormRepository
import kotlinx.coroutines.launch

// Estado da UI para a Etapa 1
data class FormStep1UiState(
    val isLoading: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(), // Mapa de [Campo] -> [Erro]
    val navigateToStep2: Boolean = false,
    val errorMessage: String? = null
)

class GeriatricFormViewModel : ViewModel() {

    private val repository = GeriatricFormRepository()

    // ---- ESTADO COMPARTILHADO ----
    private var formId: String? = null
    private val _formData = MutableLiveData<GeriatricFicha>(GeriatricFicha())
    val formData: LiveData<GeriatricFicha> = _formData
    // -----------------------------

    // Estado da UI apenas para a Etapa 1
    private val _uiState = MutableLiveData<FormStep1UiState>(FormStep1UiState())
    val uiState: LiveData<FormStep1UiState> = _uiState

    /**
     * Chamado pela Activity (Tela1FichaActivity) quando ela é criada.
     * Prepara o formulário, criando um novo documento no Firestore.
     */
    fun startNewForm(pacienteId: String, pacienteNome: String?) {
        if (formId != null) return // Já foi iniciado

        _uiState.value = FormStep1UiState(isLoading = true)
        viewModelScope.launch {
            repository.createNewFicha(pacienteId, pacienteNome).fold(
                onSuccess = { newFormId ->
                    formId = newFormId
                    // Pré-preenche o LiveData com o ID e o nome
                    _formData.value = _formData.value?.copy(
                        pacienteId = pacienteId,
                        nome = pacienteNome ?: ""
                    )
                    _uiState.value = FormStep1UiState(isLoading = false)
                },
                onFailure = {
                    _uiState.value = FormStep1UiState(errorMessage = "Falha ao criar ficha.")
                }
            )
        }
    }

    /**
     * Chamado quando o botão "Avançar" da Etapa 1 é clicado.
     */
    fun onStep1NextClicked(dataFromView: GeriatricFicha) {
        val errors = validateStep1(dataFromView)
        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value?.copy(
                validationErrors = errors,
                errorMessage = "Aviso: Todos os dados são obrigatórios!"
            )
            return
        }

        // Limpa erros e define como carregando
        _uiState.value = _uiState.value?.copy(isLoading = true, validationErrors = emptyMap())

        viewModelScope.launch {
            repository.saveStep1Data(formId!!, dataFromView).fold(
                onSuccess = {
                    _formData.value = dataFromView // Atualiza o LiveData compartilhado
                    _uiState.value = _uiState.value?.copy(isLoading = false, navigateToStep2 = true)
                },
                onFailure = {
                    _uiState.value = _uiState.value?.copy(isLoading = false, errorMessage = "Falha ao salvar dados.")
                }
            )
        }
    }

    /**
     * Lógica de validação da Etapa 1 (movida da Activity)
     */
    private fun validateStep1(data: GeriatricFicha): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val requiredError = "Campo obrigatório"

        // --- CORREÇÃO DO BUG ---
        // Remove todos os caracteres não numéricos antes de checar
        val unmaskedTelefone = data.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = data.renda.replace(Regex("[^\\d]"), "")

        // Campos de Texto
        if (data.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (data.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (data.nome.isBlank()) errors["nome"] = requiredError
        if (data.dataNascimento.isBlank()) errors["dataNascimento"] = requiredError
        if (data.idade.isBlank()) errors["idade"] = requiredError
        if (unmaskedTelefone.isBlank()) errors["telefone"] = requiredError // <-- Corrigido
        if (unmaskedRenda.isBlank()) errors["renda"] = requiredError // <-- Corrigido
        if (data.queixaPrincipal.isBlank()) errors["queixaPrincipal"] = requiredError

        // AutoComplete (Dropdowns)
        if (data.estadoCivil.isBlank()) errors["estadoCivil"] = requiredError
        if (data.escolaridade.isBlank()) errors["escolaridade"] = requiredError
        if (data.localResidencia.isBlank()) errors["localResidencia"] = requiredError
        if (data.moraCom.isBlank()) errors["moraCom"] = requiredError
        if (data.atividadeSocial.isBlank()) errors["atividadeSocial"] = requiredError
        if (data.doencasAssociadas.isBlank()) errors["doencasAssociadas"] = requiredError

        // Radio Groups
        if (data.sexo.isBlank()) errors["sexo"] = requiredError
        if (data.frequenciaSair.isBlank()) errors["frequenciaSair"] = requiredError
        if (data.praticaAtividadeFisica && data.diasPorSemana.isBlank()) {
            errors["diasPorSemana"] = requiredError
        }

        return errors
    }

    // Chamado pela View quando o Toast de erro é exibido
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}