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

// 1. Data class para representar TODO o estado da tela
data class GeriatricaUiState(
    val isLoading: Boolean = false,
    val patientProfile: PatientProfile? = null,
    val isPatientFound: Boolean = false,
    val isPatientSelected: Boolean = false,
    val canInitiateFicha: Boolean = false, // Botão principal
    val errorMessage: String? = null,
    val foundPatientId: String? = null,
    val selectedSpecialty: String? = null
)

class GeriatricaViewModel : ViewModel() {

    private val repository = PatientRepository()
    private var searchJob: Job? = null // Para o delay da busca (debounce)

    private val _uiState = MutableLiveData<GeriatricaUiState>(GeriatricaUiState())
    val uiState: LiveData<GeriatricaUiState> = _uiState

    // 1. Chamado pela Activity para definir a especialidade vinda do Intent
    fun setSpecialty(specialty: String) {
        _uiState.value = _uiState.value?.copy(selectedSpecialty = specialty)
    }

    // 2. Chamado pelo TextWatcher da Activity
    fun onCpfChanged(cpf: String) {
        // Cancela qualquer busca anterior
        searchJob?.cancel()

        if (cpf.length < 11) {
            // Se o CPF está incompleto, reseta o estado
            _uiState.value = _uiState.value?.copy(
                isLoading = false,
                patientProfile = null,
                isPatientFound = false,
                isPatientSelected = false,
                canInitiateFicha = false,
                errorMessage = null,
                foundPatientId = null
            )
            return
        }

        // Inicia uma nova busca após um pequeno delay (evita buscas a cada tecla)
        searchJob = viewModelScope.launch {
            delay(500) // Atraso de 500ms
            _uiState.value = _uiState.value?.copy(isLoading = true, isPatientSelected = false)

            val specialty = _uiState.value?.selectedSpecialty ?: "none"
            repository.findAndValidatePatient(cpf, specialty).fold(
                onSuccess = { profile ->
                    // Paciente e Vínculo Encontrados!
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        patientProfile = profile,
                        isPatientFound = true,
                        errorMessage = null,
                        foundPatientId = profile.id
                    )
                },
                onFailure = { error ->
                    // Erro (CPF não existe, Vínculo não existe, etc.)
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        patientProfile = null,
                        isPatientFound = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }

    // 3. Chamado quando o usuário clica no Card do Paciente
    fun onPatientCardClicked() {
        val currentState = _uiState.value ?: return
        if (!currentState.isPatientFound) return // Não faz nada se o paciente não foi encontrado

        val newSelectionState = !currentState.isPatientSelected

        _uiState.value = currentState.copy(
            isPatientSelected = newSelectionState,
            // O botão só fica ativo se o paciente foi encontrado E selecionado
            canInitiateFicha = currentState.isPatientFound && newSelectionState
        )
    }

    // 4. Chamado pela Activity após exibir um Toast de erro
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}
