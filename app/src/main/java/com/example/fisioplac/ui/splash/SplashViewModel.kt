package com.example.fisioplac.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fisioplac.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Classe Selada para definir os destinos de navegação
sealed class SplashDestination {
    // Para Home, passamos o nome (que a HomeActivity precisa)
    data class GoToHome(val doctorName: String) : SplashDestination()
    // Para Login, não passamos nada
    object GoToLogin : SplashDestination()
}

class SplashViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _destination = MutableLiveData<SplashDestination>()
    val destination: LiveData<SplashDestination> = _destination

    init {
        // Inicia a verificação assim que o ViewModel é criado
        decideNextScreen()
    }

    private fun decideNextScreen() {
        viewModelScope.launch {
            // Um pequeno delay para a splash ser visível (opcional, mas bom para UX)
            delay(1500) // 1.5 segundos

            if (repository.isUserLoggedIn()) {
                // Usuário está logado. Tentar buscar o nome.
                val uid = repository.getCurrentUid()!!
                repository.fetchDoctorProfile(uid).fold(
                    onSuccess = { profile ->
                        _destination.value = SplashDestination.GoToHome(profile.name)
                    },
                    onFailure = {
                        // Se falhar (ex: sem internet), mande para o Login
                        _destination.value = SplashDestination.GoToLogin
                    }
                )
            } else {
                // Usuário não está logado
                _destination.value = SplashDestination.GoToLogin
            }
        }
    }
}