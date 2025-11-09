package com.example.fisioplac.ui.form_geriatrica

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityGeriatricaBinding // 1. Import do ViewBinding
import com.example.fisioplac.ui.home.HomeActivity

class GeriatricaActivity : AppCompatActivity() {

    // 2. ViewBinding CORRIGIDO
    private lateinit var binding: ActivityGeriatricaBinding
    private val viewModel: GeriatricaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Inflar layout com ViewBinding
        binding = ActivityGeriatricaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 4. Recebe a especialidade e informa o ViewModel
        val specialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (specialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.setSpecialty(specialty)

        // 5. Configura os Listeners
        setupListeners()

        // 6. Configura a navegação
        setupNavigation()

        // 7. OBSERVA o ViewModel
        observeUiState()
    }

    /**
     * Esta é a função principal da View. Ela "reage" às mudanças
     * de estado vindas do ViewModel.
     */
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->

            // Controla o Loading (idealmente, um ProgressBar)
            // binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.editTextCpf.isEnabled = !state.isLoading

            // Mostra o card se o paciente foi encontrado
            binding.cardPatientInfo.visibility = if (state.isPatientFound) View.VISIBLE else View.GONE

            // Preenche os dados do paciente se eles existirem
            state.patientProfile?.let { profile ->
                binding.textViewPatientName.text = profile.name
                binding.textViewBirthdate.text = profile.birthDate
                binding.textViewAge.text = profile.age
                binding.textViewSex.text = profile.sex
            }

            // Controla a seleção (cor de fundo) do card
            val cardColor = if (state.isPatientSelected) R.color.verde_claro else R.color.white
            binding.cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, cardColor))

            // Controla se o botão principal está habilitado
            binding.btnIniciarFicha.isEnabled = state.canInitiateFicha

            // Mostra mensagens de erro
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.onErrorMessageShown() // Avisa o ViewModel que a msg foi mostrada
            }
        }
    }

    private fun setupListeners() {
        // Botão de Voltar
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Lógica de busca no CPF (com debounce do ViewModel)
        binding.editTextCpf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onCpfChanged(s.toString())
            }
        })

        // Clique no Card do Paciente
        binding.cardPatientInfo.setOnClickListener {
            viewModel.onPatientCardClicked()
        }

        // Botão "Iniciar Ficha"
        binding.btnIniciarFicha.setOnClickListener {
            // Pega os dados do estado atual do ViewModel
            val patientId = viewModel.uiState.value?.foundPatientId
            val patientName = viewModel.uiState.value?.patientProfile?.name

            if (patientId != null && patientName != null) {

                // --- 8. ALTERAÇÃO SOLICITADA ---
                // Navega para a Tela 1 do formulário
                val intent = Intent(this, Tela1FichaActivity::class.java).apply {
                    putExtra("PACIENTE_ID", patientId)
                    putExtra("PACIENTE_NOME", patientName) // Passa o nome correto
                }
                startActivity(intent)
                // --- FIM DA ALTERAÇÃO ---

            } else {
                Toast.makeText(this, "Erro: ID ou Nome do paciente não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationBar.selectedItemId = R.id.nav_new_file

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_new_file -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Perfil clicado (implementação futura)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}