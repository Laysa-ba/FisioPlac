package com.example.fisioplac

import com.example.fisioplac.TOTAL_FICHA_STEPS

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.databinding.ActivityTela12FichaBinding

class Tela12FichaActivity : AppCompatActivity() {

    // Declara o objeto de View Binding
    private lateinit var binding: ActivityTela12FichaBinding

    // --- 1. PASSO ATUAL DEFINIDO ---
    private val PASSO_ATUAL = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout usando View Binding
        binding = ActivityTela12FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- 2. LÓGICA DA BARRA DE PROGRESSO ADICIONADA ---
        binding.fichaProgressBar.max = TOTAL_FICHA_STEPS
        binding.fichaProgressBar.progress = PASSO_ATUAL
        // --- FIM DA LÓGICA DA BARRA ---

        // Configura a Seta de Voltar
        binding.btnBack.setOnClickListener {
            finish() // Fecha a tela atual e volta para a anterior
        }

        // Configura o Botão de Finalizar Teste
        binding.btnFinalizarTeste.setOnClickListener {
            // Pega os textos dos campos
            val diagnostico = binding.etDiagnostico.text.toString().trim()
            val objetivos = binding.etObjetivos.text.toString().trim()

            // Verificação simples para garantir que não estão vazios
            if (diagnostico.isEmpty() || objetivos.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Salvar os dados 'diagnostico' e 'objetivos' no Firebase

                // Exibe uma mensagem de sucesso
                Toast.makeText(this, "Ficha finalizada com sucesso!", Toast.LENGTH_LONG).show()

                // Cria um Intent para a HomeActivity (ou a tela principal do seu app)
                val intent = Intent(this, HomeActivity::class.java)

                // Estas 'flags' limpam o histórico de telas da ficha.
                // Ao clicar em "voltar" na Home, o app não voltará para a ficha.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
        }
    }
}