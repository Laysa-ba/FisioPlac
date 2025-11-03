package com.example.fisioplac.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.repository.AuthRepository
import kotlinx.coroutines.launch

// 1. Data class para representar TODO o estado da tela Home
data class HomeUiState(
    val isLoading: Boolean = true,
    val doctorName: String = "Carregando...",
    val specialties: List<String> = emptyList(),
    val showGeriatriaCard: Boolean = false,
    val showTraumatoCard: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false // Sinal para a View navegar para o Login
)

class HomeViewModel : ViewModel() {

    private val repository = AuthRepository()

    // 2. LiveData privado e público
    private val _uiState = MutableLiveData<HomeUiState>(HomeUiState())
    val uiState: LiveData<HomeUiState> = _uiState

    // 3. Carrega o perfil assim que o ViewModel é criado
    init {
        loadProfile()
    }

    private fun loadProfile() {
        // Assegura que o estado de "carregando" está ativo
        _uiState.value = _uiState.value?.copy(isLoading = true)

        val uid = repository.getCurrentUid()
        if (uid == null) {
            // Se não tem UID, o usuário não está logado
            _uiState.value = _uiState.value?.copy(isLoggedOut = true, isLoading = false) // Pare de carregar
            return
        }

        // 4. Usa Coroutine para buscar os dados
        viewModelScope.launch {
            repository.fetchDoctorProfile(uid).fold(
                onSuccess = { profile ->
                    // 5. Sucesso: Atualiza o estado com os dados formatados
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        doctorName = "Dr(a). ${profile.name}", // Lógica de formatação
                        specialties = profile.specialties,
                        showGeriatriaCard = profile.specialties.contains("geriatria"),
                        showTraumatoCard = profile.specialties.contains("traumatoortopedica")
                    )
                },
                onFailure = { e ->
                    // 6. Erro: Atualiza o estado com a mensagem de erro
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar dados",
                        doctorName = "Erro de conexão"
                    )
                }
            )
        }
    }

    // 7. Evento chamado pela View (clique no botão de logout)
    fun onLogoutClicked() {
        repository.logout()
        _uiState.value = _uiState.value?.copy(isLoggedOut = true)
    }

    /**
     * Chamado pela View após o Toast de erro ser exibido,
     * para limpar a mensagem e evitar que ela apareça novamente.
     */
    fun onErrorMessageShown() {
        _uiState.value = _uiState.value?.copy(errorMessage = null)
    }
}