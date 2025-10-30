package com.example.fisioplac.ui.auth

import com.example.fisioplac.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fisioplac.ui.home.HomeActivity

// Recomendo renomear este arquivo/classe para LoginActivity
class LoginActivity : AppCompatActivity() {

    // Remove as referências diretas ao Firebase
    // private lateinit var auth: FirebaseAuth
    // private lateinit var db: FirebaseFirestore

    private lateinit var binding: ActivityLoginBinding // <-- MUDANÇA AQUI

    // 1. Obtenha a instância do ViewModel
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // <-- MUDANÇA AQUI
        setContentView(binding.root)

        // 2. A View "avisa" o ViewModel sobre eventos (clique)
        binding.buttonLogin.setOnClickListener {
            val cpf = binding.editTextCpf.text.toString().trim()
            val password = binding.editTextSenha.text.toString()

            // A Activity apenas delega a ação, ela não sabe como o login é feito
            viewModel.onLoginClicked(cpf, password)
        }

        // 3. A View "observa" o LiveData do ViewModel para reagir a mudanças
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            // 4. Reaja a cada mudança de estado
            when (state) {
                is LoginUiState.Idle -> {
                    setLoading(false)
                }
                is LoginUiState.Loading -> {
                    setLoading(true)
                }
                is LoginUiState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.onErrorMessageShown() // Avisa o ViewModel que o erro foi mostrado
                }
                is LoginUiState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    navigateToHome(state.doctorName)
                }
            }
        }
    }

    /**
     * Função auxiliar para controlar a visibilidade do ProgressBar
     * e habilitar/desabilitar campos e botão.
     */
    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
        binding.editTextCpf.isEnabled = !isLoading
        binding.editTextSenha.isEnabled = !isLoading
    }

    /**
     * Função de navegação. Permanece na Activity, pois é uma responsabilidade da View.
     */
    private fun navigateToHome(doctorName: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("USER_NAME", doctorName)
        startActivity(intent)
        finish() // Termina a LoginActivity
    }

    // As funções performLogin(), loadDoctorDataAndNavigate() e navigateToHome() antigas
    // foram TODAS removidas ou substituídas, pois essa lógica agora está
    // no ViewModel e no Repository.
}