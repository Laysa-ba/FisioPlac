package com.example.fisioplac.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // 1. IMPORTANTE
import com.example.fisioplac.ui.auth.LoginActivity
import com.example.fisioplac.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // 2. Chame ANTES de super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 3. REMOVA a linha "setContentView(R.layout.activity_splash)"
        // Esta linha estava causando o crash!

        // 4. Observe o destino
        observeDestination()

        // 5. Mantenha a splash na tela até o ViewModel decidir o destino
        splashScreen.setKeepOnScreenCondition {
            viewModel.destination.value == null
        }
    }

    private fun observeDestination() {
        viewModel.destination.observe(this) { destination ->
            when (destination) {
                is SplashDestination.GoToHome -> navigateToHome(destination.doctorName)
                is SplashDestination.GoToLogin -> navigateToLogin()
                null -> { /* Esperando o ViewModel... */ }
            }
        }
    }

    private fun navigateToHome(doctorName: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("USER_NAME", doctorName)
        }
        startActivity(intent)
        finish() // Finaliza a SplashActivity
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a SplashActivity
    }
}