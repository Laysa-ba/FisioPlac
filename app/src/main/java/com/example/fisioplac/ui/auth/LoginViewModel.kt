package com.example.fisioplac.ui.auth // Ajuste o pacote se necessário

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.repository.AuthRepository
import kotlinx.coroutines.launch

// 1. Classe Selada para representar os estados da UI de forma robusta
sealed class LoginUiState {
    object Idle : LoginUiState() // Estado inicial ou resetado
    object Loading : LoginUiState() // Mostra o ProgressBar
    data class Success(val doctorName: String) : LoginUiState() // Sucesso, com o nome
    data class Error(val message: String) : LoginUiState() // Erro, com a mensagem
}

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableLiveData<LoginUiState>(LoginUiState.Idle)
    val uiState: LiveData<LoginUiState> = _uiState

    // 3. Verifica no início se o usuário já está logado
    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        if (repository.isUserLoggedIn()) {
            _uiState.value = LoginUiState.Loading
            viewModelScope.launch {
                val uid = repository.getCurrentUid()!!

                // **** AQUI ESTÁ A CORREÇÃO ****
                // Trocamos fetchDoctorName por fetchDoctorProfile
                repository.fetchDoctorProfile(uid).fold(
                    onSuccess = { profile ->
                        // E pegamos o nome do objeto 'profile'
                        _uiState.value = LoginUiState.Success(profile.name)
                    },
                    onFailure = { e ->
                        _uiState.value = LoginUiState.Error(e.message ?: "Erro ao buscar dados")
                    }
                )
            }
        }
    }

    // 4. Função chamada pela Activity quando o botão de login é clicado
    fun onLoginClicked(cpf: String, pass: String) {
        if (cpf.isBlank() || pass.isBlank()) {
            _uiState.value = LoginUiState.Error("Preencha o CPF e a senha.")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            // Esta função (loginAndFetchDoctorName) já estava correta e não mudou
            val result = repository.loginAndFetchDoctorName(cpf, pass)

            result.fold(
                onSuccess = { doctorName ->
                    _uiState.value = LoginUiState.Success(doctorName)
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                            "CPF ou senha inválidos."
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                            "Usuário não encontrado."
                        else ->
                            exception.message ?: "Falha na autenticação."
                    }
                    _uiState.value = LoginUiState.Error(errorMessage)
                }
            )
        }
    }

    /**
     * Chamado pela Activity após exibir uma mensagem de erro,
     * para redefinir o estado e permitir nova tentativa.
     */
    fun onErrorMessageShown() {
        _uiState.value = LoginUiState.Idle
    }
}