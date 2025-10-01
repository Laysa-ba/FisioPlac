package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.HomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Encontra o botão de login pelo ID
        val loginButton: Button = findViewById(R.id.button_login)

        // Adiciona um listener de clique ao botão
        loginButton.setOnClickListener {
            // Cria um Intent para iniciar a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)

            // Inicia a nova Activity
            startActivity(intent)

            // Opcional: Finaliza a MainActivity para que o usuário não volte para ela ao pressionar o botão "voltar"
            finish()
        }
    }
}