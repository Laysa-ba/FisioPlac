package com.example.fisioplac

import com.example.fisioplac.TOTAL_FICHA_STEPS

import android.content.Intent
import android.os.Bundle
import android.widget.Button // Você está usando 'Button', não 'MaterialButton' aqui
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Tela9FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var avancarButton: Button // Usando Button padrão

    // --- 1. totalPassosDaFicha REMOVIDO (usaremos a constante) ---
    // private val totalPassosDaFicha = 13

    // O passo atual está correto
    private val passoAtual = 9 // Esta é a 9ª etapa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela9_ficha)

        // 1. Inicializa os componentes da UI
        backButton = findViewById(R.id.btn_back)
        progressBar = findViewById(R.id.ficha_progress_bar)
        avancarButton = findViewById(R.id.btn_avancar) // ID do seu XML

        // --- 2. CHAMADA DA BARRA DE PROGRESSO ATUALIZADA ---
        updateProgressBar()

        // 3. Configura os cliques dos botões
        backButton.setOnClickListener {
            finish()
        }

        avancarButton.setOnClickListener {
            val intent = Intent(this, Tela10FichaActivity::class.java)
            startActivity(intent)
        }
    }

    // --- 3. FUNÇÃO updateProgressBar ATUALIZADA ---
    private fun updateProgressBar() {
        // Usa a constante global e a variável da classe
        progressBar.max = TOTAL_FICHA_STEPS
        progressBar.progress = passoAtual
    }
}