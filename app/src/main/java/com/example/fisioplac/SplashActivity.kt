package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.ui.auth.LoginActivity // Importe o caminho NOVO
import com.example.fisioplac.ui.home.HomeActivity // Importe o caminho NOVO
import com.example.fisioplac.ui.splash.SplashDestination
import com.example.fisioplac.ui.splash.SplashViewModel

class SplashActivity : AppCompatActivity() {

    // 1. Obter o ViewModel
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // O tema já está sendo aplicado pelo Manifest,
        // mas podemos setar o layout se quisermos (opcional com o tema `Theme.App.Starting`)
        setContentView(R.layout.activity_splash)

        // 2. Observar o LiveData do ViewModel
        observeDestination()
    }

    private fun observeDestination() {
        viewModel.destination.observe(this) { destination ->
            // 3. Reagir ao destino decidido pelo ViewModel
            when (destination) {
                is SplashDestination.GoToHome -> navigateToHome(destination.doctorName)
                is SplashDestination.GoToLogin -> navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        // Usa o caminho correto para a LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a Splash
    }

    private fun navigateToHome(doctorName: String) {
        // Usa o caminho correto e passa o nome para a HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("USER_NAME", doctorName) // A HomeActivity espera por isso
        startActivity(intent)
        finish() // Finaliza a Splash
    }
}