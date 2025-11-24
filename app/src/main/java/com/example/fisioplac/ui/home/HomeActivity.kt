package com.example.fisioplac.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.ui.form_geriatrica.GeriatricaActivity
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityHomeBinding
import com.example.fisioplac.ui.auth.LoginActivity
import com.example.fisioplac.ui.newfile.NewFileActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    // Variável para armazenar a lista de especialidades para o BottomNav
    private var currentSpecialties: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. NÃO precisamos mais passar dados para o ViewModel.
        // O init dele já chama loadProfile().

        setupClickListeners()
        observeUiState() // Apenas observamos o estado
    }

    // --- CORREÇÃO ADICIONADA AQUI ---
    /**
     * O onResume() é chamado toda vez que a activity volta ao foco.
     * Isso garante que o item de menu "Home" seja selecionado
     * quando voltamos da NewFileActivity.
     */
    override fun onResume() {
        super.onResume()
        binding.bottomNavigationBar.selectedItemId = R.id.nav_home
    }
    // --- FIM DA CORREÇÃO ---

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->

            // 2. Observa o estado de carregamento
            setLoading(state.isLoading)

            // 3. Atualiza a UI
            binding.textViewDoctorName.text = state.doctorName
            binding.cardGeriatrica.visibility =
                if (state.showGeriatriaCard) View.VISIBLE else View.GONE
            binding.cardTraumatoOrtopedica.visibility =
                if (state.showTraumatoCard) View.VISIBLE else View.GONE

            // Armazena as especialidades para uso na navegação
            currentSpecialties = state.specialties

            // 4. Observa o estado de erro
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // Avisa o ViewModel que o erro foi mostrado
            }

            // 5. Observa o estado de logout
            if (state.isLoggedOut) {
                navigateToLogin()
            }
        }
    }

    /**
     * Função auxiliar para controlar a visibilidade do ProgressBar
     * e habilitar/desabilitar cliques (se necessário).
     */
    private fun setLoading(isLoading: Boolean) {
        // Você pode ter um ProgressBar no seu XML
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Exemplo de como desabilitar botões enquanto carrega
        binding.btnLogout.isEnabled = !isLoading
        binding.cardGeriatrica.isEnabled = !isLoading
        binding.cardTraumatoOrtopedica.isEnabled = !isLoading
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.onLogoutClicked() // A navegação agora é tratada pelo observeUiState
        }

        binding.cardGeriatrica.setOnClickListener {
            val intent = Intent(this, GeriatricaActivity::class.java)
            intent.putExtra("ESPECIALIDADE_SELECIONADA", "geriatria")
            startActivity(intent)
        }

        binding.cardTraumatoOrtopedica.setOnClickListener {
            Toast.makeText(this, "Ficha de Traumato-Ortopédica em desenvolvimento.", Toast.LENGTH_SHORT).show()
        }

        // --- LINHA MOVIDA DAQUI ---
        // binding.bottomNavigationBar.selectedItemId = R.id.nav_home

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new_file -> {
                    val intent = Intent(this, NewFileActivity::class.java)
                    // NOTA: O NewFileActivity foi refatorado e não precisa mais
                    // receber as especialidades via Intent. Ele busca sozinho.
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

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}