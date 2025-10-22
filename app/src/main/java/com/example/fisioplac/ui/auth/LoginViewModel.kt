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

    private val repository = AuthRepository() // (No futuro, isso será injetado)

    // 2. O LiveData privado que o ViewModel controla
    private val _uiState = MutableLiveData<LoginUiState>(LoginUiState.Idle)

    // O LiveData público que a Activity vai "observar"
    val uiState: LiveData<LoginUiState> = _uiState

    // 3. Verifica no início se o usuário já está logado
    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        if (repository.isUserLoggedIn()) {
            _uiState.value = LoginUiState.Loading // Já está logado, vamos buscar os dados
            viewModelScope.launch {
                val uid = repository.getCurrentUid()!! // Seguro, pois isUserLoggedIn() foi true
                repository.fetchDoctorName(uid).fold(
                    onSuccess = { name -> _uiState.value = LoginUiState.Success(name) },
                    onFailure = { e -> _uiState.value = LoginUiState.Error(e.message ?: "Erro ao buscar dados") }
                )
            }
        }
        // Se não estiver logado, o estado continua LoginUiState.Idle
    }

    // 4. Função chamada pela Activity quando o botão de login é clicado
    fun onLoginClicked(cpf: String, pass: String) {
        // Validação de UI fica no ViewModel
        if (cpf.isBlank() || pass.isBlank()) {
            _uiState.value = LoginUiState.Error("Preencha o CPF e a senha.")
            return
        }

        // Define o estado para Carregando
        _uiState.value = LoginUiState.Loading

        // Inicia uma Coroutine no escopo do ViewModel
        viewModelScope.launch {
            val result = repository.loginAndFetchDoctorName(cpf, pass)

            // O .fold é uma forma elegante de tratar Success e Failure do Result
            result.fold(
                onSuccess = { doctorName ->
                    _uiState.value = LoginUiState.Success(doctorName) // Sucesso!
                },
                onFailure = { exception ->
                    // Mapeia erros comuns para mensagens amigáveis
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