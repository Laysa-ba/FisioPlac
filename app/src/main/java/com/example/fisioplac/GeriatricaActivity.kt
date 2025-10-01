package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class GeriatricaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_geriatrica)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val backButton: ImageButton = findViewById(R.id.btn_back)

        // 1. Define o item de Nova Ficha como selecionado (Laranja)
        bottomNav.selectedItemId = R.id.nav_new_file

        // 2. Ação do Botão de Voltar (Seta)
        backButton.setOnClickListener {
            // REDIRECIONA PARA A NEWFILEACTIVITY
            val intent = Intent(this, NewFileActivity::class.java)
            // Usamos FLAG_ACTIVITY_CLEAR_TOP para limpar as atividades acima da NewFileActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // 3. Configura a navegação do BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Vai para a Home
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_new_file -> true // Já está na Ficha, não faz nada
                R.id.nav_profile -> true // Implementação futura
                else -> false
            }
        }

        // Configuração de Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}