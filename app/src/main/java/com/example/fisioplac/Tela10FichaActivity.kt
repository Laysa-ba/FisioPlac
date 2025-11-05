package com.example.fisioplac

import android.content.Intent // Importa a classe para navegar entre telas
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fisioplac.databinding.ActivityTela10FichaBinding // Importa a classe de binding

class Tela10FichaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTela10FichaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTela10FichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Ação para o botão de voltar
        binding.backArrow.setOnClickListener {
            finish() // 'finish()' simplesmente fecha a tela atual e volta para a anterior
        }

        // Ação para o botão de avançar
        binding.btnAvancar.setOnClickListener {
            if (validateFields()) {
                // Se todos os campos estiverem válidos, navega para a próxima tela
                // TODO: Substitua 'ProximaTelaActivity::class.java' pela sua próxima tela
                // val intent = Intent(this, ProximaTelaActivity::class.java)
                // startActivity(intent)

                Toast.makeText(this, "Todos os campos preenchidos!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        // Criamos uma lista com TODOS os EditTexts que precisam ser validados
        val fieldsToValidate: List<EditText> = listOf(
            binding.etPa0, binding.etPa3, binding.etPa6,
            binding.etFc0, binding.etFc3, binding.etFc6,
            binding.etSat0, binding.etSat3, binding.etSat6,
            binding.etFr0, binding.etFr3, binding.etFr6,
            binding.etBorg0, binding.etBorg3, binding.etBorg6,
            binding.etDistanciaPercorrida, // Este vem do TextInputLayout
            binding.etDistanciaPredita     // Este também vem do TextInputLayout
        )

        // Verificamos se 'todos' os campos na lista não estão em branco
        val allFieldsValid = fieldsToValidate.all { it.text.isNotBlank() }

        // Retorna true se todos estiverem preenchidos, false se algum estiver vazio
        return allFieldsValid
    }
}