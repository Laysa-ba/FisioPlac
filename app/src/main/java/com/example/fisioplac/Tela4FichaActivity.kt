package com.example.fisioplac

import com.example.fisioplac.TOTAL_FICHA_STEPS

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.databinding.ActivityTela4FichaBinding

class Tela4FichaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTela4FichaBinding

    // Define o passo atual
    private val PASSO_ATUAL = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela4FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a barra de progresso
        binding.fichaProgressBar.max = TOTAL_FICHA_STEPS
        binding.fichaProgressBar.progress = PASSO_ATUAL

        // Botão Voltar
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Botão Avançar
        binding.btnAvancar.setOnClickListener {
            val intent = Intent(this, Tela5FichaActivity::class.java)
            // TODO: Passar dados da ficha (se houver)
            startActivity(intent)
        }
    }
}