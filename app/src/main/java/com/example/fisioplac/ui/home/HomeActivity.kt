package com.example.fisioplac.ui.home // <-- MUDANÇA AQUI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels // Importante
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.GeriatricaActivity // <-- IMPORT ADICIONADO
import com.example.fisioplac.NewFileActivity // <-- IMPORT ADICIONADO
import com.example.fisioplac.R // <-- **** CORREÇÃO ADICIONADA AQUI ****
import com.example.fisioplac.databinding.ActivityHomeBinding // 1. Importar ViewBinding
import com.example.fisioplac.ui.auth.LoginActivity
// import com.example.fisioplac.ui.home.HomeViewModel // 2. Não é mais necessário, estão no mesmo pacote

class HomeActivity : AppCompatActivity() {

    // 3. Configurar ViewBinding
    private lateinit var binding: ActivityHomeBinding

    // 4. Obter o ViewModel
    private val viewModel: HomeViewModel by viewModels()

    // (Todas as referências de Firebase e estado foram removidas daqui)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 5. Inflar o layout com ViewBinding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 6. Configurar Listeners (enviando eventos para o ViewModel)
        binding.btnLogout.setOnClickListener {
            viewModel.onLogoutClicked()
        }

        // 7. Configurar Listeners (lógica de navegação que é da View)
        binding.cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            intent.putExtra("ESPECIALIDADE_SELECIONADA", "geriatria")
            startActivity(intent)
        }

        binding.cardTraumatoOrtopedica.setOnClickListener {
            Toast.makeText(this, "Ficha de Traumato-Ortopédica em desenvolvimento.", Toast.LENGTH_SHORT).show()
        }

        setupBottomNavigation()

        // 8. Observar o estado do ViewModel
        observeUiState()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationBar.selectedItemId = R.id.nav_home
        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_file -> {
                    // Pega a lista ATUAL do estado do ViewModel
                    val specialties = viewModel.uiState.value?.specialties

                    val intent = Intent(this, NewFileActivity::class.java)
                    // Converte para ArrayList (necessário pelo 'putStringArrayListExtra')
                    intent.putStringArrayListExtra("ESPECIALIDADES_LISTA", ArrayList(specialties ?: emptyList()))
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> true // Já estamos na Home
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->

            // 9. A View apenas REAGE ao estado

            // Atualiza o nome (já vem formatado do ViewModel)
            binding.textViewDoctorName.text = state.doctorName

            // Atualiza visibilidade dos cards
            binding.cardGeriatrica.visibility = if (state.showGeriatriaCard) View.VISIBLE else View.GONE
            binding.cardTraumatoOrtopedica.visibility = if (state.showTraumatoCard) View.VISIBLE else View.GONE

            // Mostra erros (apenas se houver um)
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // Avisa o ViewModel que o erro foi mostrado
            }

            // Lida com o sinal de logout
            if (state.isLoggedOut) {
                navigateToLogin()
            }
        }
    }

    /**
     * Função de navegação. Permanece na Activity, pois é uma responsabilidade da View.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // A função loadDoctorProfile() foi toda movida para o ViewModel/Repository.
    // A função performLogout() foi renomeada para navigateToLogin()
}