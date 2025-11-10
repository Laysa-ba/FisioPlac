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
import com.example.fisioplac.databinding.ActivityGeriatricaBinding // Import do ViewBinding
import com.example.fisioplac.ui.home.HomeActivity

class GeriatricaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeriatricaBinding
    private val viewModel: GeriatricaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeriatricaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val specialty = intent.getStringExtra("ESPECIALIDADE_SELECIONADA")
        if (specialty == null) {
            Toast.makeText(this, "Erro: Especialidade não definida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.setSpecialty(specialty)

        setupListeners()
        setupNavigation()
        observeUiState()
    }

    private fun setupListeners() {
        binding.editTextCpf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onCpfChanged(s.toString())
            }
        })

        binding.cardPatientInfo.setOnClickListener {
            viewModel.onPatientCardClicked()
        }

        binding.btnIniciarFicha.setOnClickListener {
            val patientId = viewModel.uiState.value?.foundPatientId
            val patientName = binding.textViewPatientName.text.toString()

            // *** MUDANÇA PRINCIPAL AQUI ***
            // Inicia a nova Activity "Mãe" do formulário
            val intent = Intent(this, GeriatricFormActivity::class.java).apply {
                putExtra("PACIENTE_ID", patientId)
                putExtra("PACIENTE_NOME", patientName)
            }
            startActivity(intent)
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            // Controla o Loading
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.editTextCpf.isEnabled = !state.isLoading

            binding.cardPatientInfo.visibility = if (state.isPatientFound) View.VISIBLE else View.GONE

            state.patientProfile?.let { profile ->
                binding.textViewPatientName.text = profile.name
                binding.textViewBirthdate.text = profile.birthDate
                binding.textViewAge.text = profile.age
                binding.textViewSex.text = profile.sex
            }

            val cardColor = if (state.isPatientSelected) R.color.verde_claro else R.color.white
            binding.cardPatientInfo.setCardBackgroundColor(ContextCompat.getColor(this, cardColor))

            binding.btnIniciarFicha.isEnabled = state.canInitiateFicha

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.onErrorMessageShown()
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