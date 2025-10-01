package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_file)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val cardGeriatrica: CardView = findViewById(R.id.card_geriatrica)
        val cardTraumatoOrtopedica: CardView = findViewById(R.id.card_traumato_ortopedica)

        // 1. Define o item de Nova Ficha como selecionado (Laranja)
        bottomNav.selectedItemId = R.id.nav_new_file

        // 2. Ação para o CardView Geriátrica
        cardGeriatrica.setOnClickListener {
            startActivity(Intent(this, GeriatricaActivity::class.java))
        }

        // 3. Ação para o CardView Traumato-Ortopédica
        cardTraumatoOrtopedica.setOnClickListener {
            //Por enquanto, não vai ter nada
        }

        // 4. Configura a navegação do BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Configuração de Insets (para Status Bar e Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}