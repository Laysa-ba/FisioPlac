package com.example.fisioplac

import com.example.fisioplac.TOTAL_FICHA_STEPS

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.databinding.ActivityTela3FichaBinding

class Tela3FichaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTela3FichaBinding

    // Define o passo atual
    private val PASSO_ATUAL = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela3FichaBinding.inflate(layoutInflater)
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
            val intent = Intent(this, Tela4FichaActivity::class.java)
            // TODO: Passar dados da ficha (se houver)
            startActivity(intent)
        }
    }
}