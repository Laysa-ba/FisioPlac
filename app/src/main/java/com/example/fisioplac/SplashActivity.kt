// SplashActivity.kt
package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    // Tempo que a splash screen ficará visível (ex: 2 segundos)
    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Usa um Handler para atrasar a transição
        Handler(Looper.getMainLooper()).postDelayed({
            // Cria um Intent para iniciar a MainActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finaliza a SplashActivity para que o usuário não possa voltar
            finish()
        }, SPLASH_TIME_OUT)
    }
}