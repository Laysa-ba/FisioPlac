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
import com.example.fisioplac.databinding.ActivityGeriatricaBinding
import com.example.fisioplac.ui.home.HomeActivity

class GeriatricaActivity : AppCompatActivity() {

    // 1. Configurar ViewBinding
    private lateinit var binding: ActivityGeriatricaBinding

    // 2. Obter o ViewModel
    private val viewModel: GeriatricaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Inflar layout com ViewBinding
        binding = ActivityGeriatricaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 4. Recebe a especialidade e informa o ViewModel
        val selectedSpecialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (selectedSpecialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.setSpecialty(selectedSpecialty)

        // 5. Configura os listeners de UI
        setupClickListeners()

        // 6. Observa o estado do ViewModel
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            // Controla o ProgressBar (você pode adicionar um ao seu XML)
            // binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Controla a visibilidade do Card do Paciente
            binding.cardPatientInfo.visibility = if (state.isPatientFound) View.VISIBLE else View.GONE

            // Preenche os dados do paciente se ele for encontrado
            state.patientProfile?.let { profile ->
                binding.textViewPatientName.text = profile.name
                binding.textViewBirthdate.text = profile.birthDate
                binding.textViewAge.text = profile.age
                binding.textViewSex.text = profile.sex
            }

            // Controla a cor de seleção do Card
            val cardColor = if (state.isPatientSelected) R.color.verde_claro else R.color.white
            binding.cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, cardColor))

            // Controla o estado do botão "Iniciar Ficha"
            binding.btnIniciarFicha.isEnabled = state.canInitiateFicha

            // Mostra mensagens de erro
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                viewModel.onErrorMessageShown() // Avisa o ViewModel que a mensagem foi mostrada
            }
        }
    }

    private fun setupClickListeners() {
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
            val patientId = viewModel.uiState.value?.foundPatientId
            if (patientId != null) {
                // Navega para a Tela 1 do formulário
                val intent = Intent(this, Tela1FichaActivity::class.java).apply {
                    putExtra("PACIENTE_ID", patientId)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Erro: ID do paciente não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Navegação Inferior
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.bottomNavigationBar.selectedItemId = R.id.nav_new_file

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