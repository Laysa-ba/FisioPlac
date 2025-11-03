package com.example.fisioplac.ui.newfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * Data class que representa o estado da UI para a NewFileActivity.
 * Ele diz exatamente quais cards devem ser visíveis.
 */
data class NewFileUiState(
    val showGeriatriaCard: Boolean = false,
    val showTraumatoCard: Boolean = false
)

class NewFileViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableLiveData<NewFileUiState>(NewFileUiState())
    val uiState: LiveData<NewFileUiState> = _uiState

    init {
        // Assim que o ViewModel for criado, busca as especialidades do doutor
        fetchDoctorSpecialties()
    }

    private fun fetchDoctorSpecialties() {
        viewModelScope.launch {
            val uid = repository.getCurrentUid()
            if (uid != null) {
                // Usa a função que já existe no nosso repositório
                repository.fetchDoctorProfile(uid).fold(
                    onSuccess = { profile ->
                        // Verifica a lista de especialidades e cria o novo estado
                        val showGeriatria = profile.specialties.contains("geriatria")
                        val showTraumato = profile.specialties.contains("traumatoortopedica")

                        _uiState.value = NewFileUiState(
                            showGeriatriaCard = showGeriatria,
                            showTraumatoCard = showTraumato
                        )
                    },
                    onFailure = {
                        // Se falhar, o estado padrão (false, false) será mantido
                        // Você pode adicionar um LiveData de erro aqui se quiser
                    }
                )
            }
            // Se o UID for nulo, também mantém o estado padrão (nada para mostrar)
        }
    }
}