package com.example.fisioplac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Tela9FichaActivity : AppCompatActivity() {

    // Componentes da UI
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var avancarButton: Button

    private val totalPassosDaFicha = 13 // Total de passos da ficha (ajuste se necessário)
    private val passoAtual = 9 // Esta é a 9ª etapa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela9_ficha)

        // 1. Inicializa os componentes da UI
        backButton = findViewById(R.id.btn_back)
        progressBar = findViewById(R.id.ficha_progress_bar)
        avancarButton = findViewById(R.id.btn_avancar)

        // 2. Atualiza o progresso da barra
        updateProgressBar(passoAtual, totalPassosDaFicha)

        // 3. Configura os cliques dos botões
        backButton.setOnClickListener {
            finish()
        }

        avancarButton.setOnClickListener {
            val intent = Intent(this, Tela10FichaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateProgressBar(currentStep: Int, totalSteps: Int) {
        val progress = (currentStep * 100) / totalSteps
        progressBar.progress = progress
    }
}