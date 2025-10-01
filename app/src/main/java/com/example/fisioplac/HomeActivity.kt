package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Botão de Logout (Canto Superior Direito)
        val logoutButton: ImageButton = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            // Redireciona para a tela de Login
            val intent = Intent(this, MainActivity::class.java)
            // Flags para limpar a pilha de activities e evitar voltar para a Home
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 2. Acesso Rápido - Ficha Geriátrica
        val cardGeriatrica: CardView = findViewById(R.id.card_geriatrica)
        cardGeriatrica.setOnClickListener {
            // Redireciona para a tela específica da Ficha Geriátrica
            val intent = Intent(this, GeriatricaActivity::class.java)
            startActivity(intent)
        }

        // 3. Menu de Navegação Inferior
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)

        // **NOVA LINHA:** Define Home como Laranja (ativo)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_file -> {
                    // Vai para a tela de Nova Ficha
                    val intent = Intent(this, NewFileActivity::class.java)
                    startActivity(intent)
                    // Não chame 'finish()', para que a Home fique na pilha
                    true
                }
                R.id.nav_home -> true // Já está na Home
                R.id.nav_profile -> true // Implementação futura para Perfil
                else -> false
            }
        }
    }
}