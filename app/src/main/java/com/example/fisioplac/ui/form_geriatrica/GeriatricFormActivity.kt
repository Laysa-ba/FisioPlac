package com.example.fisioplac.ui.form_geriatrica

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fisioplac.TOTAL_FICHA_STEPS
import com.example.fisioplac.R
import com.example.fisioplac.databinding.ActivityGeriatricFormBinding

class GeriatricFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeriatricFormBinding
    private val viewModel: GeriatricFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeriatricFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pacienteId = intent.getStringExtra("PACIENTE_ID")
        val pacienteNome = intent.getStringExtra("PACIENTE_NOME")
        if (pacienteId == null) {
            Toast.makeText(this, "Erro: Paciente ID nulo.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicia o ViewModel (apenas localmente)
        viewModel.startForm(pacienteId, pacienteNome)

        binding.fichaProgressBar.max = TOTAL_FICHA_STEPS
        binding.backArrow.setOnClickListener {
            viewModel.onBackClicked()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentStep.value?.let { step ->
            navigateToStep(step)
        }
    }

    private fun observeViewModel() {
        viewModel.currentStep.observe(this) { step ->
            binding.fichaProgressBar.progress = step
            navigateToStep(step)
        }

        viewModel.closeForm.observe(this) { shouldClose ->
            if (shouldClose) {
                finish()
            }
        }

        // *** NOVO OBSERVADOR ***
        // Observa o sucesso do salvamento
        viewModel.formSaveSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Ficha salva com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Fecha a Activity do formulário
            }
        }
    }

    private fun navigateToStep(step: Int) {
        val fragment: Fragment = when (step) {
            1 -> Step1Fragment()
            2 -> Step2Fragment()
            3 -> Step3Fragment()

            else -> Step1Fragment()
        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.id_fragment_container)
        if (currentFragment == null || currentFragment::class.java != fragment::class.java) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, fragment)
                .commit()
        }
    }
}