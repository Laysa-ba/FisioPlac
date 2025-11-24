package com.example.fisioplac.ui.newfile // Pacote atualizado

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels // Importação para o ViewModel
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.ui.form_geriatrica.GeriatricaActivity // Importe do pacote raiz
import com.example.fisioplac.R // Importe o R do pacote raiz
import com.example.fisioplac.databinding.ActivityNewFileBinding // Importe o ViewBinding

class NewFileActivity : AppCompatActivity() {

    // 1. Configurar ViewBinding
    private lateinit var binding: ActivityNewFileBinding

    // 2. Obter o ViewModel
    private val viewModel: NewFileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do ViewBinding
        binding = ActivityNewFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. REMOVEMOS a lógica do Intent. Agora observamos o ViewModel.
        observeUiState()

        // --- Lógica de cliques e navegação (permanece na View) ---

        // Define o item de Nova Ficha como selecionado no menu
        binding.bottomNavigationBar.selectedItemId = R.id.nav_new_file

        // Ação para o CardView de Geriátrica
        binding.cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            intent.putExtra("ESPECIALIDADE_SELECIONADA", "geriatria")
            startActivity(intent)
        }

        // Ação para o CardView de Traumato-Ortopédica
        binding.cardTraumatoOrtopedica.setOnClickListener {
            Toast.makeText(this, "Ficha de Traumato-Ortopédica em desenvolvimento.", Toast.LENGTH_SHORT).show()
        }

        // Configura a navegação do BottomNavigationView
        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // Volta para a Home (que já está aberta)
                    true
                }
                R.id.nav_new_file -> true // Já estamos aqui
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Observa o LiveData do ViewModel e atualiza a UI.
     */
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            // Atualiza a visibilidade dos cards com base no estado
            binding.cardGeriatrica.visibility =
                if (state.showGeriatriaCard) View.VISIBLE else View.GONE

            binding.cardTraumatoOrtopedica.visibility =
                if (state.showTraumatoCard) View.VISIBLE else View.GONE
        }
    }
}