package com.example.fisioplac.ui.form_geriatrica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.model.GeriatricFicha
import com.example.fisioplac.data.repository.GeriatricFormRepository
import kotlinx.coroutines.launch

// 1. Estado da UI para a Tela 1
data class FormUiState(
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(), // Mapa de [Campo] -> [Erro]
    val errorMessage: String? = null
)

class GeriatricFormViewModel : ViewModel() {

    private val repository = GeriatricFormRepository()

    // 2. LiveData do estado da UI
    private val _uiState = MutableLiveData<FormUiState>(FormUiState())
    val uiState: LiveData<FormUiState> = _uiState

    /**
     * Chamado quando o botão "Concluir" (antigo Avançar) é clicado.
     */
    fun onConcluirClicked(pacienteId: String, data: GeriatricFicha) {
        // 3. Valida os dados
        val errors = validateStep1(data)
        if (errors.isNotEmpty()) {
            _uiState.value = FormUiState(
                validationErrors = errors,
                errorMessage = "Aviso: Todos os dados são obrigatórios!"
            )
            return
        }

        // 4. Se for válido, tenta salvar
        _uiState.value = FormUiState(isLoading = true)
        viewModelScope.launch {
            repository.saveFichaData(pacienteId, data).fold(
                onSuccess = {
                    _uiState.value = FormUiState(isSaveSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = FormUiState(errorMessage = "Falha ao salvar: ${e.message}")
                }
            )
        }
    }

    /**
     * Lógica de validação (movida da Activity)
     */
    private fun validateStep1(data: GeriatricFicha): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val requiredError = "Campo obrigatório"

        // Remove máscaras para validar
        val unmaskedTelefone = data.telefone.replace(Regex("[^\\d]"), "")
        val unmaskedRenda = data.renda.replace(Regex("[^\\d]"), "")

        // Campos de Texto
        if (data.dataAvaliacao.isBlank()) errors["dataAvaliacao"] = requiredError
        if (data.estagiario.isBlank()) errors["estagiario"] = requiredError
        if (data.nome.isBlank()) errors["nome"] = requiredError
        if (data.dataNascimento.isBlank()) errors["dataNascimento"] = requiredError
        if (data.idade.isBlank()) errors["idade"] = requiredError
        if (unmaskedTelefone.isBlank()) errors["telefone"] = requiredError
        if (unmaskedRenda.isBlank()) errors["renda"] = requiredError
        if (data.queixaPrincipal.isBlank()) errors["queixaPrincipal"] = requiredError

        // Dropdowns
        if (data.estadoCivil.isBlank()) errors["estadoCivil"] = requiredError
        if (data.escolaridade.isBlank()) errors["escolaridade"] = requiredError
        if (data.localResidencia.isBlank()) errors["localResidencia"] = requiredError
        if (data.moraCom.isBlank()) errors["moraCom"] = requiredError
        if (data.atividadeSocial.isBlank()) errors["atividadeSocial"] = requiredError
        if (data.doencasAssociadas.isBlank()) errors["doencasAssociadas"] = requiredError

        // Radio Groups
        if (data.sexo.isNullOrBlank()) errors["sexo"] = requiredError
        if (data.praticaAtividadeFisica.isNullOrBlank()) errors["praticaAtividadeFisica"] = requiredError
        if (data.frequenciaSair.isNullOrBlank()) errors["frequenciaSair"] = requiredError

        // Validação condicional
        if (data.praticaAtividadeFisica == "Sim" && data.diasPorSemana.isBlank()) {
            errors["diasPorSemana"] = requiredError
        }

        return errors
    }

    // Chamado pela View quando o Toast de erro é exibido
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}