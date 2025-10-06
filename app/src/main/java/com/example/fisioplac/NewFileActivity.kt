package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // A função enableEdgeToEdge() foi removida pois pode interferir com
        // o padding manual que já existe no seu código.
        setContentView(R.layout.activity_new_file)

        // Referências aos componentes da UI
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        val cardGeriatrica: CardView = findViewById(R.id.card_geriatrica)
        val cardTraumatoOrtopedica: CardView = findViewById(R.id.card_traumato_ortopedica)

        // 1. Recebe a lista de especialidades enviada pela HomeActivity
        val specialties = intent.getStringArrayListExtra("ESPECIALIDADES_LISTA")

        // 2. Verifica quais cards devem ser mostrados
        if (specialties != null) {
            // Mostra o card de Geriatria APENAS SE a lista contiver "geriatria"
            if (specialties.contains("geriatria")) {
                cardGeriatrica.visibility = View.VISIBLE
            }

            // Mostra o card de Traumato-Ortopédica APENAS SE a lista contiver "traumatoortopedica"
            if (specialties.contains("traumatoortopedica")) {
                cardTraumatoOrtopedica.visibility = View.VISIBLE
            }
        } else {
            // Opcional: Mostra uma mensagem se a lista não for recebida
            Toast.makeText(this, "Não foi possível carregar as especialidades.", Toast.LENGTH_LONG).show()
        }

        // --- FIM DA NOVA LÓGICA ---

        // Define o item de Nova Ficha como selecionado no menu
        bottomNav.selectedItemId = R.id.nav_new_file

        // Ação para o CardView de Geriátrica
        cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            // IMPORTANTE: Envia a informação da especialidade para a próxima tela
            intent.putExtra("ESPECIALIDADE_SELECIONADA", "geriatria")
            startActivity(intent)
        }

        // Ação para o CardView de Traumato-Ortopédica
        cardTraumatoOrtopedica.setOnClickListener {
            // Ação futura
            Toast.makeText(this, "Ficha de Traumato-Ortopédica em desenvolvimento.", Toast.LENGTH_SHORT).show()
        }

        // Configura a navegação do BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Usar finish() aqui previne que o usuário empilhe várias telas
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}