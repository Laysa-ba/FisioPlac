package com.example.fisioplac.ui.form_geriatrica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.repository.PatientProfile
import com.example.fisioplac.data.repository.PatientRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Representa o estado da UI para a GeriatricaActivity (Tela de Seleção de Paciente).
 */
data class GeriatricaUiState(
    val isLoading: Boolean = false,
    val isPatientFound: Boolean = false,
    val isPatientSelected: Boolean = false,
    val canInitiateFicha: Boolean = false,
    val foundPatientId: String? = null,
    val patientProfile: PatientProfile? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel para a GeriatricaActivity (Tela de Seleção de Paciente).
 * Lida com a busca e validação do paciente por CPF.
 */
class GeriatricaViewModel : ViewModel() {

    private val repository = PatientRepository()
    private var searchJob: Job? = null
    private var selectedSpecialty: String = ""

    private val _uiState = MutableLiveData(GeriatricaUiState())
    val uiState: LiveData<GeriatricaUiState> = _uiState

    /**
     * Define a especialidade selecionada (vinda da NewFileActivity).
     */
    fun setSpecialty(specialty: String) {
        selectedSpecialty = specialty
    }

    /**
     * Chamado toda vez que o texto do CPF muda.
     * Usa um "debounce" de 500ms para evitar buscas excessivas.
     */
    fun onCpfChanged(cpf: String) {
        searchJob?.cancel() // Cancela a busca anterior

        if (cpf.length < 11) {
            _uiState.value = GeriatricaUiState() // Reseta o estado
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce: espera 500ms após o usuário parar de digitar
            _uiState.value = GeriatricaUiState(isLoading = true)

            repository.findAndValidatePatient(cpf, selectedSpecialty).fold(
                onSuccess = { profile ->
                    _uiState.value = GeriatricaUiState(
                        isPatientFound = true,
                        foundPatientId = profile.id,
                        patientProfile = profile
                    )
                },
                onFailure = { e ->
                    _uiState.value = GeriatricaUiState(errorMessage = e.message)
                }
            )
        }
    }

    /**
     * Chamado quando o card do paciente é clicado.
     */
    fun onPatientCardClicked() {
        val currentState = _uiState.value ?: return
        if (!currentState.isPatientFound) return

        val isNowSelected = !currentState.isPatientSelected
        _uiState.value = currentState.copy(
            isPatientSelected = isNowSelected,
            canInitiateFicha = isNowSelected // Habilita o botão se estiver selecionado
        )
    }

    /**
     * Chamado pela View após exibir uma mensagem de erro.
     */
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}